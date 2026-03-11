package org.gca.schoolms.dashboard;

import java.math.BigDecimal;

public record DashboardMetrics(
    long activeStudents,
    long storedRecords,
    BigDecimal outstandingBalance,
    long activeSections
) {
}
