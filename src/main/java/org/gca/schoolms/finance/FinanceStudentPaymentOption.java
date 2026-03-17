package org.gca.schoolms.finance;

public record FinanceStudentPaymentOption(
    Long id,
    Long familyAccountId,
    Long billingPayerProfileId,
    String billingPayerLabel,
    String label
) {
}
