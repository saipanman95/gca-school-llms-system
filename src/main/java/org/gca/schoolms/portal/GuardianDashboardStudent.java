package org.gca.schoolms.portal;

public record GuardianDashboardStudent(
    Long id,
    String displayName,
    String gradeLevel,
    String campusCode,
    String status
) {
}
