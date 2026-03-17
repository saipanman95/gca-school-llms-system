package org.gca.schoolms.settings;

public record SchoolProfileView(
    String schoolName,
    String emailAddress,
    String phoneNumber,
    String mailingAddressLine1,
    String mailingAddressLine2,
    String mailingCity,
    String mailingState,
    String mailingPostalCode
) {
    public String mailingAddress() {
        String line2 = mailingAddressLine2 != null && !mailingAddressLine2.isBlank() ? ", " + mailingAddressLine2 : "";
        return mailingAddressLine1 + line2 + ", " + mailingCity + ", " + mailingState + " " + mailingPostalCode;
    }
}
