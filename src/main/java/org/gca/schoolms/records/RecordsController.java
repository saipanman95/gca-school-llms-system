package org.gca.schoolms.records;

import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.enrollment.EnrollmentReviewService;
import org.gca.schoolms.enrollment.RegistrarReviewStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_STAFF')")
public class RecordsController {

    private final StudentRepository studentRepository;
    private final CampusRepository campusRepository;
    private final EnrollmentReviewService enrollmentReviewService;

    public RecordsController(StudentRepository studentRepository, CampusRepository campusRepository,
                             EnrollmentReviewService enrollmentReviewService) {
        this.studentRepository = studentRepository;
        this.campusRepository = campusRepository;
        this.enrollmentReviewService = enrollmentReviewService;
    }

    @GetMapping("/records")
    public String recordsHome(Model model) {
        model.addAttribute("students", studentRepository.findTop10ByOrderByLastNameAscFirstNameAsc());
        model.addAttribute("campuses", campusRepository.findAllByOrderByNameAsc());
        model.addAttribute("enrollmentQueue", enrollmentReviewService.loadRegistrarQueue());
        model.addAttribute("registrarStatuses", RegistrarReviewStatus.values());
        return "records/index";
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
        return "redirect:/records";
    }
}
