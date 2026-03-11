package org.gca.schoolms.portal;

import jakarta.validation.Valid;
import java.util.Objects;
import org.gca.schoolms.enrollment.EnrollmentRequestType;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.finance.MaritalStatus;
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

    private static final String[] STUDENT_ETHNICITY_OPTIONS = {
        "Chamorro", "Carolinian", "Micronesian", "American", "Japanese",
        "Filipino", "Korean", "Chinese", "Other"
    };

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

    @GetMapping("/portal/guardian/profile")
    public String profileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (!model.containsAttribute("profileForm")) {
            model.addAttribute("profileForm", guardianPortalService.buildProfileForm(userDetails.getUsername()));
        }
        model.addAttribute("maritalStatuses", MaritalStatus.values());
        return "portal/guardian-profile";
    }

    @PostMapping("/portal/guardian/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @Valid @ModelAttribute("profileForm") GuardianProfileForm profileForm,
                                BindingResult bindingResult, Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("maritalStatuses", MaritalStatus.values());
            return "portal/guardian-profile";
        }
        guardianPortalService.updateGuardianProfile(userDetails.getUsername(), profileForm);
        redirectAttributes.addFlashAttribute("message", "Parent information updated.");
        return "redirect:/portal/guardian";
    }

    @GetMapping("/portal/guardian/finance")
    public String financeView(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("financeView", guardianPortalService.loadFinance(userDetails.getUsername()));
        return "portal/guardian-finance";
    }

    @GetMapping("/portal/guardian/enrollment")
    public String enrollmentForm(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam(name = "studentId", required = false) Long studentId,
                                 @RequestParam(name = "requestId", required = false) Long requestId,
                                 Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", guardianPortalService.buildEnrollmentForm(userDetails.getUsername(), studentId, requestId));
        }
        GuardianEnrollmentForm form = (GuardianEnrollmentForm) model.getAttribute("form");
        model.addAttribute("students", guardianPortalService.loadStudentsForGuardian(userDetails.getUsername()));
        model.addAttribute("campuses", campusRepository.findAllByOrderByNameAsc());
        model.addAttribute("gradeLevels", GradeLevel.values());
        model.addAttribute("maritalStatuses", MaritalStatus.values());
        model.addAttribute("requestTypes", EnrollmentRequestType.values());
        model.addAttribute("studentEthnicityOptions", STUDENT_ETHNICITY_OPTIONS);
        model.addAttribute("completion", guardianPortalService.calculateCompletion(form));
        model.addAttribute("prefill", guardianPortalService.buildEnrollmentPrefill(userDetails.getUsername(), form.getExistingStudentId()));
        return "portal/guardian-enrollment";
    }

    @GetMapping("/portal/guardian/enrollment/prefill")
    public String enrollmentPrefill(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestParam(name = "studentId", required = false) Long studentId,
                                    Model model) {
        model.addAttribute("prefill", guardianPortalService.buildEnrollmentPrefill(userDetails.getUsername(), studentId));
        model.addAttribute("form", guardianPortalService.buildEnrollmentForm(userDetails.getUsername(), studentId, null));
        model.addAttribute("campuses", campusRepository.findAllByOrderByNameAsc());
        model.addAttribute("gradeLevels", GradeLevel.values());
        model.addAttribute("maritalStatuses", MaritalStatus.values());
        model.addAttribute("studentEthnicityOptions", STUDENT_ETHNICITY_OPTIONS);
        return "portal/fragments/enrollment-prefill";
    }

    @GetMapping("/portal/guardian/enrollment/kindergarten")
    public String kindergartenReadinessSection(@ModelAttribute("form") GuardianEnrollmentForm form, Model model) {
        model.addAttribute("form", form);
        return "portal/fragments/enrollment-prefill :: kindergartenReadinessSection";
    }

    @PostMapping("/portal/guardian/enrollment")
    public String submitEnrollment(@AuthenticationPrincipal UserDetails userDetails,
                                   @ModelAttribute("form") GuardianEnrollmentForm form,
                                   BindingResult bindingResult, Model model,
                                   @RequestParam("submissionMode") String submissionMode,
                                   RedirectAttributes redirectAttributes) {
        EnrollmentCompletionView completion = guardianPortalService.calculateCompletion(form);
        boolean finalSubmit = Objects.equals(submissionMode, "SUBMIT");
        if (finalSubmit && !completion.readyForSubmission()) {
            model.addAttribute("students", guardianPortalService.loadStudentsForGuardian(userDetails.getUsername()));
            model.addAttribute("campuses", campusRepository.findAllByOrderByNameAsc());
            model.addAttribute("gradeLevels", GradeLevel.values());
            model.addAttribute("maritalStatuses", MaritalStatus.values());
            model.addAttribute("requestTypes", EnrollmentRequestType.values());
            model.addAttribute("studentEthnicityOptions", STUDENT_ETHNICITY_OPTIONS);
            model.addAttribute("completion", completion);
            model.addAttribute("prefill",
                guardianPortalService.buildEnrollmentPrefill(userDetails.getUsername(), form.getExistingStudentId()));
            return "portal/guardian-enrollment";
        }
        if (finalSubmit) {
            guardianPortalService.submitEnrollmentRequest(userDetails.getUsername(), form);
            redirectAttributes.addFlashAttribute("message", "Enrollment request submitted with student details and uploaded documents.");
        } else {
            guardianPortalService.saveEnrollmentDraft(userDetails.getUsername(), form);
            redirectAttributes.addFlashAttribute("message", "Enrollment draft saved.");
        }
        return "redirect:/portal/guardian";
    }

    @PostMapping("/portal/guardian/enrollment/delete")
    public String deleteDraft(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam("requestId") Long requestId,
                              RedirectAttributes redirectAttributes) {
        guardianPortalService.deleteEnrollmentDraft(userDetails.getUsername(), requestId);
        redirectAttributes.addFlashAttribute("message", "Enrollment draft deleted.");
        return "redirect:/portal/guardian";
    }
}
