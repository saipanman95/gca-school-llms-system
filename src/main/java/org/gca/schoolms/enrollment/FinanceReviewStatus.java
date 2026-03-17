package org.gca.schoolms.enrollment;

public enum FinanceReviewStatus {
    PENDING,
    BLOCKED,
    CONDITIONALLY_CLEARED,
    CLEARED;

    public String getLabel() {
        return switch (this) {
            case PENDING -> "Pending";
            case BLOCKED -> "Blocked";
            case CONDITIONALLY_CLEARED -> "Conditionally cleared";
            case CLEARED -> "Cleared";
        };
    }
}
