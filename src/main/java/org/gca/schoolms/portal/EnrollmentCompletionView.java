package org.gca.schoolms.portal;

import java.util.List;

public record EnrollmentCompletionView(
    int completionPercentage,
    List<String> missingFields
) {
    public boolean readyForSubmission() {
        return completionPercentage >= 100;
    }
}
