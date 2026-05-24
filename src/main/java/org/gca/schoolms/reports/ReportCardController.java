package org.gca.schoolms.reports;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.gca.schoolms.certificates.CertificatePdfService;
import org.gca.schoolms.certificates.CertificateSettingsService;
import org.gca.schoolms.certificates.CertificateGradeQueryService;
import org.gca.schoolms.certificates.CertificateLabelUtil;
import org.gca.schoolms.certificates.CertificateStudentSummary;
import org.gca.schoolms.settings.SchoolProfileService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_STAFF')")
public class ReportCardController {

    private final CertificateGradeQueryService certificateGradeQueryService;
    private final ReportCardService reportCardService;
    private final CertificatePdfService certificatePdfService;
    private final CertificateSettingsService certificateSettingsService;
    private final SchoolProfileService schoolProfileService;

    public ReportCardController(
        CertificateGradeQueryService certificateGradeQueryService,
        ReportCardService reportCardService,
        CertificatePdfService certificatePdfService,
        CertificateSettingsService certificateSettingsService,
        SchoolProfileService schoolProfileService
    ) {
        this.certificateGradeQueryService = certificateGradeQueryService;
        this.reportCardService = reportCardService;
        this.certificatePdfService = certificatePdfService;
        this.certificateSettingsService = certificateSettingsService;
        this.schoolProfileService = schoolProfileService;
    }

    @GetMapping("/reports/report-cards")
    public String reportCards(
        @RequestParam(required = false) Integer grade,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) String term,
        @RequestParam(required = false, name = "enrollStatus") List<Integer> enrollStatuses,
        @RequestParam(required = false) Long studentId,
        Model model
    ) {
        if (term == null || term.isBlank()) {
            term = "Q3";
        }
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        if (enrollStatuses == null || enrollStatuses.isEmpty()) {
            enrollStatuses = new ArrayList<>(List.of(0));
        }

        List<CertificateStudentSummary> students = certificateGradeQueryService.findStudents(grade, year, term, enrollStatuses);
        CertificateStudentSummary selectedStudent = null;
        if (studentId != null) {
            selectedStudent = students.stream()
                .filter(student -> student.id().equals(studentId))
                .findFirst()
                .orElseGet(() -> certificateGradeQueryService.findStudentById(studentId));
        } else if (!students.isEmpty()) {
            selectedStudent = students.getFirst();
        }

        populateDetailModel(model, selectedStudent, term, year);
        model.addAttribute("students", students);
        model.addAttribute("selectedTerm", term);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedGrade", grade);
        model.addAttribute("selectedEnrollStatuses", enrollStatuses);
        model.addAttribute("terms", List.of("Q1", "Q2", "Q3", "Q4"));
        model.addAttribute("years", buildYearOptions(year));
        model.addAttribute("gradeOptions", buildGradeOptions());
        return "reports/report-cards";
    }

    @GetMapping("/reports/report-cards/{studentId}/pdf")
    public ResponseEntity<byte[]> reportCardPdf(
        @PathVariable Long studentId,
        @RequestParam String term,
        @RequestParam Integer year
    ) {
        CertificateStudentSummary student = certificateGradeQueryService.findStudentById(studentId);
        ReportCardView reportCard = reportCardService.loadReportCard(student, term, year);
        var schoolProfile = schoolProfileService.loadView();
        var certificateSettings = certificateSettingsService.getSettings();

        Context context = new Context();
        context.setVariable("schoolName", schoolProfile.schoolName());
        context.setVariable("schoolAddress", schoolProfile.mailingAddress());
        context.setVariable("schoolPhone", schoolProfile.phoneNumber());
        context.setVariable("principalName", certificateSettings.principalName());
        context.setVariable("issueLocation", certificateSettings.issueLocation());
        context.setVariable("issueDate", certificateSettings.issueDate());
        context.setVariable("selectedTerm", term);
        context.setVariable("selectedYear", year);
        context.setVariable("student", student);
        context.setVariable("studentName", student.displayName());
        context.setVariable("gradeDisplay", CertificateLabelUtil.displayGrade(student.gradeLevel()));
        context.setVariable("schoolYearLabel", CertificateLabelUtil.schoolYearLabel(term, year));
        context.setVariable("reportCard", reportCard);
        context.setVariable("legacyContact", reportCard.legacyContact());

        byte[] pdf = certificatePdfService.generate("reports/report-card-pdf", context);
        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.inline()
                    .filename(reportCardFileName(student, term, year), StandardCharsets.UTF_8)
                    .build()
                    .toString()
            )
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    private void populateDetailModel(Model model, CertificateStudentSummary selectedStudent, String term, Integer year) {
        model.addAttribute("selectedStudent", selectedStudent);
        if (selectedStudent == null) {
            return;
        }
        model.addAttribute("reportCard", reportCardService.loadReportCard(selectedStudent, term, year));
    }

    private Map<Integer, String> buildGradeOptions() {
        Map<Integer, String> options = new LinkedHashMap<>();
        options.put(-1, "K4");
        options.put(0, "K5");
        for (int grade = 1; grade <= 12; grade++) {
            options.put(grade, String.valueOf(grade));
        }
        return options;
    }

    private List<Integer> buildYearOptions(int selectedYear) {
        List<Integer> years = new ArrayList<>();
        for (int year = selectedYear - 2; year <= selectedYear + 1; year++) {
            years.add(year);
        }
        return years;
    }

    private String reportCardFileName(CertificateStudentSummary student, String term, Integer year) {
        String safeName = student.displayName().replaceAll("[^A-Za-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return safeName + "-report-card-" + term + "-" + year + ".pdf";
    }
}
