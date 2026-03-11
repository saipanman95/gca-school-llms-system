package org.gca.schoolms.academics;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_STAFF')")
public class AcademicsController {

    private final SectionRepository sectionRepository;

    public AcademicsController(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @GetMapping("/academics")
    public String academicsHome(Model model) {
        model.addAttribute("sections", sectionRepository.findTop10ByOrderByTermNameDescCourseCodeAsc());
        return "academics/index";
    }
}
