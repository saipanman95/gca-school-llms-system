package org.gca.schoolms.enrollment;

import java.util.List;
import org.gca.schoolms.records.GradeLevel;

public record EnrollmentReviewSnapshot(
    Long id,
    String studentDisplayName,
    String familyAccountName,
    String campusCode,
    EnrollmentRequestType requestType,
    String schoolYear,
    GradeLevel requestedGradeLevel,
    EnrollmentRequestStatus status,
    RegistrarReviewStatus registrarReviewStatus,
    String registrarComment,
    int completionPercentage,
    List<String> missingDocuments,
    String parentStatusLabel,
    FinanceReviewStatus financeReviewStatus,
    String financeComment,
    List<EnrollmentFinanceAuthorizationType> financeAuthorizationTypes,
    String financeAuthorizationSummary,
    boolean enrollmentFeePaid,
    String financeStatusLabel
) {
}
