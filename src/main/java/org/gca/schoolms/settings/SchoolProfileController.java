package org.gca.schoolms.settings;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN')")
public class SchoolProfileController {

    private final SchoolProfileService schoolProfileService;

    public SchoolProfileController(SchoolProfileService schoolProfileService) {
        this.schoolProfileService = schoolProfileService;
    }

    @GetMapping("/admin/settings")
    public String settings(Model model) {
        model.addAttribute("profileForm", schoolProfileService.loadForm());
        return "admin/settings";
    }

    @PostMapping("/admin/settings")
    public String updateSettings(SchoolProfileForm profileForm, RedirectAttributes redirectAttributes) {
        schoolProfileService.update(profileForm);
        redirectAttributes.addFlashAttribute("message", "School profile updated.");
        return "redirect:/admin/settings";
    }
}
