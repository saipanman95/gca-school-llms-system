package org.gca.schoolms.reports;

import java.time.LocalDate;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_STAFF')")
public class ReportsController {

    private final ValedictorianSalutatorianReportService valedictorianSalutatorianReportService;

    public ReportsController(ValedictorianSalutatorianReportService valedictorianSalutatorianReportService) {
        this.valedictorianSalutatorianReportService = valedictorianSalutatorianReportService;
    }

    @GetMapping("/reports")
    public String reportsHome(Model model) {
        model.addAttribute("reportCategories", new String[]{
            "Certificate generation",
            "Valedictorian and salutatorian rankings",
            "Honor roll and academic award reporting",
            "Report card and transcript outputs"
        });
        return "reports/index";
    }

    @GetMapping("/reports/valedictorian-salutatorian")
    public String valedictorianSalutatorianReport(
        @RequestParam(required = false, defaultValue = "Q4") String quarter,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cutoffDate,
        @RequestParam(required = false) Long studentId,
        Model model
    ) {
        LocalDate effectiveCutoffDate = cutoffDate != null ? cutoffDate : LocalDate.now();
        model.addAttribute("reportTitle", "Academic Ranking Awards");
        model.addAttribute("reportSummary", "Graduation and promotion ranking report. Grade 12 and grade 5 cohorts continue to rank by cumulative numeric average, while grade 8 and K5 cohorts sort by cumulative GPA and label the top three students by highest numerical average. Award labels also surface CPA and CHR for students who maintain current-year Principal's List or Honor Roll grades across the selected evaluation window.");
        model.addAttribute("quarterOptions", List.of("Q1", "Q2", "Q3", "Q4"));
        model.addAttribute("selectedQuarter", quarter);
        model.addAttribute("selectedCutoffDate", effectiveCutoffDate);
        model.addAttribute("selectedStudentId", studentId);
        model.addAttribute("selectedStudentBreakdown", valedictorianSalutatorianReportService.loadStudentBreakdown(studentId, quarter, effectiveCutoffDate));
        model.addAttribute("cohorts", valedictorianSalutatorianReportService.loadReport(quarter, effectiveCutoffDate));
        return "reports/valedictorian-salutatorian";
    }
}
