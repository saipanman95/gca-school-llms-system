package org.gca.schoolms.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record GraduationRankingCourseBreakdown(
    String courseName,
    BigDecimal percent,
    String gradeScaleName,
    BigDecimal gpa
) {

    public String percentDisplay() {
        return percent == null ? "-" : percent.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public String gpaDisplay() {
        return gpa == null ? "-" : gpa.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
