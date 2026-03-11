package org.gca.schoolms.portal;

import org.gca.schoolms.enrollment.EnrollmentRequestStatus;
import org.gca.schoolms.enrollment.EnrollmentRequestType;
import org.gca.schoolms.records.GradeLevel;

public record GuardianEnrollmentActivityView(
    Long id,
    String studentDisplayName,
    EnrollmentRequestType requestType,
    String schoolYear,
    GradeLevel requestedGradeLevel,
    EnrollmentRequestStatus status,
    String parentStatusLabel,
    int completionPercentage,
    boolean editable,
    boolean deletable
) {
}
