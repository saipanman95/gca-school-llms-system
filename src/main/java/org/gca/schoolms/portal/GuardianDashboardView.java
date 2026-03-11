package org.gca.schoolms.portal;

import java.math.BigDecimal;
import java.util.List;

public record GuardianDashboardView(
    String accountName,
    String primaryGuardianName,
    String spouseName,
    String billingRecipient,
    long enrolledStudentCount,
    BigDecimal outstandingBalance,
    List<GuardianDashboardStudent> students,
    List<GuardianEnrollmentActivityView> enrollmentRequests
) {
}
