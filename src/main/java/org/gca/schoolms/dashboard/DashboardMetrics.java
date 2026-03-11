package org.gca.schoolms.dashboard;

import java.math.BigDecimal;

public record DashboardMetrics(
    long activeCampuses,
    long activeStudents,
    long storedRecords,
    BigDecimal outstandingBalance,
    long activeSections,
    long openInvoices,
    long absencesToday
) {
}
