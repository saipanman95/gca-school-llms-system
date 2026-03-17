package org.gca.schoolms.finance;

public record FinanceStudentPaymentOption(
    Long id,
    Long familyAccountId,
    String label
) {
}
