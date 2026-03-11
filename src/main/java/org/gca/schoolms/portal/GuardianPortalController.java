package org.gca.schoolms.portal;

import jakarta.validation.Valid;
import org.gca.schoolms.enrollment.EnrollmentRequestType;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.records.GradeLevel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasRole('PARENT_GUARDIAN')")
public class GuardianPortalController {

    private final GuardianPortalService guardianPortalService;
    private final CampusRepository campusRepository;

    public GuardianPortalController(GuardianPortalService guardianPortalService, CampusRepository campusRepository) {
        this.guardianPortalService = guardianPortalService;
        this.campusRepository = campusRepository;
    }

    @GetMapping("/portal/guardian")
    public String guardianDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("portal", guardianPortalService.loadDashboard(userDetails.getUsername()));
        return "portal/guardian-dashboard";
    }

    @GetMapping("/portal/guardian/enrollment")
    public String enrollmentForm(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam(name = "studentId", required = false) Long studentId,
                                 Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", guardianPortalService.buildEnrollmentForm(userDetails.getUsername(), studentId));
        }
        model.addAttribute("students", guardianPortalService.loadStudentsForGuardian(userDetails.getUsername()));
        model.addAttribute("campuses", campusRepository.findAllByOrderByNameAsc());
        model.addAttribute("gradeLevels", GradeLevel.values());
        model.addAttribute("requestTypes", EnrollmentRequestType.values());
        model.addAttribute("prefill", guardianPortalService.buildEnrollmentPrefill(userDetails.getUsername(), studentId));
        return "portal/guardian-enrollment";
    }

    @GetMapping("/portal/guardian/enrollment/prefill")
    public String enrollmentPrefill(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestParam(name = "studentId", required = false) Long studentId,
                                    Model model) {
        model.addAttribute("prefill", guardianPortalService.buildEnrollmentPrefill(userDetails.getUsername(), studentId));
        model.addAttribute("form", guardianPortalService.buildEnrollmentForm(userDetails.getUsername(), studentId));
        model.addAttribute("campuses", campusRepository.findAllByOrderByNameAsc());
        model.addAttribute("gradeLevels", GradeLevel.values());
        return "portal/fragments/enrollment-prefill";
    }

    @PostMapping("/portal/guardian/enrollment")
    public String submitEnrollment(@AuthenticationPrincipal UserDetails userDetails,
                                   @Valid @ModelAttribute("form") GuardianEnrollmentForm form,
                                   BindingResult bindingResult, Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("students", guardianPortalService.loadStudentsForGuardian(userDetails.getUsername()));
            model.addAttribute("campuses", campusRepository.findAllByOrderByNameAsc());
            model.addAttribute("gradeLevels", GradeLevel.values());
            model.addAttribute("requestTypes", EnrollmentRequestType.values());
            model.addAttribute("prefill",
                guardianPortalService.buildEnrollmentPrefill(userDetails.getUsername(), form.getExistingStudentId()));
            return "portal/guardian-enrollment";
        }
        guardianPortalService.submitEnrollmentRequest(userDetails.getUsername(), form);
        redirectAttributes.addFlashAttribute("message", "Enrollment request submitted.");
        return "redirect:/portal/guardian";
    }
}
