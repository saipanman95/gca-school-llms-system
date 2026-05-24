package org.gca.schoolms.academics;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.math.BigDecimal;
import org.gca.schoolms.certificates.CertificateBatchExportService;
import org.gca.schoolms.certificates.CertificateCourseScaleService;
import org.gca.schoolms.certificates.CertificateGradeQueryService;
import org.gca.schoolms.certificates.CertificateHonorRollEvaluationService;
import org.gca.schoolms.certificates.CertificateLabelUtil;
import org.gca.schoolms.certificates.CertificateLetterGradeService;
import org.gca.schoolms.certificates.CertificatePdfService;
import org.gca.schoolms.certificates.CertificateSettingsService;
import org.gca.schoolms.certificates.CertificateTeacherResolverService;
import org.gca.schoolms.certificates.CertificateStudentSummary;
import org.gca.schoolms.certificates.HonorRollResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_STAFF')")
public class CertificateIntegrationController {

    private final String certificateGeneratorUrl;
    private final CertificateGradeQueryService certificateGradeQueryService;
    private final CertificateLetterGradeService certificateLetterGradeService;
    private final CertificateHonorRollEvaluationService certificateHonorRollEvaluationService;
    private final CertificateTeacherResolverService certificateTeacherResolverService;
    private final CertificateSettingsService certificateSettingsService;
    private final CertificatePdfService certificatePdfService;
    private final CertificateBatchExportService certificateBatchExportService;
    private final CertificateCourseScaleService certificateCourseScaleService;

    public CertificateIntegrationController(
        @Value("${app.integrations.certificate-generator-url:http://localhost:8081}") String certificateGeneratorUrl,
        CertificateGradeQueryService certificateGradeQueryService,
        CertificateLetterGradeService certificateLetterGradeService,
        CertificateHonorRollEvaluationService certificateHonorRollEvaluationService,
        CertificateTeacherResolverService certificateTeacherResolverService,
        CertificateSettingsService certificateSettingsService,
        CertificatePdfService certificatePdfService,
        CertificateBatchExportService certificateBatchExportService,
        CertificateCourseScaleService certificateCourseScaleService
    ) {
        this.certificateGeneratorUrl = certificateGeneratorUrl;
        this.certificateGradeQueryService = certificateGradeQueryService;
        this.certificateLetterGradeService = certificateLetterGradeService;
        this.certificateHonorRollEvaluationService = certificateHonorRollEvaluationService;
        this.certificateTeacherResolverService = certificateTeacherResolverService;
        this.certificateSettingsService = certificateSettingsService;
        this.certificatePdfService = certificatePdfService;
        this.certificateBatchExportService = certificateBatchExportService;
        this.certificateCourseScaleService = certificateCourseScaleService;
    }

    @GetMapping("/academics/certificates")
    public String certificateGenerator(
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
        Map<Long, HonorRollResult> honorResultsByStudentId = new LinkedHashMap<>();
        for (CertificateStudentSummary student : students) {
            HonorRollResult honorResult = certificateHonorRollEvaluationService.evaluate(
                certificateGradeQueryService.findStudentGrades(student.id(), term, year)
            );
            honorResultsByStudentId.put(student.id(), honorResult);
        }

        CertificateStudentSummary selectedStudent = null;
        if (studentId != null) {
            selectedStudent = students.stream()
                .filter(student -> student.id().equals(studentId))
                .findFirst()
                .orElseGet(() -> certificateGradeQueryService.findStudentById(studentId));
        } else if (!students.isEmpty()) {
            selectedStudent = students.getFirst();
        }

        populateDetailModel(model, selectedStudent, term, year, honorResultsByStudentId);
        model.addAttribute("students", students);
        model.addAttribute("honorResultsByStudentId", honorResultsByStudentId);
        model.addAttribute("selectedTerm", term);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedGrade", grade);
        model.addAttribute("selectedEnrollStatuses", enrollStatuses);
        model.addAttribute("terms", List.of("Q1", "Q2", "Q3", "Q4", "S1", "S2"));
        model.addAttribute("years", buildYearOptions(year));
        model.addAttribute("gradeOptions", buildGradeOptions());
        model.addAttribute("honorResults", HonorRollResult.values());
        model.addAttribute("certificateTypes", Stream.of(HonorRollResult.values())
            .filter(result -> result != HonorRollResult.NONE)
            .toList());
        model.addAttribute("certificateGeneratorUrl", certificateGeneratorUrl);
        return "academics/certificates";
    }

    @GetMapping("/academics/certificates/detail")
    public String certificateDetail(
        @RequestParam Long studentId,
        @RequestParam String term,
        @RequestParam Integer year,
        Model model
    ) {
        CertificateStudentSummary selectedStudent = certificateGradeQueryService.findStudentById(studentId);
        Map<Long, HonorRollResult> honorResultsByStudentId = new LinkedHashMap<>();
        honorResultsByStudentId.put(
            studentId,
            certificateHonorRollEvaluationService.evaluate(
                certificateGradeQueryService.findStudentGrades(studentId, term, year)
            )
        );

        populateDetailModel(model, selectedStudent, term, year, honorResultsByStudentId);
        model.addAttribute("selectedTerm", term);
        model.addAttribute("selectedYear", year);
        model.addAttribute("certificateTypes", Stream.of(HonorRollResult.values())
            .filter(result -> result != HonorRollResult.NONE)
            .toList());
        return "academics/fragments/certificate-detail :: detail";
    }

    @PostMapping("/academics/certificates/export")
    public ResponseEntity<byte[]> exportCertificates(
        @RequestParam(required = false) Integer grade,
        @RequestParam Integer year,
        @RequestParam String term,
        @RequestParam(required = false, name = "enrollStatus") List<Integer> enrollStatuses,
        @RequestParam(required = false, name = "honorTypes") List<HonorRollResult> honorTypes
    ) {
        if (enrollStatuses == null || enrollStatuses.isEmpty()) {
            enrollStatuses = new ArrayList<>(List.of(0));
        }

        byte[] zip = certificateBatchExportService.exportHonorCertificates(
            grade,
            year,
            term,
            enrollStatuses,
            honorTypes
        );

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=honor-certificates.zip")
            .contentType(MediaType.parseMediaType("application/zip"))
            .body(zip);
    }

    @GetMapping("/academics/certificates/{studentId}/honor-roll")
    public ResponseEntity<byte[]> generateHonorRollCertificate(
        @PathVariable Long studentId,
        @RequestParam String term,
        @RequestParam Integer year,
        @RequestParam(required = false) HonorRollResult override
    ) {
        CertificateStudentSummary student = certificateGradeQueryService.findStudentById(studentId);
        var grades = certificateGradeQueryService.findStudentGrades(studentId, term, year);
        HonorRollResult honorResult = override != null
            ? override
            : certificateHonorRollEvaluationService.evaluate(grades);

        String studentName = java.util.stream.Stream.of(student.fname(), student.mname(), student.lname())
            .filter(value -> value != null && !value.isBlank())
            .collect(Collectors.joining(" "));

        String homeroomTeacher = certificateTeacherResolverService.findHomeRoomTeacher("Citizenship", grades);
        var certificateSettings = certificateSettingsService.getSettings();

        Context context = new Context();
        context.setVariable("studentName", studentName);
        context.setVariable("studentFontSize", CertificateLabelUtil.determineStudentFontSize(studentName));
        context.setVariable("gradeLevel", CertificateLabelUtil.displayGrade(student.gradeLevel()));
        context.setVariable("termLabel", CertificateLabelUtil.termDisplay(term));
        context.setVariable("schoolYearLabel", CertificateLabelUtil.schoolYearLabel(term, year));
        context.setVariable("issueDateOrdinal", CertificateLabelUtil.dayOfMonthOrdinal(certificateSettings.issueDate()));
        context.setVariable("issueMonthYear", CertificateLabelUtil.monthYearLabel(certificateSettings.issueDate()));
        context.setVariable("issueLocation", certificateSettings.issueLocation());
        context.setVariable("principalName", certificateSettings.principalName());
        context.setVariable("homeroomTeacher", homeroomTeacher);
        context.setVariable("honorResult", honorResult);

        byte[] pdf = certificatePdfService.generate("academics/certificate-pdf", context);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=honor-certificate.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
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

    private void populateDetailModel(
        Model model,
        CertificateStudentSummary selectedStudent,
        String term,
        Integer year,
        Map<Long, HonorRollResult> honorResultsByStudentId
    ) {
        model.addAttribute("selectedStudent", selectedStudent);
        if (selectedStudent == null) {
            return;
        }

        var grades = certificateCourseScaleService.enrich(
            certificateLetterGradeService.transformForDisplay(
                certificateGradeQueryService.findStudentGrades(selectedStudent.id(), term, year)
            )
        );
        model.addAttribute("grades", grades);
        model.addAttribute("averagePercent", averagePercent(grades));
        model.addAttribute("averageGpa", averageGpa(grades));
        model.addAttribute("honorResult", honorResultsByStudentId.getOrDefault(selectedStudent.id(), HonorRollResult.NONE));
    }

    private BigDecimal averagePercent(List<org.gca.schoolms.certificates.CertificateStudentCourseGrade> grades) {
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (var grade : grades) {
            if (Boolean.TRUE.equals(grade.excludeFromGpa()) || grade.percent() == null) {
                continue;
            }
            total = total.add(grade.percent());
            count++;
        }
        return count == 0 ? null : total.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal averageGpa(List<org.gca.schoolms.certificates.CertificateStudentCourseGrade> grades) {
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (var grade : grades) {
            if (Boolean.TRUE.equals(grade.excludeFromGpa()) || grade.computedGpa() == null) {
                continue;
            }
            total = total.add(grade.computedGpa());
            count++;
        }
        return count == 0 ? null : total.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);
    }
}
