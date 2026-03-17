package org.gca.schoolms.records;

import org.gca.schoolms.enrollment.EnrollmentDocumentRepository;
import org.gca.schoolms.enrollment.EnrollmentRequestRepository;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.enrollment.EnrollmentReviewService;
import org.gca.schoolms.enrollment.RegistrarReviewStatus;
import org.gca.schoolms.finance.StudentFeeRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_STAFF')")
public class RecordsController {

    private final StudentRepository studentRepository;
    private final CampusRepository campusRepository;
    private final EnrollmentReviewService enrollmentReviewService;
    private final EnrollmentRequestRepository enrollmentRequestRepository;
    private final EnrollmentDocumentRepository enrollmentDocumentRepository;
    private final StudentFeeRepository studentFeeRepository;

    public RecordsController(StudentRepository studentRepository, CampusRepository campusRepository,
                             EnrollmentReviewService enrollmentReviewService,
                             EnrollmentRequestRepository enrollmentRequestRepository,
                             EnrollmentDocumentRepository enrollmentDocumentRepository,
                             StudentFeeRepository studentFeeRepository) {
        this.studentRepository = studentRepository;
        this.campusRepository = campusRepository;
        this.enrollmentReviewService = enrollmentReviewService;
        this.enrollmentRequestRepository = enrollmentRequestRepository;
        this.enrollmentDocumentRepository = enrollmentDocumentRepository;
        this.studentFeeRepository = studentFeeRepository;
    }

    @GetMapping("/records")
    public String recordsHome(Model model) {
        model.addAttribute("students", studentRepository.findTop10ByOrderByLastNameAscFirstNameAsc().stream()
            .map(student -> new RecordsStudentRow(
                student.getId(),
                student.getStudentNumber(),
                student.getDisplayName(),
                student.getCampus().getCode(),
                student.getFamilyAccount().getAccountName(),
                student.getStatus(),
                student.getDateOfBirth(),
                enrollmentRequestRepository.findTopByStudentOrderBySubmittedOnDesc(student)
                    .map(request -> request.getId())
                    .orElse(null)
            ))
            .toList());
        model.addAttribute("campuses", campusRepository.findAllByOrderByNameAsc());
        model.addAttribute("enrollmentQueue", enrollmentReviewService.loadRegistrarQueue());
        model.addAttribute("registrarStatuses", RegistrarReviewStatus.values());
        return "records/index";
    }

    @GetMapping("/records/enrollments/{requestId}")
    @Transactional(readOnly = true)
    public String enrollmentRecordDetail(@PathVariable Long requestId, Model model) {
        var request = enrollmentRequestRepository.findById(requestId).orElseThrow();
        model.addAttribute("recordDetail", new EnrollmentRecordDetailView(
            request,
            request.getStudentLanguages().stream().toList(),
            request.getEmergencyContacts().stream().toList(),
            request.getFinanceAuthorizations().stream().toList(),
            enrollmentDocumentRepository.findByEnrollmentRequestOrderByDateUploadedDesc(request),
            studentFeeRepository.findByEnrollmentRequest(request).orElse(null),
            enrollmentReviewService.missingDocumentLabels(requestId)
        ));
        model.addAttribute("registrarStatuses", RegistrarReviewStatus.values());
        return "records/detail";
    }

    @PostMapping("/records/enrollment-review")
    public String updateEnrollmentReview(@RequestParam Long requestId,
                                         @RequestParam RegistrarReviewStatus reviewStatus,
                                         @RequestParam(required = false) String comment,
                                         RedirectAttributes redirectAttributes) {
        try {
            enrollmentReviewService.updateRegistrarReview(requestId, reviewStatus, comment);
            redirectAttributes.addFlashAttribute("message", "Registrar review updated.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/records/enrollments/" + requestId;
    }
}
