package org.gca.schoolms.certificates;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
public class CertificateBatchExportService {

    private final CertificateGradeQueryService certificateGradeQueryService;
    private final CertificateHonorRollEvaluationService certificateHonorRollEvaluationService;
    private final CertificateTeacherResolverService certificateTeacherResolverService;
    private final CertificateSettingsService certificateSettingsService;
    private final CertificatePdfService certificatePdfService;

    public CertificateBatchExportService(
        CertificateGradeQueryService certificateGradeQueryService,
        CertificateHonorRollEvaluationService certificateHonorRollEvaluationService,
        CertificateTeacherResolverService certificateTeacherResolverService,
        CertificateSettingsService certificateSettingsService,
        CertificatePdfService certificatePdfService
    ) {
        this.certificateGradeQueryService = certificateGradeQueryService;
        this.certificateHonorRollEvaluationService = certificateHonorRollEvaluationService;
        this.certificateTeacherResolverService = certificateTeacherResolverService;
        this.certificateSettingsService = certificateSettingsService;
        this.certificatePdfService = certificatePdfService;
    }

    public byte[] exportHonorCertificates(
        Integer grade,
        Integer year,
        String term,
        List<Integer> enrollStatuses,
        List<HonorRollResult> honorTypes
    ) {
        Set<HonorRollResult> allowedTypes = normalizeHonorTypes(honorTypes);
        List<CertificateStudentSummary> students = certificateGradeQueryService.findStudents(grade, year, term, enrollStatuses);
        CertificateSettingsView certificateSettings = certificateSettingsService.getSettings();

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(bytes);

            for (CertificateStudentSummary student : students) {
                List<CertificateStudentCourseGrade> grades = certificateGradeQueryService.findStudentGrades(student.id(), term, year);
                HonorRollResult honorResult = certificateHonorRollEvaluationService.evaluate(grades);
                if (!allowedTypes.contains(honorResult)) {
                    continue;
                }

                String studentName = Stream.of(student.fname(), student.mname(), student.lname())
                    .filter(value -> value != null && !value.isBlank())
                    .collect(Collectors.joining(" "));

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
                context.setVariable("homeroomTeacher", certificateTeacherResolverService.findHomeRoomTeacher("Citizenship", grades));
                context.setVariable("honorResult", honorResult);

                byte[] pdf = certificatePdfService.generate("academics/certificate-pdf", context);
                zipOutputStream.putNextEntry(new ZipEntry(buildFilename(student, term, honorResult)));
                zipOutputStream.write(pdf);
                zipOutputStream.closeEntry();
            }

            zipOutputStream.finish();
            zipOutputStream.close();
            return bytes.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to export honor certificates", exception);
        }
    }

    private Set<HonorRollResult> normalizeHonorTypes(List<HonorRollResult> honorTypes) {
        if (honorTypes == null || honorTypes.isEmpty()) {
            return Set.of(HonorRollResult.PRINCIPAL, HonorRollResult.A_HONOR, HonorRollResult.B_HONOR);
        }
        return honorTypes.stream()
            .filter(result -> result != null && result != HonorRollResult.NONE)
            .collect(Collectors.toSet());
    }

    private String buildFilename(CertificateStudentSummary student, String term, HonorRollResult honorResult) {
        String name = Stream.of(student.lname(), student.fname())
            .filter(value -> value != null && !value.isBlank())
            .map(this::safeToken)
            .collect(Collectors.joining("_"));
        return name + "_" + safeToken(term) + "_" + safeToken(honorResult.getDisplayName()) + ".pdf";
    }

    private String safeToken(String value) {
        return value == null ? "unknown" : value.replaceAll("[^A-Za-z0-9]+", "-").replaceAll("(^-+|-+$)", "");
    }
}
