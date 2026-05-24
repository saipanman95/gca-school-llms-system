package org.gca.schoolms.records;

import org.gca.schoolms.enrollment.EnrollmentDocumentRepository;
import org.gca.schoolms.enrollment.EnrollmentRequestRepository;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.enrollment.EnrollmentReviewService;
import org.gca.schoolms.enrollment.RegistrarReviewStatus;
import org.gca.schoolms.finance.StudentFeeRepository;
import org.gca.schoolms.integration.powerschool.PowerSchoolStudentRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class RecordsController {

    private final StudentRepository studentRepository;
    private final CampusRepository campusRepository;
    private final EnrollmentReviewService enrollmentReviewService;
    private final EnrollmentRequestRepository enrollmentRequestRepository;
    private final EnrollmentDocumentRepository enrollmentDocumentRepository;
    private final StudentFeeRepository studentFeeRepository;
    private final PowerSchoolStudentRepository powerSchoolStudentRepository;
    private final StudentGuardianStageService studentGuardianStageService;

    public RecordsController(StudentRepository studentRepository, CampusRepository campusRepository,
                             EnrollmentReviewService enrollmentReviewService,
                             EnrollmentRequestRepository enrollmentRequestRepository,
                             EnrollmentDocumentRepository enrollmentDocumentRepository,
                             StudentFeeRepository studentFeeRepository,
                             PowerSchoolStudentRepository powerSchoolStudentRepository,
                             StudentGuardianStageService studentGuardianStageService) {
        this.studentRepository = studentRepository;
        this.campusRepository = campusRepository;
        this.enrollmentReviewService = enrollmentReviewService;
        this.enrollmentRequestRepository = enrollmentRequestRepository;
        this.enrollmentDocumentRepository = enrollmentDocumentRepository;
        this.studentFeeRepository = studentFeeRepository;
        this.powerSchoolStudentRepository = powerSchoolStudentRepository;
        this.studentGuardianStageService = studentGuardianStageService;
    }

    @GetMapping("/records")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_STAFF','GUIDANCE_COUNSELOR')")
    public String recordsHome(Model model) {
        try {
            model.addAttribute("students", studentRepository.findTop10ByOrderByLastNameAscFirstNameAsc().stream()
                .map(student -> new RecordsStudentRow(
                    student.getId(),
                    student.getStudentNumber(),
                    student.getDisplayName(),
                    student.getCampus().getCode(),
                    student.getFamilyAccount().getAccountName(),
                    null,
                    null,
                    student.getStatus(),
                    student.getDateOfBirth(),
                    enrollmentRequestRepository.findTopByStudentOrderBySubmittedOnDesc(student)
                        .map(request -> request.getId())
                        .orElse(null)
                ))
                .toList());
        } catch (DataAccessException exception) {
            var importedStudents = powerSchoolStudentRepository.findTop10ByOrderByLastNameAscFirstNameAsc();
            var guardiansByStudentId = studentGuardianStageService.findByStudentIds(importedStudents.stream()
                .map(student -> student.getStudentId().longValue())
                .toList());
            model.addAttribute("students", importedStudents.stream()
                .map(student -> {
                    List<StudentGuardianStageRow> guardians = guardiansByStudentId.getOrDefault(
                        student.getStudentId().longValue(),
                        List.of()
                    );
                    return new RecordsStudentRow(
                        student.getStudentId().longValue(),
                        student.getStudentId().toString(),
                        buildPowerSchoolDisplayName(student.getFirstName(), student.getLastName()),
                        buildPowerSchoolCampusCode(student.getSchoolId()),
                        "Imported PowerSchool record",
                        studentGuardianStageService.summarizeForStudent(guardians),
                        guardians.size(),
                        student.getEnrollStatus() != null && student.getEnrollStatus() == 0 ? StudentStatus.ACTIVE : StudentStatus.INACTIVE,
                        null,
                        null
                    );
                })
                .toList());
        }
        model.addAttribute("campuses", campusRepository.findAllByOrderByNameAsc());
        try {
            model.addAttribute("enrollmentQueue", enrollmentReviewService.loadRegistrarQueue());
        } catch (DataAccessException exception) {
            model.addAttribute("enrollmentQueue", List.of());
        }
        try {
            model.addAttribute("importedGuardianPreview", studentGuardianStageService.findPreviewRows(20));
        } catch (DataAccessException exception) {
            model.addAttribute("importedGuardianPreview", List.of());
        }
        model.addAttribute("registrarStatuses", RegistrarReviewStatus.values());
        return "records/index";
    }

    private static String buildPowerSchoolDisplayName(String firstName, String lastName) {
        String safeFirstName = firstName == null ? "" : firstName.trim();
        String safeLastName = lastName == null ? "" : lastName.trim();
        return (safeFirstName + " " + safeLastName).trim();
    }

    private static String buildPowerSchoolCampusCode(Integer schoolId) {
        if (schoolId == null) {
            return "PS";
        }
        return "PS-" + schoolId;
    }

    @GetMapping("/records/enrollments/{requestId}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_STAFF','GUIDANCE_COUNSELOR')")
    public String enrollmentRecordDetail(@PathVariable Long requestId, Model model) {
        var request = enrollmentRequestRepository.findById(requestId).orElseThrow();
        model.addAttribute("recordDetail", new EnrollmentRecordDetailView(
            request,
            request.getStudentLanguages().stream().toList(),
            request.getEmergencyContacts().stream().toList(),
            request.getFinanceAuthorizations().stream().toList(),
            enrollmentDocumentRepository.findByEnrollmentRequestOrderByDateUploadedDesc(request),
            studentFeeRepository.findByEnrollmentRequestOrderByAssessedAtAscIdAsc(request).stream().findFirst().orElse(null),
            enrollmentReviewService.missingDocumentLabels(requestId)
        ));
        model.addAttribute("registrarStatuses", RegistrarReviewStatus.values());
        return "records/detail";
    }

    @PostMapping("/records/enrollment-review")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_STAFF')")
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
