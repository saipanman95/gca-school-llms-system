package org.gca.schoolms.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PaymentReceiptView(
    Long paymentId,
    String receiptNumber,
    LocalDateTime paymentDate,
    String schoolName,
    String schoolEmailAddress,
    String schoolPhoneNumber,
    String schoolMailingAddress,
    String receivedByUserId,
    String payerFirstName,
    String payerMiddleName,
    String payerLastName,
    String payerBusinessName,
    String payerRecordId,
    String payerEmailAddress,
    String payerPhoneNumber,
    String payerAccountName,
    boolean anonymousToFamily,
    String paymentPurposeLabel,
    String targetLabel,
    String schoolProjectTypeName,
    String paymentMethodLabel,
    String referenceNumber,
    BigDecimal totalAmount,
    BigDecimal unappliedAmount,
    BigDecimal remainingBalance,
    String notes,
    List<PaymentReceiptAllocationView> allocations
) {
}
