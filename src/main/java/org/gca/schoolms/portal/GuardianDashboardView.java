package org.gca.schoolms.portal;

import java.math.BigDecimal;
import java.util.List;
import org.gca.schoolms.enrollment.EnrollmentRequest;

public record GuardianDashboardView(
    String accountName,
    String primaryGuardianName,
    String spouseName,
    String billingRecipient,
    long enrolledStudentCount,
    BigDecimal outstandingBalance,
    List<GuardianDashboardStudent> students,
    List<EnrollmentRequest> enrollmentRequests
) {
}
