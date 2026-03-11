package org.gca.schoolms.academics;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_STAFF')")
public class AcademicsController {

    private final SectionRepository sectionRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    public AcademicsController(SectionRepository sectionRepository, AttendanceRecordRepository attendanceRecordRepository) {
        this.sectionRepository = sectionRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    @GetMapping("/academics")
    public String academicsHome(Model model) {
        model.addAttribute("sections", sectionRepository.findTop10ByOrderByTermNameDescCourseCodeAsc());
        model.addAttribute("attendanceRecords", attendanceRecordRepository.findTop10ByOrderByAttendanceDateDesc());
        return "academics/index";
    }
}
