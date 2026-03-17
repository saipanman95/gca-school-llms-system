package org.gca.schoolms.enrollment;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.gca.schoolms.portal.EnrollmentCompletionView;
import org.gca.schoolms.portal.GuardianPortalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentReviewService {

    private final EnrollmentRequestRepository enrollmentRequestRepository;
    private final GuardianPortalService guardianPortalService;

    public EnrollmentReviewService(EnrollmentRequestRepository enrollmentRequestRepository,
                                   GuardianPortalService guardianPortalService) {
        this.enrollmentRequestRepository = enrollmentRequestRepository;
        this.guardianPortalService = guardianPortalService;
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

    private EnrollmentReviewSnapshot toSnapshot(EnrollmentRequest request) {
        EnrollmentCompletionView completion = guardianPortalService.calculateCompletion(request);
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
            request.getFinanceStatusLabel()
        );
    }
}
