package org.gca.schoolms.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRowView(
    String payerDisplayName,
    String familyAccountName,
    String paymentPurposeLabel,
    String targetDisplayName,
    String schoolProjectTypeName,
    String paymentMethodLabel,
    LocalDateTime paymentDate,
    String referenceNumber,
    BigDecimal totalAmount,
    BigDecimal unappliedAmount,
    String notes
) {
}
