package org.gca.schoolms.certificates;

public enum HonorRollResult {
    PRINCIPAL("Principal's List"),
    A_HONOR("A Honor Roll"),
    B_HONOR("B Honor Roll"),
    NONE("None");

    private final String displayName;

    HonorRollResult(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
