package org.gca.schoolms.portal;

import java.math.BigDecimal;
import java.util.List;
import org.gca.schoolms.finance.PaymentRowView;
import org.gca.schoolms.finance.StudentFee;

public record GuardianFinanceView(
    BigDecimal outstandingBalance,
    String billingRecipient,
    List<StudentFee> fees,
    List<PaymentRowView> payments
) {
}
