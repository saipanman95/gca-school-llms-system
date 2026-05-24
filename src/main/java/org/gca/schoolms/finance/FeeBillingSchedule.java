package org.gca.schoolms.finance;

public enum FeeBillingSchedule {
    ONE_TIME("One-time"),
    MONTHLY("Monthly");

    private final String label;

    FeeBillingSchedule(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
