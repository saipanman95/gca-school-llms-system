package org.gca.schoolms.records;

import java.time.LocalDate;

public record RecordsStudentRow(
    Long id,
    String studentNumber,
    String displayName,
    String campusCode,
    String familyAccountName,
    String importedGuardianSummary,
    Integer importedGuardianCount,
    StudentStatus status,
    LocalDate dateOfBirth,
    Long latestEnrollmentRequestId
) {
}
