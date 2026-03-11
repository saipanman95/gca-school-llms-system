package org.gca.schoolms.records;

import org.gca.schoolms.organization.CampusRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_STAFF')")
public class RecordsController {

    private final StudentRepository studentRepository;
    private final CampusRepository campusRepository;

    public RecordsController(StudentRepository studentRepository, CampusRepository campusRepository) {
        this.studentRepository = studentRepository;
        this.campusRepository = campusRepository;
    }

    @GetMapping("/records")
    public String recordsHome(Model model) {
        model.addAttribute("students", studentRepository.findTop10ByOrderByLastNameAscFirstNameAsc());
        model.addAttribute("campuses", campusRepository.findAllByOrderByNameAsc());
        return "records/index";
    }
}
