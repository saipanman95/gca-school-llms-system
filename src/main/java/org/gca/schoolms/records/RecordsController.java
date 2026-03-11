package org.gca.schoolms.records;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_STAFF')")
public class RecordsController {

    private final StudentRepository studentRepository;

    public RecordsController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/records")
    public String recordsHome(Model model) {
        model.addAttribute("students", studentRepository.findTop10ByOrderByLastNameAscFirstNameAsc());
        return "records/index";
    }
}
