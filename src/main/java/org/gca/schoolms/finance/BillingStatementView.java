package org.gca.schoolms.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BillingStatementView(
    LocalDateTime generatedOn,
    String schoolName,
    String schoolPhoneNumber,
    String schoolEmailAddress,
    String schoolMailingAddress,
    String familyAccountNumber,
    String familyAccountName,
    String billingRecipientName,
    String billingRecipientEmail,
    String billingRecipientPhone,
    String mailingAddress,
    BigDecimal outstandingBalance,
    List<StudentFee> fees,
    List<BillingStatementPaymentRow> payments
) {
}
