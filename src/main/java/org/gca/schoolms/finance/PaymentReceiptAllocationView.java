package org.gca.schoolms.finance;

import java.math.BigDecimal;

public record PaymentReceiptAllocationView(
    String feeTypeName,
    String description,
    BigDecimal amountApplied,
    BigDecimal remainingBalance
) {
}
