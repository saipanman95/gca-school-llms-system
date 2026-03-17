package org.gca.schoolms.finance;

public enum StudentFeeStatus {
    ACTIVE("Active"),
    CANCELLED("Cancelled");

    private final String label;

    StudentFeeStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
