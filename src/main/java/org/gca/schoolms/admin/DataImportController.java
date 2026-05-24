package org.gca.schoolms.admin;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.gca.schoolms.integration.powerschool.PowerSchoolImportPreview;
import org.gca.schoolms.integration.powerschool.PowerSchoolImportReport;
import org.gca.schoolms.integration.powerschool.PowerSchoolImportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/imports")
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_STAFF')")
public class DataImportController {

    private static final String SESSION_PENDING_TOKEN = "powerschoolImport.pendingToken";
    private static final String SESSION_PENDING_FILENAME = "powerschoolImport.pendingFilename";
    private static final String SESSION_PENDING_BYTES = "powerschoolImport.pendingBytes";
    private static final String FLASH_PREVIEW = "importPreview";
    private static final String FLASH_RESULT = "importResult";
    private static final String FLASH_MESSAGE = "message";

    private final PowerSchoolImportService powerSchoolImportService;

    public DataImportController(PowerSchoolImportService powerSchoolImportService) {
        this.powerSchoolImportService = powerSchoolImportService;
    }

    @GetMapping
    public String importsPage(Model model) {
        return importsPage(null, model);
    }

    @GetMapping(params = "uploadError")
    public String importsPage(
        @RequestParam(required = false) String uploadError,
        Model model
    ) {
        if (!model.containsAttribute(FLASH_PREVIEW)) {
            model.addAttribute(FLASH_PREVIEW, null);
        }
        if (!model.containsAttribute(FLASH_RESULT)) {
            model.addAttribute(FLASH_RESULT, null);
        }
        if ("fileTooLarge".equals(uploadError) && !model.containsAttribute(FLASH_MESSAGE)) {
            model.addAttribute(FLASH_MESSAGE, "The uploaded file exceeded the current size limit. The LMS has been updated to allow larger TSV imports, so retry the upload after refresh.");
        }
        model.addAttribute("normalExportFiles", List.of(
            "Students_export_YYYYMMDD.tsv",
            "StoredGrades_export_YYYYMMDD.tsv",
            "PGFinalGrades_export_YYYYMMDD.tsv",
            "Sections_export_YYYYMMDD.tsv",
            "SectionTeacher_export_YYYYMMDD.tsv",
            "Teachers_export_YYYYMMDD.tsv",
            "SchoolStaff_export_YYYYMMDD.tsv",
            "Guardian_export_YYYYMMDD.tsv",
            "GuardianStudent_export_YYYYMMDD.tsv",
            "GuardianRelationshipType_export_YYYYMMDD.tsv",
            "GuardianPersonAssoc_export_YYYYMMDD.tsv",
            "PSM_Teacher_export_YYYYMMDD.tsv",
            "PSM_Section_export_YYYYMMDD.tsv",
            "PSM_SectionTeacher_export_YYYYMMDD.tsv",
            "PSM_StudentContact_export_YYYYMMDD.tsv",
            "PSM_StudentContactType_export_YYYYMMDD.tsv",
            "PSM_SchoolCourse_export_YYYYMMDD.tsv",
            "PSM_GradeScale_export_YYYYMMDD.tsv",
            "PSM_Grade_export_YYYYMMDD.tsv"
        ));
        model.addAttribute("optionalExportFiles", List.of(
            "Person_export_YYYYMMDD.tsv",
            "PersonAddress_export_YYYYMMDD.tsv",
            "PersonAddressAssoc_export_YYYYMMDD.tsv",
            "EmailAddress_export_YYYYMMDD.tsv",
            "PhoneNumber_export_YYYYMMDD.tsv",
            "PersonPhoneNumberAssoc_export_YYYYMMDD.tsv",
            "PersonEmailAddressAssoc_export_YYYYMMDD.tsv",
            "StudentContactAssoc_export_YYYYMMDD.tsv",
            "StudentContactDetail_export_YYYYMMDD.tsv"
        ));
        model.addAttribute("supportedDatasets", List.of(
            "Students",
            "Stored Grades",
            "Person",
            "PersonAddress",
            "PersonAddressAssoc",
            "EmailAddress",
            "PhoneNumber",
            "PersonPhoneNumberAssoc",
            "PersonEmailAddressAssoc",
            "StudentContactAssoc",
            "StudentContactDetail",
            "PGFinalGrades",
            "Sections",
            "SectionTeacher",
            "Teachers",
            "SchoolStaff",
            "Guardian",
            "GuardianStudent",
            "GuardianRelationshipType",
            "GuardianPersonAssoc",
            "PSM_Teacher",
            "PSM_Section",
            "PSM_SectionTeacher",
            "PSM_StudentContact",
            "PSM_StudentContactType",
            "PSM_SchoolCourse",
            "PSM_GradeScale",
            "PSM_GRADE"
        ));
        return "admin/imports";
    }

    @PostMapping("/preview")
    public String previewImport(
        @RequestParam("file") MultipartFile file,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        clearPendingImport(session);

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, "Choose a TSV file before validating.");
            return "redirect:/admin/imports";
        }

        try {
            byte[] bytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            PowerSchoolImportReport report = powerSchoolImportService.validateBytes(originalFilename, bytes);
            String token = UUID.randomUUID().toString();
            PowerSchoolImportPreview preview = new PowerSchoolImportPreview(
                report.success(),
                token,
                report.originalFilename(),
                report.datasetType(),
                report.rowsProcessed(),
                report.messages()
            );

            if (report.success()) {
                session.setAttribute(SESSION_PENDING_TOKEN, token);
                session.setAttribute(SESSION_PENDING_FILENAME, report.originalFilename());
                session.setAttribute(SESSION_PENDING_BYTES, bytes);
            }

            redirectAttributes.addFlashAttribute(FLASH_PREVIEW, preview);
        } catch (IOException ex) {
            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, "The uploaded file could not be read.");
        }

        return "redirect:/admin/imports";
    }

    @PostMapping("/confirm")
    public String confirmImport(
        @RequestParam("token") String token,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        String pendingToken = (String) session.getAttribute(SESSION_PENDING_TOKEN);
        String filename = (String) session.getAttribute(SESSION_PENDING_FILENAME);
        byte[] bytes = (byte[]) session.getAttribute(SESSION_PENDING_BYTES);

        if (pendingToken == null || bytes == null || filename == null || !pendingToken.equals(token)) {
            clearPendingImport(session);
            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, "The pending import expired. Validate the file again.");
            return "redirect:/admin/imports";
        }

        PowerSchoolImportReport report = powerSchoolImportService.importBytes(filename, bytes);
        clearPendingImport(session);
        redirectAttributes.addFlashAttribute(FLASH_RESULT, report);
        return "redirect:/admin/imports";
    }

    @PostMapping("/reset")
    public String resetImport(HttpSession session) {
        clearPendingImport(session);
        return "redirect:/admin/imports";
    }

    private void clearPendingImport(HttpSession session) {
        session.removeAttribute(SESSION_PENDING_TOKEN);
        session.removeAttribute(SESSION_PENDING_FILENAME);
        session.removeAttribute(SESSION_PENDING_BYTES);
    }
}
