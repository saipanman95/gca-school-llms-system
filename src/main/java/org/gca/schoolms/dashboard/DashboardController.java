package org.gca.schoolms.dashboard;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/", "/dashboard"})
    public Object dashboard(Authentication authentication, Model model) {
        boolean guardianUser = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_PARENT_GUARDIAN"));
        if (guardianUser) {
            return new RedirectView("/portal/guardian");
        }
        model.addAttribute("metrics", dashboardService.loadMetrics());
        return "dashboard/index";
    }
}
