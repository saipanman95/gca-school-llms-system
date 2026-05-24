package org.gca.schoolms.certificates;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CertificateCourseScaleService {

    private final JdbcTemplate jdbcTemplate;

    public CertificateCourseScaleService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CertificateStudentCourseGrade> enrich(List<CertificateStudentCourseGrade> grades) {
        ScaleRuleLookup lookup = loadScaleRuleLookup();
        List<CertificateStudentCourseGrade> result = new ArrayList<>(grades.size());
        for (CertificateStudentCourseGrade grade : grades) {
            String trackLabel = resolveTrackLabel(grade.gradeScaleName());
            BigDecimal computedGpa = lookup.resolveGpaPoints(grade.gradeScaleName(), grade.percent(), grade.gpaPoints());
            result.add(new CertificateStudentCourseGrade(
                grade.studentId(),
                grade.studentLName(),
                grade.studentFName(),
                grade.studentMName(),
                grade.gradeLevel(),
                grade.courseName(),
                grade.percent(),
                grade.letterGrade(),
                grade.excludeFromGpa(),
                grade.teacherId(),
                grade.teacherName(),
                grade.teacherLastName(),
                grade.teacherFirstName(),
                grade.gpaPoints(),
                grade.gradeScaleName(),
                trackLabel,
                computedGpa
            ));
        }
        return result;
    }

    private String resolveTrackLabel(String gradeScaleName) {
        if (gradeScaleName == null || gradeScaleName.isBlank()) {
            return "N/A";
        }
        return switch (gradeScaleName) {
            case "Advanced Placement Grades" -> "AP";
            case "Honors Class Grades" -> "Honors";
            case "Standard Grade Scale", "Default", "A, B, C, D, F" -> "Standard";
            default -> "N/A";
        };
    }

    private ScaleRuleLookup loadScaleRuleLookup() {
        Map<String, List<ScaleRule>> rulesByScaleName = new HashMap<>();
        jdbcTemplate.query(
            """
                SELECT
                    gs.name,
                    gi.cutoff_percent,
                    gi.points_value
                FROM powerschool_grade_scale_local gs
                JOIN powerschool_grade_scale_item_local gi
                  ON gi.grade_scale_id = gs.grade_scale_id
                WHERE COALESCE(gs.is_numeric, FALSE) = TRUE
                  AND COALESCE(gi.is_special, FALSE) = FALSE
                  AND gi.cutoff_percent IS NOT NULL
                  AND gi.points_value IS NOT NULL
                ORDER BY gs.name, gi.cutoff_percent DESC
                """,
            (ResultSet rs) -> {
                while (rs.next()) {
                    rulesByScaleName.computeIfAbsent(rs.getString("name"), ignored -> new ArrayList<>())
                        .add(new ScaleRule(rs.getBigDecimal("cutoff_percent"), rs.getBigDecimal("points_value")));
                }
                return null;
            }
        );
        return new ScaleRuleLookup(rulesByScaleName);
    }

    private record ScaleRule(BigDecimal cutoffPercent, BigDecimal pointsValue) {
    }

    private static final class ScaleRuleLookup {
        private final Map<String, List<ScaleRule>> rulesByScaleName;

        private ScaleRuleLookup(Map<String, List<ScaleRule>> rulesByScaleName) {
            this.rulesByScaleName = rulesByScaleName;
        }

        private BigDecimal resolveGpaPoints(String gradeScaleName, BigDecimal percent, BigDecimal fallbackGpaPoints) {
            if (percent != null && gradeScaleName != null) {
                List<ScaleRule> rules = rulesByScaleName.get(gradeScaleName);
                if (rules != null) {
                    for (ScaleRule rule : rules) {
                        if (percent.compareTo(rule.cutoffPercent()) >= 0) {
                            return rule.pointsValue().setScale(2, RoundingMode.HALF_UP);
                        }
                    }
                }
            }
            return fallbackGpaPoints == null ? null : fallbackGpaPoints.setScale(2, RoundingMode.HALF_UP);
        }
    }
}
