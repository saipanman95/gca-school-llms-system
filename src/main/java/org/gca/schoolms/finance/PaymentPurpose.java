package org.gca.schoolms.finance;

public enum PaymentPurpose {
    STUDENT_ACCOUNT,
    GIFT_TO_SCHOOL,
    EDUCATION_TAX_CREDIT;

    public String getLabel() {
        return switch (this) {
            case STUDENT_ACCOUNT -> "Pay toward student";
            case GIFT_TO_SCHOOL -> "Gift to school";
            case EDUCATION_TAX_CREDIT -> "ETC";
        };
    }
}
