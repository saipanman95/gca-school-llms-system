package org.gca.schoolms.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ReportCardMetricSummary(
    String label,
    BigDecimal gpa,
    BigDecimal numericAverage
) {

    public String gpaDisplay() {
        return display(gpa, 2);
    }

    public String numericAverageDisplay() {
        return display(numericAverage, 2);
    }

    private static String display(BigDecimal value, int scale) {
        if (value == null) {
            return "-";
        }
        return value.setScale(scale, RoundingMode.HALF_UP).toPlainString();
    }
}
