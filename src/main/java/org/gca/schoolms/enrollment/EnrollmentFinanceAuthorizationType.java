package org.gca.schoolms.enrollment;

public enum EnrollmentFinanceAuthorizationType {
    ENROLLMENT_FEE_PAID,
    PAYMENT_AGREEMENT_SIGNED,
    SCHOLARSHIP_GRANTED,
    PAYMENT_RECEIVED,
    CCDF_INHOUSE_GRANT,
    OTHER;

    public String getLabel() {
        return switch (this) {
            case ENROLLMENT_FEE_PAID -> "Enrollment fee paid";
            case PAYMENT_AGREEMENT_SIGNED -> "Payment agreement signed";
            case SCHOLARSHIP_GRANTED -> "Scholarship granted";
            case PAYMENT_RECEIVED -> "Payment received";
            case CCDF_INHOUSE_GRANT -> "CCDF in-house grant";
            case OTHER -> "Other authorization";
        };
    }
}
