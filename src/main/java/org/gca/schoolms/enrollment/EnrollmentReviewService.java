package org.gca.schoolms.enrollment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.gca.schoolms.finance.StudentFee;
import org.gca.schoolms.finance.StudentFeeRepository;
import org.gca.schoolms.portal.EnrollmentCompletionView;
import org.gca.schoolms.portal.GuardianPortalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentReviewService {

    private final EnrollmentRequestRepository enrollmentRequestRepository;
    private final GuardianPortalService guardianPortalService;
    private final StudentFeeRepository studentFeeRepository;

    public EnrollmentReviewService(EnrollmentRequestRepository enrollmentRequestRepository,
                                   GuardianPortalService guardianPortalService,
                                   StudentFeeRepository studentFeeRepository) {
        this.enrollmentRequestRepository = enrollmentRequestRepository;
        this.guardianPortalService = guardianPortalService;
        this.studentFeeRepository = studentFeeRepository;
    }

    @Transactional(readOnly = true)
    public List<EnrollmentReviewSnapshot> loadRegistrarQueue() {
        return enrollmentRequestRepository.findAllByOrderBySubmittedOnDescIdDesc().stream()
            .map(this::toSnapshot)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentReviewSnapshot> loadFinanceQueue() {
        return enrollmentRequestRepository.findByStatusNotOrderBySubmittedOnDescIdDesc(EnrollmentRequestStatus.DRAFT).stream()
            .filter(request -> request.getStatus() != EnrollmentRequestStatus.ENROLLED)
            .map(this::toSnapshot)
            .toList();
    }

    @Transactional
    public void updateRegistrarReview(Long requestId, RegistrarReviewStatus reviewStatus, String comment) {
        EnrollmentRequest request = enrollmentRequestRepository.findById(requestId).orElseThrow();
        if (reviewStatus == RegistrarReviewStatus.COMPLETE && !request.isFinanceSatisfied()) {
            throw new IllegalStateException("Finance clearance is required before registrar can mark this enrollment complete.");
        }
        request.updateRegistrarReview(reviewStatus, comment, LocalDate.now());
    }

    @Transactional
    public void updateFinanceReview(Long requestId,
                                    FinanceReviewStatus financeReviewStatus,
                                    String financeComment,
                                    List<EnrollmentFinanceAuthorizationType> authorizationTypes,
                                    String authorizationNote) {
        EnrollmentRequest request = enrollmentRequestRepository.findById(requestId).orElseThrow();
        request.replaceFinanceAuthorizations(authorizationTypes.stream()
            .distinct()
            .map(type -> new EnrollmentFinanceAuthorization(
                request,
                type,
                type == EnrollmentFinanceAuthorizationType.OTHER ? authorizationNote : null,
                LocalDate.now()))
            .toList());
        request.updateFinanceReview(financeReviewStatus, financeComment, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<String> missingDocumentLabels(Long requestId) {
        EnrollmentRequest request = enrollmentRequestRepository.findById(requestId).orElseThrow();
        return guardianPortalService.missingDocumentLabels(request);
    }

    private EnrollmentReviewSnapshot toSnapshot(EnrollmentRequest request) {
        EnrollmentCompletionView completion = guardianPortalService.calculateCompletion(request);
        List<StudentFee> enrollmentFees = studentFeeRepository.findByEnrollmentRequestOrderByAssessedAtAscIdAsc(request);
        BigDecimal outstandingAmount = enrollmentFees.stream()
            .map(StudentFee::getOutstandingAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        String enrollmentChargeSummary = enrollmentFees.isEmpty()
            ? "No enrollment-linked charges created yet."
            : enrollmentFees.size() + " charge(s) linked to request • outstanding " + outstandingAmount;
        return new EnrollmentReviewSnapshot(
            request.getId(),
            request.getStudentDisplayName(),
            request.getFamilyAccount().getAccountName(),
            request.getCampus().getCode(),
            request.getRequestType(),
            request.getSchoolYear(),
            request.getRequestedGradeLevel(),
            request.getStatus(),
            request.getRegistrarReviewStatus(),
            request.getRegistrarComment(),
            completion.completionPercentage(),
            completion.missingDocuments(),
            guardianPortalService.buildParentStatusLabel(request, completion),
            request.getFinanceReviewStatus(),
            request.getFinanceComment(),
            request.getFinanceAuthorizations().stream()
                .map(EnrollmentFinanceAuthorization::getAuthorizationType)
                .toList(),
            request.getFinanceAuthorizations().stream()
                .map(EnrollmentFinanceAuthorization::getAuthorizationType)
                .map(EnrollmentFinanceAuthorizationType::getLabel)
                .collect(Collectors.joining(", ")),
            request.hasFinanceAuthorization(EnrollmentFinanceAuthorizationType.ENROLLMENT_FEE_PAID),
            enrollmentChargeSummary,
            request.getFinanceStatusLabel()
        );
    }
}
