package org.gca.schoolms.finance;

import java.math.BigDecimal;

public record FinanceOutstandingAccountRow(
    Long familyAccountId,
    String accountNumber,
    String accountName,
    String primaryGuardianName,
    String primaryGuardianPhone,
    String primaryGuardianEmail,
    String campusCode,
    BigDecimal outstandingBalance,
    long openChargeCount
) {
}
