package org.gca.schoolms.settings;

import org.gca.schoolms.security.AppUserAdminService;
import org.gca.schoolms.security.AppUserForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN')")
public class SchoolProfileController {

    private final SchoolProfileService schoolProfileService;
    private final AppUserAdminService appUserAdminService;

    public SchoolProfileController(SchoolProfileService schoolProfileService, AppUserAdminService appUserAdminService) {
        this.schoolProfileService = schoolProfileService;
        this.appUserAdminService = appUserAdminService;
    }

    @GetMapping("/admin/settings")
    public String settings(@RequestParam(required = false) String editUsername, Model model) {
        model.addAttribute("profileForm", schoolProfileService.loadForm());
        model.addAttribute("appUsers", appUserAdminService.users());
        model.addAttribute("appRoles", appUserAdminService.roles());
        model.addAttribute("appUserForm", appUserAdminService.buildForm(editUsername));
        return "admin/settings";
    }

    @PostMapping("/admin/settings")
    public String updateSettings(SchoolProfileForm profileForm, RedirectAttributes redirectAttributes) {
        schoolProfileService.update(profileForm);
        redirectAttributes.addFlashAttribute("message", "School profile updated.");
        return "redirect:/admin/settings";
    }

    @PostMapping("/admin/settings/users")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public String saveUser(AppUserForm appUserForm, RedirectAttributes redirectAttributes) {
        try {
            appUserAdminService.saveUser(appUserForm);
            redirectAttributes.addFlashAttribute("message", "User account saved.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/admin/settings";
    }
}
