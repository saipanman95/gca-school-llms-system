package org.gca.schoolms.portal;

import java.math.BigDecimal;
import java.util.List;
import org.gca.schoolms.finance.Invoice;

public record GuardianFinanceView(
    BigDecimal outstandingBalance,
    String billingRecipient,
    List<Invoice> invoices
) {
}
