package org.gca.schoolms.finance;

import java.math.BigDecimal;

public record FinanceHomeView(
    BigDecimal outstandingBalance,
    long openChargeCount,
    long totalPaymentCount,
    long pendingClearanceCount,
    long outstandingFamilyCount,
    long feeTypeCount
) {
}
