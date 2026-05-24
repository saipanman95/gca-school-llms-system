package org.gca.schoolms.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.gca.schoolms.certificates.CertificateStudentSummary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportCardService {

    private final JdbcTemplate jdbcTemplate;
    private final StudentLegacyContactStageService studentLegacyContactStageService;

    public ReportCardService(
        JdbcTemplate jdbcTemplate,
        StudentLegacyContactStageService studentLegacyContactStageService
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.studentLegacyContactStageService = studentLegacyContactStageService;
    }

    public ReportCardView loadReportCard(CertificateStudentSummary student, String term, Integer year) {
        List<String> visibleTerms = visibleTerms(term);
        LocalDate schoolYearStart = LocalDate.of(year - 1, 8, 1);
        LocalDate schoolYearEnd = LocalDate.of(year, 8, 1);
        Map<String, CourseAccumulator> byCourse = new LinkedHashMap<>();
        String placeholders = String.join(", ", java.util.Collections.nCopies(visibleTerms.size(), "?"));

        String sql = """
            SELECT
                pg.final_grade_name,
                COALESCE(
                    sg.course_name,
                    CASE
                        WHEN s.course_number = 'CK4' THEN 'Citizenship'
                        WHEN s.course_number = 'PEK4' THEN 'Physical Education'
                        ELSE s.course_number
                    END
                ) AS course_name,
                COALESCE(
                    (
                        SELECT tcs.teacher_name
                        FROM teacher_course_stage tcs
                        WHERE (
                              tcs.course_number = s.course_number
                              OR (
                                  sg.course_name IS NOT NULL
                                  AND tcs.course_name = sg.course_name
                                  AND (
                                      tcs.grade_level = CASE
                                          WHEN st.grade_level = -1 THEN 'K4'
                                          WHEN st.grade_level = 0 THEN 'K5'
                                          WHEN st.grade_level >= 9 THEN 'HS'
                                          ELSE CAST(st.grade_level AS CHAR)
                                      END
                                      OR tcs.grade_level IS NULL
                                  )
                              )
                              OR (
                                  sg.course_name IS NOT NULL
                                  AND sg.course_name LIKE 'Beginning Japanese %%'
                                  AND tcs.course_name LIKE 'Beginning Japanese %%'
                              )
                          )
                        ORDER BY
                            CASE
                                WHEN tcs.school_year = '25-26' THEN 0
                                WHEN tcs.school_year IN ('SEM 1', 'SEM 2') THEN 1
                                ELSE 2
                            END,
                            CASE
                                WHEN sg.course_name IS NOT NULL AND tcs.course_name = sg.course_name THEN 0
                                WHEN sg.course_name IS NOT NULL
                                     AND sg.course_name LIKE 'Beginning Japanese %%'
                                     AND tcs.course_name LIKE 'Beginning Japanese %%' THEN 1
                                ELSE 2
                            END,
                            CASE WHEN tcs.course_number = s.course_number THEN 0 ELSE 1 END,
                            CASE
                                WHEN tcs.grade_level = CASE
                                    WHEN st.grade_level = -1 THEN 'K4'
                                    WHEN st.grade_level = 0 THEN 'K5'
                                    WHEN st.grade_level >= 9 THEN 'HS'
                                    ELSE CAST(st.grade_level AS CHAR)
                                END THEN 0
                                ELSE 1
                            END,
                            tcs.teacher_name
                        LIMIT 1
                    ),
                    (
                        SELECT tl.last_first
                        FROM section_teacher_local stl
                        JOIN teacher_local tl
                          ON tl.teacher_id = stl.teacher_id
                        WHERE stl.section_id = pg.section_id
                        ORDER BY
                            CASE WHEN stl.priority_order IS NULL THEN 1 ELSE 0 END,
                            stl.priority_order,
                            stl.teacher_id
                        LIMIT 1
                    ),
                    (
                        SELECT tl.last_first
                        FROM sections_local sl
                        JOIN teacher_local tl
                          ON tl.teacher_id = sl.teacher_id
                        WHERE sl.section_id = pg.section_id
                        LIMIT 1
                    ),
                    s.teacher_descr
                ) AS teacher_name,
                pg.grade_value,
                pg.citizenship
            FROM pg_final_grades_local pg
            JOIN students_local st
              ON st.student_id = pg.student_id
            LEFT JOIN sections_local s
              ON s.section_id = pg.section_id
            LEFT JOIN stored_grades_local sg
              ON sg.student_id = pg.student_id
             AND sg.store_code = pg.final_grade_name
             AND sg.course_department = s.course_number
            WHERE pg.student_id = ?
              AND pg.final_grade_name IN (%s)
              AND pg.end_date >= ?
              AND pg.end_date < ?
            ORDER BY course_name, pg.final_grade_name
            """.formatted(placeholders);

        List<Object> params = new ArrayList<>();
        params.add(student.id());
        params.addAll(visibleTerms);
        params.add(schoolYearStart);
        params.add(schoolYearEnd);

        jdbcTemplate.query(
            sql,
            params.toArray(),
            rs -> {
                String courseName = rs.getString("course_name");
                if (courseName == null || courseName.isBlank()) {
                    return;
                }
                CourseAccumulator accumulator = byCourse.computeIfAbsent(
                    courseName,
                    ignored -> new CourseAccumulator(courseName)
                );
                accumulator.accept(
                    rs.getString("final_grade_name"),
                    rs.getString("teacher_name"),
                    rs.getString("grade_value"),
                    rs.getString("citizenship")
                );
            }
        );

        List<ReportCardCourseRow> courses = byCourse.values().stream()
            .sorted(Comparator.comparing(CourseAccumulator::courseName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
            .map(CourseAccumulator::toView)
            .toList();

        List<ReportCardMetricSummary> quarterSummaries = loadQuarterSummaries(student.id(), visibleTerms, schoolYearStart, schoolYearEnd);
        ReportCardMetricSummary overallSummary = loadOverallSummary(student.id(), visibleTerms, schoolYearStart, schoolYearEnd);

        return new ReportCardView(
            student,
            visibleTerms,
            courses,
            studentLegacyContactStageService.findByStudentId(student.id()),
            quarterSummaries,
            overallSummary
        );
    }

    public List<String> visibleTerms(String selectedTerm) {
        List<String> terms = new ArrayList<>();
        terms.add("Q1");
        if (!"Q1".equals(selectedTerm)) {
            terms.add("Q2");
        }
        if ("Q3".equals(selectedTerm) || "Q4".equals(selectedTerm)) {
            terms.add("Q3");
        }
        if ("Q4".equals(selectedTerm)) {
            terms.add("Q4");
        }
        return terms;
    }

    private List<ReportCardMetricSummary> loadQuarterSummaries(
        Long studentId,
        List<String> visibleTerms,
        LocalDate schoolYearStart,
        LocalDate schoolYearEnd
    ) {
        if (visibleTerms.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(", ", java.util.Collections.nCopies(visibleTerms.size(), "?"));
        String sql = """
            SELECT
                pg.final_grade_name AS store_code,
                AVG(CASE WHEN COALESCE(sg.exclude_from_gpa, b'0') = b'0' THEN sg.gpa_points END) AS quarter_gpa,
                AVG(CASE WHEN COALESCE(sg.exclude_from_gpa, b'0') = b'0' THEN pg.percent END) AS numeric_average
            FROM pg_final_grades_local pg
            LEFT JOIN stored_grades_local sg
              ON sg.student_id = pg.student_id
             AND sg.store_code = pg.final_grade_name
             AND (
                 sg.section_id = pg.section_id
                 OR (
                     sg.section_id IS NULL
                     AND pg.section_id IS NULL
                 )
             )
            WHERE pg.student_id = ?
              AND pg.final_grade_name IN (%s)
              AND pg.end_date >= ?
              AND pg.end_date < ?
              AND NULLIF(TRIM(pg.grade_value), '') IS NOT NULL
            GROUP BY pg.final_grade_name
            """.formatted(placeholders);

        List<Object> params = new ArrayList<>();
        params.add(studentId);
        params.addAll(visibleTerms);
        params.add(schoolYearStart);
        params.add(schoolYearEnd);

        Map<String, ReportCardMetricSummary> byQuarter = new LinkedHashMap<>();
        jdbcTemplate.query(sql, params.toArray(), rs -> {
            String quarter = rs.getString("store_code");
            byQuarter.put(
                quarter,
                new ReportCardMetricSummary(
                    quarter,
                    rs.getBigDecimal("quarter_gpa"),
                    rs.getBigDecimal("numeric_average")
                )
            );
        });

        List<ReportCardMetricSummary> summaries = new ArrayList<>();
        for (String quarter : visibleTerms) {
            summaries.add(byQuarter.getOrDefault(quarter, new ReportCardMetricSummary(quarter, null, null)));
        }
        return summaries;
    }

    private ReportCardMetricSummary loadOverallSummary(
        Long studentId,
        List<String> visibleTerms,
        LocalDate schoolYearStart,
        LocalDate schoolYearEnd
    ) {
        if (visibleTerms.isEmpty()) {
            return new ReportCardMetricSummary("Overall for Year", null, null);
        }
        String placeholders = String.join(", ", java.util.Collections.nCopies(visibleTerms.size(), "?"));
        String sql = """
            SELECT
                AVG(CASE WHEN COALESCE(sg.exclude_from_gpa, b'0') = b'0' THEN sg.gpa_points END) AS overall_gpa,
                AVG(CASE WHEN COALESCE(sg.exclude_from_gpa, b'0') = b'0' THEN pg.percent END) AS overall_numeric_average
            FROM pg_final_grades_local pg
            LEFT JOIN stored_grades_local sg
              ON sg.student_id = pg.student_id
             AND sg.store_code = pg.final_grade_name
             AND (
                 sg.section_id = pg.section_id
                 OR (
                     sg.section_id IS NULL
                     AND pg.section_id IS NULL
                 )
             )
            WHERE pg.student_id = ?
              AND pg.final_grade_name IN (%s)
              AND pg.end_date >= ?
              AND pg.end_date < ?
              AND NULLIF(TRIM(pg.grade_value), '') IS NOT NULL
            """.formatted(placeholders);

        List<Object> params = new ArrayList<>();
        params.add(studentId);
        params.addAll(visibleTerms);
        params.add(schoolYearStart);
        params.add(schoolYearEnd);

        return jdbcTemplate.query(
            sql,
            params.toArray(),
            rs -> rs.next()
                ? new ReportCardMetricSummary(
                    "Overall for Year",
                    rs.getBigDecimal("overall_gpa"),
                    rs.getBigDecimal("overall_numeric_average")
                )
                : new ReportCardMetricSummary("Overall for Year", null, null)
        );
    }

    private static final class CourseAccumulator {
        private final String courseName;
        private String teacherName;
        private String q1Grade;
        private String q1Citizenship;
        private String q2Grade;
        private String q2Citizenship;
        private String q3Grade;
        private String q3Citizenship;
        private String q4Grade;
        private String q4Citizenship;

        private CourseAccumulator(String courseName) {
            this.courseName = courseName;
        }

        private String courseName() {
            return courseName;
        }

        private void accept(String quarter, String teacherDescr, String gradeValue, String citizenship) {
            if ((teacherName == null || teacherName.isBlank()) && teacherDescr != null && !teacherDescr.isBlank()) {
                teacherName = teacherDescr;
            }
            switch (quarter) {
                case "Q1" -> {
                    q1Grade = gradeValue;
                    q1Citizenship = citizenship;
                }
                case "Q2" -> {
                    q2Grade = gradeValue;
                    q2Citizenship = citizenship;
                }
                case "Q3" -> {
                    q3Grade = gradeValue;
                    q3Citizenship = citizenship;
                }
                case "Q4" -> {
                    q4Grade = gradeValue;
                    q4Citizenship = citizenship;
                }
                default -> {
                }
            }
        }

        private ReportCardCourseRow toView() {
            String resolvedTeacherName = teacherName;
            if (courseName != null && courseName.startsWith("Beginning Japanese")) {
                resolvedTeacherName = "Miller, Akiko";
            }
            return new ReportCardCourseRow(
                courseName,
                resolvedTeacherName,
                q1Grade,
                q1Citizenship,
                q2Grade,
                q2Citizenship,
                q3Grade,
                q3Citizenship,
                q4Grade,
                q4Citizenship
            );
        }
    }
}
