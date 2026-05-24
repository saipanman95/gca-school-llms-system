package org.gca.schoolms.certificates;

import java.time.LocalDate;

public record CertificateSettingsView(
    LocalDate issueDate,
    String issueLocation,
    String principalName
) {
}
