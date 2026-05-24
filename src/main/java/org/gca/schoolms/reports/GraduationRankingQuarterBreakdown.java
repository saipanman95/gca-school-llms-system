package org.gca.schoolms.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record GraduationRankingQuarterBreakdown(
    String quarter,
    BigDecimal quarterGpa,
    BigDecimal quarterNumericAverage,
    int includedCourseCount,
    List<GraduationRankingCourseBreakdown> courses
) {

    public String quarterGpaDisplay() {
        return quarterGpa == null ? "-" : quarterGpa.setScale(4, RoundingMode.HALF_UP).toPlainString();
    }

    public String quarterNumericAverageDisplay() {
        return quarterNumericAverage == null ? "-" : quarterNumericAverage.setScale(3, RoundingMode.HALF_UP).toPlainString();
    }
}
