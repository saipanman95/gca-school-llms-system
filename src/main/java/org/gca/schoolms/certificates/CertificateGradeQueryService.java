package org.gca.schoolms.certificates;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CertificateGradeQueryService {

    private final JdbcTemplate jdbcTemplate;

    public CertificateGradeQueryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CertificateStudentSummary> findStudents(
        Integer grade,
        Integer year,
        String term,
        List<Integer> enrollStatuses
    ) {
        if (grade == null) {
            return Stream.concat(
                    findActiveStudents(null, year, term, enrollStatuses).stream(),
                    findActiveStudents(-1, year, term, enrollStatuses).stream()
                )
                .collect(
                    LinkedHashMap<Long, CertificateStudentSummary>::new,
                    (studentsById, student) -> studentsById.put(student.id(), student),
                    LinkedHashMap::putAll
                )
                .values()
                .stream()
                .sorted(
                    Comparator.comparing(CertificateStudentSummary::lname, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(CertificateStudentSummary::fname, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(CertificateStudentSummary::mname, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(CertificateStudentSummary::id)
                )
                .toList();
        }
        return findActiveStudents(grade, year, term, enrollStatuses);
    }

    public CertificateStudentSummary findStudentById(Long studentId) {
        String sql = """
            SELECT
                student_id,
                last_name,
                first_name,
                middle_name,
                grade_level,
                enroll_status,
                FALSE AS exclude_from_gpa
            FROM students_local
            WHERE student_id = ?
            """;

        return jdbcTemplate.queryForObject(
            sql,
            new Object[]{studentId},
            (rs, rowNum) -> new CertificateStudentSummary(
                rs.getLong("student_id"),
                rs.getString("last_name"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getInt("grade_level"),
                rs.getInt("enroll_status"),
                rs.getBoolean("exclude_from_gpa")
            )
        );
    }

    public List<CertificateStudentCourseGrade> findStudentGrades(Long studentId, String term, Integer year) {
        CertificateStudentSummary student = findStudentById(studentId);
        LocalDate schoolYearEnd = LocalDate.of(year, 8, 1);
        if (student.gradeLevel() == -1) {
            return findK4StudentGrades(studentId, term, schoolYearEnd);
        }
        return findStandardStudentGrades(studentId, term, schoolYearEnd);
    }

    private List<CertificateStudentSummary> findActiveStudents(
        Integer grade,
        Integer year,
        String term,
        List<Integer> enrollStatuses
    ) {
        if (grade != null && grade == -1) {
            return findActiveK4Students(year, term, enrollStatuses);
        }

        StringBuilder sql = new StringBuilder("""
            SELECT
                s.student_id,
                s.last_name,
                s.first_name,
                s.middle_name,
                s.grade_level,
                s.enroll_status,
                MAX(CASE WHEN sg.exclude_from_gpa THEN 1 ELSE 0 END) AS exclude_from_gpa
            FROM students_local s
            JOIN stored_grades_local sg
              ON sg.student_id = s.student_id
            WHERE s.exclude_from_rank = FALSE
              AND sg.exclude_from_honor_roll = FALSE
              AND sg.percent IS NOT NULL
            """);

        List<Object> params = new ArrayList<>();
        if (enrollStatuses != null && !enrollStatuses.isEmpty()) {
            sql.append(" AND s.enroll_status IN (");
            sql.append(String.join(", ", java.util.Collections.nCopies(enrollStatuses.size(), "?")));
            sql.append(")");
            params.addAll(enrollStatuses);
        }
        if (term != null) {
            sql.append(" AND sg.store_code = ?");
            params.add(term);
        }
        if (year != null) {
            sql.append(" AND sg.date_stored >= ? ");
            sql.append(" AND sg.date_stored < ? ");
            params.add(LocalDate.of(year - 1, 8, 1));
            params.add(LocalDate.of(year, 8, 1));
        }
        if (grade != null) {
            sql.append(" AND s.grade_level = ? ");
            params.add(grade);
        }

        sql.append("""
            AND sg.course_name NOT IN (
                'Computer',
                'Art',
                'Basic Programming',
                'Japanese 01',
                'Music',
                'Citizenship',
                'Cooperative Education Program'
            )
            AND NOT (
                sg.course_name LIKE 'Elem Jap%'
                OR sg.course_name LIKE '%Elementary%Jap%'
            )
            AND NOT (
                sg.course_name = 'Physical Education'
                AND s.grade_level IN (0, 1, 2)
            )
            GROUP BY
                s.student_id,
                s.last_name,
                s.first_name,
                s.middle_name,
                s.grade_level,
                s.enroll_status
            ORDER BY s.last_name, s.first_name
            """);

        return jdbcTemplate.query(
            sql.toString(),
            params.toArray(),
            (rs, rowNum) -> new CertificateStudentSummary(
                rs.getLong("student_id"),
                rs.getString("last_name"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getInt("grade_level"),
                rs.getInt("enroll_status"),
                rs.getBoolean("exclude_from_gpa")
            )
        );
    }

    private List<CertificateStudentSummary> findActiveK4Students(
        Integer year,
        String term,
        List<Integer> enrollStatuses
    ) {
        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT
                s.student_id,
                s.last_name,
                s.first_name,
                s.middle_name,
                s.grade_level,
                s.enroll_status,
                FALSE AS exclude_from_gpa
            FROM students_local s
            JOIN pg_final_grades_local pg
              ON pg.student_id = s.student_id
            WHERE s.grade_level = -1
            """);

        List<Object> params = new ArrayList<>();
        if (enrollStatuses != null && !enrollStatuses.isEmpty()) {
            sql.append(" AND s.enroll_status IN (");
            sql.append(String.join(", ", java.util.Collections.nCopies(enrollStatuses.size(), "?")));
            sql.append(")");
            params.addAll(enrollStatuses);
        }
        if (term != null) {
            sql.append(" AND pg.final_grade_name = ?");
            params.add(term);
        }
        if (year != null) {
            sql.append(" AND pg.start_date >= ?");
            sql.append(" AND pg.start_date < ?");
            params.add(LocalDate.of(year - 1, 8, 1));
            params.add(LocalDate.of(year, 8, 1));
        }
        sql.append(" ORDER BY s.last_name, s.first_name");

        return jdbcTemplate.query(
            sql.toString(),
            params.toArray(),
            (rs, rowNum) -> new CertificateStudentSummary(
                rs.getLong("student_id"),
                rs.getString("last_name"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getInt("grade_level"),
                rs.getInt("enroll_status"),
                rs.getBoolean("exclude_from_gpa")
            )
        );
    }

    private List<CertificateStudentCourseGrade> findStandardStudentGrades(Long studentId, String term, LocalDate schoolYearEnd) {
        String sql = """
            SELECT
                s.student_id,
                s.last_name,
                s.first_name,
                s.middle_name,
                s.grade_level,
                sg.course_name,
                sg.percent,
                sg.letter_grade,
                sg.exclude_from_gpa,
                sg.gpa_points,
                sg.grade_scale_name,
                NULL AS staff_id,
                COALESCE(
                    (
                        SELECT tcs.teacher_name
                        FROM teacher_course_stage tcs
                        WHERE (
                              tcs.course_number = sg.course_department
                              OR (
                                  tcs.course_name = sg.course_name
                                  AND (
                                      tcs.grade_level = CASE
                                          WHEN s.grade_level = -1 THEN 'K4'
                                          WHEN s.grade_level = 0 THEN 'K5'
                                          WHEN s.grade_level >= 9 THEN 'HS'
                                          ELSE CAST(s.grade_level AS CHAR)
                                      END
                                      OR tcs.grade_level IS NULL
                                  )
                              )
                              OR (
                                  sg.course_name LIKE 'Beginning Japanese %'
                                  AND tcs.course_name LIKE 'Beginning Japanese %'
                              )
                          )
                        ORDER BY
                            CASE
                                WHEN tcs.school_year = '25-26' THEN 0
                                WHEN tcs.school_year IN ('SEM 1', 'SEM 2') THEN 1
                                ELSE 2
                            END,
                            CASE
                                WHEN tcs.course_name = sg.course_name THEN 0
                                WHEN sg.course_name LIKE 'Beginning Japanese %'
                                     AND tcs.course_name LIKE 'Beginning Japanese %' THEN 1
                                ELSE 2
                            END,
                            CASE WHEN tcs.course_number = sg.course_department THEN 0 ELSE 1 END,
                            CASE
                                WHEN tcs.grade_level = CASE
                                    WHEN s.grade_level = -1 THEN 'K4'
                                    WHEN s.grade_level = 0 THEN 'K5'
                                    WHEN s.grade_level >= 9 THEN 'HS'
                                    ELSE CAST(s.grade_level AS CHAR)
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
                        WHERE stl.section_id = sg.section_id
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
                        WHERE sl.section_id = sg.section_id
                        LIMIT 1
                    )
                ) AS teacher_name,
                COALESCE(
                    (
                        SELECT tcs.teacher_last_name
                        FROM teacher_course_stage tcs
                        WHERE (
                              tcs.course_number = sg.course_department
                              OR (
                                  tcs.course_name = sg.course_name
                                  AND (
                                      tcs.grade_level = CASE
                                          WHEN s.grade_level = -1 THEN 'K4'
                                          WHEN s.grade_level = 0 THEN 'K5'
                                          WHEN s.grade_level >= 9 THEN 'HS'
                                          ELSE CAST(s.grade_level AS CHAR)
                                      END
                                      OR tcs.grade_level IS NULL
                                  )
                              )
                              OR (
                                  sg.course_name LIKE 'Beginning Japanese %'
                                  AND tcs.course_name LIKE 'Beginning Japanese %'
                              )
                          )
                        ORDER BY
                            CASE
                                WHEN tcs.school_year = '25-26' THEN 0
                                WHEN tcs.school_year IN ('SEM 1', 'SEM 2') THEN 1
                                ELSE 2
                            END,
                            CASE
                                WHEN tcs.course_name = sg.course_name THEN 0
                                WHEN sg.course_name LIKE 'Beginning Japanese %'
                                     AND tcs.course_name LIKE 'Beginning Japanese %' THEN 1
                                ELSE 2
                            END,
                            CASE WHEN tcs.course_number = sg.course_department THEN 0 ELSE 1 END,
                            CASE
                                WHEN tcs.grade_level = CASE
                                    WHEN s.grade_level = -1 THEN 'K4'
                                    WHEN s.grade_level = 0 THEN 'K5'
                                    WHEN s.grade_level >= 9 THEN 'HS'
                                    ELSE CAST(s.grade_level AS CHAR)
                                END THEN 0
                                ELSE 1
                            END,
                            tcs.teacher_name
                        LIMIT 1
                    ),
                    (
                        SELECT tl.last_name
                        FROM section_teacher_local stl
                        JOIN teacher_local tl
                          ON tl.teacher_id = stl.teacher_id
                        WHERE stl.section_id = sg.section_id
                        ORDER BY
                            CASE WHEN stl.priority_order IS NULL THEN 1 ELSE 0 END,
                            stl.priority_order,
                            stl.teacher_id
                        LIMIT 1
                    ),
                    (
                        SELECT tl.last_name
                        FROM sections_local sl
                        JOIN teacher_local tl
                          ON tl.teacher_id = sl.teacher_id
                        WHERE sl.section_id = sg.section_id
                        LIMIT 1
                    )
                ) AS teacher_last_name,
                COALESCE(
                    (
                        SELECT tcs.teacher_first_name
                        FROM teacher_course_stage tcs
                        WHERE (
                              tcs.course_number = sg.course_department
                              OR (
                                  tcs.course_name = sg.course_name
                                  AND (
                                      tcs.grade_level = CASE
                                          WHEN s.grade_level = -1 THEN 'K4'
                                          WHEN s.grade_level = 0 THEN 'K5'
                                          WHEN s.grade_level >= 9 THEN 'HS'
                                          ELSE CAST(s.grade_level AS CHAR)
                                      END
                                      OR tcs.grade_level IS NULL
                                  )
                              )
                              OR (
                                  sg.course_name LIKE 'Beginning Japanese %'
                                  AND tcs.course_name LIKE 'Beginning Japanese %'
                              )
                          )
                        ORDER BY
                            CASE
                                WHEN tcs.school_year = '25-26' THEN 0
                                WHEN tcs.school_year IN ('SEM 1', 'SEM 2') THEN 1
                                ELSE 2
                            END,
                            CASE
                                WHEN tcs.course_name = sg.course_name THEN 0
                                WHEN sg.course_name LIKE 'Beginning Japanese %'
                                     AND tcs.course_name LIKE 'Beginning Japanese %' THEN 1
                                ELSE 2
                            END,
                            CASE WHEN tcs.course_number = sg.course_department THEN 0 ELSE 1 END,
                            CASE
                                WHEN tcs.grade_level = CASE
                                    WHEN s.grade_level = -1 THEN 'K4'
                                    WHEN s.grade_level = 0 THEN 'K5'
                                    WHEN s.grade_level >= 9 THEN 'HS'
                                    ELSE CAST(s.grade_level AS CHAR)
                                END THEN 0
                                ELSE 1
                            END,
                            tcs.teacher_name
                        LIMIT 1
                    ),
                    (
                        SELECT tl.first_name
                        FROM section_teacher_local stl
                        JOIN teacher_local tl
                          ON tl.teacher_id = stl.teacher_id
                        WHERE stl.section_id = sg.section_id
                        ORDER BY
                            CASE WHEN stl.priority_order IS NULL THEN 1 ELSE 0 END,
                            stl.priority_order,
                            stl.teacher_id
                        LIMIT 1
                    ),
                    (
                        SELECT tl.first_name
                        FROM sections_local sl
                        JOIN teacher_local tl
                          ON tl.teacher_id = sl.teacher_id
                        WHERE sl.section_id = sg.section_id
                        LIMIT 1
                    )
                ) AS teacher_first_name
            FROM students_local s
            JOIN stored_grades_local sg
              ON sg.student_id = s.student_id
            WHERE s.student_id = ?
              AND sg.store_code = ?
              AND sg.date_stored >= ?
              AND sg.date_stored < ?
              AND sg.percent IS NOT NULL
              AND sg.exclude_from_honor_roll = FALSE
            ORDER BY sg.course_name
            """;

        return jdbcTemplate.query(
            sql,
            new Object[]{
                studentId,
                term,
                schoolYearEnd.minusYears(1),
                schoolYearEnd
            },
            (rs, rowNum) -> new CertificateStudentCourseGrade(
                rs.getLong("student_id"),
                rs.getString("last_name"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getInt("grade_level"),
                rs.getString("course_name"),
                rs.getBigDecimal("percent"),
                rs.getString("letter_grade"),
                rs.getBoolean("exclude_from_gpa"),
                rs.getObject("staff_id", Long.class),
                rs.getString("teacher_name"),
                rs.getString("teacher_last_name"),
                rs.getString("teacher_first_name"),
                rs.getBigDecimal("gpa_points"),
                rs.getString("grade_scale_name"),
                null,
                null
            )
        );
    }

    private List<CertificateStudentCourseGrade> findK4StudentGrades(Long studentId, String term, LocalDate schoolYearEnd) {
        String sql = """
            SELECT
                s.student_id,
                s.last_name,
                s.first_name,
                s.middle_name,
                s.grade_level,
                CASE s2.course_number
                    WHEN 'CK4' THEN 'Citizenship'
                    WHEN 'PEK4' THEN 'Physical Education'
                    ELSE REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(s2.course_number,
                        'ArtK4', 'Art'),
                        'BibleK4', 'Bible'),
                        'MusicK4', 'Music'),
                        'NumbersK4', 'Numbers'),
                        'PhonicsK4', 'Phonics'),
                        'ReadingK4', 'Reading'),
                        'WritingK4', 'Writing'),
                        'PEK4', 'Physical Education')
                END AS course_name,
                pg.percent,
                pg.grade_value AS letter_grade,
                FALSE AS exclude_from_gpa,
                NULL AS gpa_points,
                'K4 Grade Scale' AS grade_scale_name,
                NULL AS staff_id,
                COALESCE(
                    (
                        SELECT tcs.teacher_name
                        FROM teacher_course_stage tcs
                        WHERE tcs.course_number = s2.course_number
                        ORDER BY
                            CASE
                                WHEN tcs.school_year = '25-26' THEN 0
                                WHEN tcs.school_year IN ('SEM 1', 'SEM 2') THEN 1
                                ELSE 2
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
                    s2.teacher_descr
                ) AS teacher_name,
                COALESCE(
                    (
                        SELECT tcs.teacher_last_name
                        FROM teacher_course_stage tcs
                        WHERE tcs.course_number = s2.course_number
                        ORDER BY
                            CASE
                                WHEN tcs.school_year = '25-26' THEN 0
                                WHEN tcs.school_year IN ('SEM 1', 'SEM 2') THEN 1
                                ELSE 2
                            END,
                            tcs.teacher_name
                        LIMIT 1
                    ),
                    (
                        SELECT tl.last_name
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
                        SELECT tl.last_name
                        FROM sections_local sl
                        JOIN teacher_local tl
                          ON tl.teacher_id = sl.teacher_id
                        WHERE sl.section_id = pg.section_id
                        LIMIT 1
                    ),
                    SUBSTRING_INDEX(s2.teacher_descr, ',', 1)
                ) AS teacher_last_name,
                COALESCE(
                    (
                        SELECT tcs.teacher_first_name
                        FROM teacher_course_stage tcs
                        WHERE tcs.course_number = s2.course_number
                        ORDER BY
                            CASE
                                WHEN tcs.school_year = '25-26' THEN 0
                                WHEN tcs.school_year IN ('SEM 1', 'SEM 2') THEN 1
                                ELSE 2
                            END,
                            tcs.teacher_name
                        LIMIT 1
                    ),
                    (
                        SELECT tl.first_name
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
                        SELECT tl.first_name
                        FROM sections_local sl
                        JOIN teacher_local tl
                          ON tl.teacher_id = sl.teacher_id
                        WHERE sl.section_id = pg.section_id
                        LIMIT 1
                    ),
                    NULLIF(TRIM(SUBSTRING_INDEX(s2.teacher_descr, ',', -1)), '')
                ) AS teacher_first_name
            FROM students_local s
            JOIN pg_final_grades_local pg
              ON pg.student_id = s.student_id
            JOIN sections_local s2
              ON s2.section_id = pg.section_id
            WHERE s.student_id = ?
              AND pg.final_grade_name = ?
              AND pg.start_date >= ?
              AND pg.start_date < ?
            ORDER BY course_name
            """;

        return jdbcTemplate.query(
            sql,
            new Object[]{
                studentId,
                term,
                schoolYearEnd.minusYears(1),
                schoolYearEnd
            },
            (rs, rowNum) -> new CertificateStudentCourseGrade(
                rs.getLong("student_id"),
                rs.getString("last_name"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getInt("grade_level"),
                rs.getString("course_name"),
                rs.getBigDecimal("percent"),
                rs.getString("letter_grade"),
                rs.getBoolean("exclude_from_gpa"),
                rs.getObject("staff_id", Long.class),
                rs.getString("teacher_name"),
                rs.getString("teacher_last_name"),
                rs.getString("teacher_first_name"),
                rs.getBigDecimal("gpa_points"),
                rs.getString("grade_scale_name"),
                null,
                null
            )
        );
    }
}
