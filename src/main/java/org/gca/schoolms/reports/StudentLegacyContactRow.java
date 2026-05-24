package org.gca.schoolms.reports;

public record StudentLegacyContactRow(
    Long studentId,
    Integer studentDcid,
    String studentFirstName,
    String studentLastName,
    String primaryContactName,
    String guardianStageEmail,
    String legacyGuardianEmail,
    String primaryContactEmail,
    String primaryContactPhone,
    String mailingStreet,
    String mailingCity,
    String mailingState,
    String mailingZip
) {

    public String mailingAddressSingleLine() {
        StringBuilder address = new StringBuilder();
        append(address, mailingStreet);
        if (mailingCity != null && !mailingCity.isBlank()) {
            if (!address.isEmpty()) {
                address.append(", ");
            }
            address.append(mailingCity.trim());
        }
        if (mailingState != null && !mailingState.isBlank()) {
            if (!address.isEmpty()) {
                address.append(", ");
            }
            address.append(mailingState.trim());
        }
        if (mailingZip != null && !mailingZip.isBlank()) {
            if (!address.isEmpty()) {
                address.append(' ');
            }
            address.append(mailingZip.trim());
        }
        return address.isEmpty() ? null : address.toString();
    }

    private static void append(StringBuilder builder, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (!builder.isEmpty()) {
            builder.append(", ");
        }
        builder.append(value.trim());
    }
}
