package org.gca.schoolms.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillingStatementPaymentRow(
    LocalDateTime paymentDate,
    String payerDisplayName,
    String paymentPurposeLabel,
    String targetDisplayName,
    String paymentMethodLabel,
    String referenceNumber,
    BigDecimal totalAmount,
    BigDecimal unappliedAmount,
    String notes
) {
}
