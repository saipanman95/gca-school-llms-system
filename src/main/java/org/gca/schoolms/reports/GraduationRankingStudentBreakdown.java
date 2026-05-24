package org.gca.schoolms.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record GraduationRankingStudentBreakdown(
    long studentId,
    String displayName,
    int currentGradeLevel,
    int startGrade,
    int endGrade,
    BigDecimal cumulativeGpa,
    BigDecimal cumulativeNumericAverage,
    BigDecimal minimumNumericGrade,
    List<GraduationRankingYearBreakdown> years
) {

    public String gradeWindowLabel() {
        if (startGrade == endGrade) {
            return gradeLabel(startGrade);
        }
        return gradeLabel(startGrade) + "-" + gradeLabel(endGrade);
    }

    public String cumulativeGpaDisplay() {
        return cumulativeGpa == null ? "-" : cumulativeGpa.setScale(6, RoundingMode.HALF_UP).toPlainString();
    }

    public String cumulativeNumericAverageDisplay() {
        return cumulativeNumericAverage == null ? "-" : cumulativeNumericAverage.setScale(4, RoundingMode.HALF_UP).toPlainString();
    }

    public String minimumNumericGradeDisplay() {
        return minimumNumericGrade == null ? "-" : minimumNumericGrade.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String gradeLabel(int gradeLevel) {
        if (gradeLevel == 0) {
            return "K5";
        }
        return gradeLevel + ordinalSuffix(gradeLevel);
    }

    private static String ordinalSuffix(int value) {
        if (value % 100 >= 11 && value % 100 <= 13) {
            return "th";
        }
        return switch (value % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }
}
