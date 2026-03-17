package org.gca.schoolms.finance;

public record PayerLookupOption(
    Long id,
    Long familyAccountId,
    String label
) {
}
