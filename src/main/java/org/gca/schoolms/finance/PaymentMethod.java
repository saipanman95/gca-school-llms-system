package org.gca.schoolms.finance;

public enum PaymentMethod {
    CASH,
    CHECK,
    CREDIT_CARD,
    SCHOLARSHIP_APPLIED,
    DISCOUNT_APPLIED,
    CCDF_INHOUSE_GRANT;

    public String getLabel() {
        return switch (this) {
            case CASH -> "Cash";
            case CHECK -> "Check";
            case CREDIT_CARD -> "Credit card";
            case SCHOLARSHIP_APPLIED -> "Scholarship applied";
            case DISCOUNT_APPLIED -> "Discount applied";
            case CCDF_INHOUSE_GRANT -> "CCDF in-house grant";
        };
    }
}
