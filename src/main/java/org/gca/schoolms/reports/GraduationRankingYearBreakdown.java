package org.gca.schoolms.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record GraduationRankingYearBreakdown(
    int gradeLevel,
    BigDecimal yearGpa,
    BigDecimal yearNumericAverage,
    List<GraduationRankingQuarterBreakdown> quarters
) {

    public String yearLabel() {
        if (gradeLevel == 0) {
            return "K5";
        }
        return gradeLevel + ordinalSuffix(gradeLevel) + " Grade";
    }

    public String yearGpaDisplay() {
        return yearGpa == null ? "-" : yearGpa.setScale(4, RoundingMode.HALF_UP).toPlainString();
    }

    public String yearNumericAverageDisplay() {
        return yearNumericAverage == null ? "-" : yearNumericAverage.setScale(3, RoundingMode.HALF_UP).toPlainString();
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
