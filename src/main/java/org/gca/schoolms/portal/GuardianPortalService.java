package org.gca.schoolms.portal;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.gca.schoolms.enrollment.EnrollmentDocument;
import org.gca.schoolms.enrollment.EnrollmentDocumentRepository;
import org.gca.schoolms.enrollment.EnrollmentDocumentStorageService;
import org.gca.schoolms.enrollment.EnrollmentDocumentType;
import org.gca.schoolms.enrollment.EnrollmentRequest;
import org.gca.schoolms.enrollment.EnrollmentRequestLanguage;
import org.gca.schoolms.enrollment.EnrollmentRequestRepository;
import org.gca.schoolms.enrollment.EnrollmentRequestStatus;
import org.gca.schoolms.enrollment.EnrollmentRequestType;
import org.gca.schoolms.finance.FamilyAccount;
import org.gca.schoolms.finance.FamilyAccountRepository;
import org.gca.schoolms.finance.InvoiceRepository;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.records.Student;
import org.gca.schoolms.records.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuardianPortalService {

    private final FamilyAccountRepository familyAccountRepository;
    private final StudentRepository studentRepository;
    private final InvoiceRepository invoiceRepository;
    private final EnrollmentRequestRepository enrollmentRequestRepository;
    private final EnrollmentDocumentRepository enrollmentDocumentRepository;
    private final EnrollmentDocumentStorageService enrollmentDocumentStorageService;
    private final CampusRepository campusRepository;

    public GuardianPortalService(FamilyAccountRepository familyAccountRepository, StudentRepository studentRepository,
                                 InvoiceRepository invoiceRepository, EnrollmentRequestRepository enrollmentRequestRepository,
                                 EnrollmentDocumentRepository enrollmentDocumentRepository,
                                 EnrollmentDocumentStorageService enrollmentDocumentStorageService,
                                 CampusRepository campusRepository) {
        this.familyAccountRepository = familyAccountRepository;
        this.studentRepository = studentRepository;
        this.invoiceRepository = invoiceRepository;
        this.enrollmentRequestRepository = enrollmentRequestRepository;
        this.enrollmentDocumentRepository = enrollmentDocumentRepository;
        this.enrollmentDocumentStorageService = enrollmentDocumentStorageService;
        this.campusRepository = campusRepository;
    }

    public GuardianDashboardView loadDashboard(String username) {
        FamilyAccount familyAccount = resolveFamilyAccount(username);
        List<Student> students = studentRepository.findByFamilyAccountOrderByLastNameAscFirstNameAsc(familyAccount);
        List<GuardianDashboardStudent> dashboardStudents = students.stream()
            .map(student -> new GuardianDashboardStudent(
                student.getId(),
                student.getDisplayName(),
                student.getGradeLevel().getLabel(),
                student.getCampus().getCode(),
                student.getStatus().name()))
            .toList();
        return new GuardianDashboardView(
            familyAccount.getAccountName(),
            familyAccount.getPrimaryGuardianName(),
            familyAccount.getSecondaryGuardianName(),
            familyAccount.isPrimaryGuardianBillingRecipient()
                ? familyAccount.getPrimaryGuardianName()
                : familyAccount.getSecondaryGuardianName(),
            students.size(),
            invoiceRepository.sumOutstandingBalanceByFamilyAccount(familyAccount).orElse(java.math.BigDecimal.ZERO),
            dashboardStudents,
            enrollmentRequestRepository.findByFamilyAccountOrderBySubmittedOnDesc(familyAccount)
        );
    }

    public FamilyAccount resolveFamilyAccount(String username) {
        if ("guardian".equals(username)) {
            return familyAccountRepository.findByAccountNumber("FA-1001")
                .orElseThrow();
        }
        return familyAccountRepository.findTop10ByOrderByAccountNameAsc().stream().findFirst().orElseThrow();
    }

    public List<Student> loadStudentsForGuardian(String username) {
        return studentRepository.findByFamilyAccountOrderByLastNameAscFirstNameAsc(resolveFamilyAccount(username));
    }

    public GuardianEnrollmentForm buildEnrollmentForm(String username, Long studentId) {
        GuardianEnrollmentForm form = new GuardianEnrollmentForm();
        FamilyAccount familyAccount = resolveFamilyAccount(username);
        applyGuardianProfile(form, familyAccount);
        if (studentId == null) {
            return form;
        }
        Student student = findGuardianStudent(username, studentId);
        form.setExistingStudentId(student.getId());
        form.setRequestType(EnrollmentRequestType.REENROLLMENT);
        form.setStudentFirstName(student.getFirstName());
        form.setStudentMiddleName(student.getMiddleName());
        form.setStudentLastName(student.getLastName());
        form.setStudentSuffix(student.getSuffix());
        form.setStudentAlias(student.getPreferredName());
        form.setStudentDateOfBirth(student.getDateOfBirth());
        form.setCampusId(student.getCampus().getId());
        form.setRequestedGradeLevel(student.getGradeLevel().nextGradeLevel());
        form.setReenrollmentPrefill(true);
        return form;
    }

    public GuardianEnrollmentPrefillView buildEnrollmentPrefill(String username, Long studentId) {
        if (studentId == null) {
            return new GuardianEnrollmentPrefillView("New student application", "Select a grade level");
        }
        Student student = findGuardianStudent(username, studentId);
        return new GuardianEnrollmentPrefillView(
            student.getDisplayName() + " / current grade " + student.getGradeLevel().getLabel(),
            student.getGradeLevel().nextGradeLevel().getLabel()
        );
    }

    public GuardianProfileForm buildProfileForm(String username) {
        GuardianProfileForm form = new GuardianProfileForm();
        applyGuardianProfile(form, resolveFamilyAccount(username));
        return form;
    }

    @Transactional
    public void updateGuardianProfile(String username, GuardianProfileForm form) {
        FamilyAccount familyAccount = resolveFamilyAccount(username);
        familyAccount.updateGuardianProfile(
            form.getGuardianName(),
            form.getGuardianEmail(),
            form.getGuardianPhone(),
            form.getGuardianMailingAddressLine1(),
            form.getGuardianMailingAddressLine2(),
            form.getGuardianMailingCity(),
            form.getGuardianMailingState(),
            form.getGuardianMailingPostalCode(),
            form.getGuardianEmployerName(),
            form.getGuardianWorkPhone(),
            form.getGuardianWorkEmail(),
            form.getGuardianWorkAddressLine1(),
            form.getGuardianWorkAddressLine2(),
            form.getGuardianWorkCity(),
            form.getGuardianWorkState(),
            form.getGuardianWorkPostalCode(),
            form.getGuardianGender(),
            form.getGuardianEthnicity(),
            form.getGuardianCitizenshipStatus(),
            form.getGuardianCountryOfCitizenship(),
            form.isGuardianVisaRequired(),
            form.getGuardianVisaType(),
            form.getGuardianVisaNumber(),
            form.getGuardianVisaIssueDate(),
            form.getGuardianVisaExpirationDate(),
            form.getMaritalStatus(),
            form.getSecondaryGuardianName(),
            form.getSecondaryGuardianEmail(),
            form.getSecondaryGuardianPhone(),
            form.getSecondaryMailingAddressLine1(),
            form.getSecondaryMailingAddressLine2(),
            form.getSecondaryMailingCity(),
            form.getSecondaryMailingState(),
            form.getSecondaryMailingPostalCode(),
            form.getSecondaryEmployerName(),
            form.getSecondaryWorkPhone(),
            form.getSecondaryWorkEmail(),
            form.getSecondaryWorkAddressLine1(),
            form.getSecondaryWorkAddressLine2(),
            form.getSecondaryWorkCity(),
            form.getSecondaryWorkState(),
            form.getSecondaryWorkPostalCode(),
            form.getSecondaryGender(),
            form.getSecondaryEthnicity(),
            form.getSecondaryCitizenshipStatus(),
            form.getSecondaryCountryOfCitizenship(),
            form.isSecondaryVisaRequired(),
            form.getSecondaryVisaType(),
            form.getSecondaryVisaNumber(),
            form.getSecondaryVisaIssueDate(),
            form.getSecondaryVisaExpirationDate(),
            form.isSecondaryGuardianPortalAccess(),
            form.isPrimaryGuardianBillingRecipient()
        );
    }

    public GuardianFinanceView loadFinance(String username) {
        FamilyAccount familyAccount = resolveFamilyAccount(username);
        String billingRecipient = familyAccount.isPrimaryGuardianBillingRecipient()
            ? familyAccount.getPrimaryGuardianName()
            : familyAccount.getSecondaryGuardianName();
        return new GuardianFinanceView(
            invoiceRepository.sumOutstandingBalanceByFamilyAccount(familyAccount).orElse(java.math.BigDecimal.ZERO),
            billingRecipient,
            invoiceRepository.findByFamilyAccountOrderByDueDateAsc(familyAccount)
        );
    }

    @Transactional
    public void submitEnrollmentRequest(String username, GuardianEnrollmentForm form) {
        FamilyAccount familyAccount = resolveFamilyAccount(username);
        Student existingStudent = form.getExistingStudentId() == null ? null :
            findGuardianStudent(username, form.getExistingStudentId());
        EnrollmentRequest request = new EnrollmentRequest(
            familyAccount,
            existingStudent,
            campusRepository.findById(form.getCampusId()).orElseThrow(),
            existingStudent == null ? form.getRequestType() : EnrollmentRequestType.REENROLLMENT,
            EnrollmentRequestStatus.SUBMITTED,
            form.getSchoolYear(),
            form.getStudentFirstName(),
            form.getStudentMiddleName(),
            form.getStudentLastName(),
            form.getStudentSuffix(),
            form.getStudentAlias(),
            form.getStudentDateOfBirth(),
            form.getStudentReligiousAffiliation(),
            form.getStudentChurchAttending(),
            form.getStudentEthnicBackgrounds() == null ? "" : form.getStudentEthnicBackgrounds().stream()
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining(", ")),
            form.getStudentEthnicBackgroundOther(),
            form.isChildPottyTrained(),
            form.getPottyAccidentFrequency(),
            form.getGuardianName(),
            form.getGuardianEmail(),
            form.getGuardianPhone(),
            form.getGuardianMailingAddressLine1(),
            form.getGuardianMailingAddressLine2(),
            form.getGuardianMailingCity(),
            form.getGuardianMailingState(),
            form.getGuardianMailingPostalCode(),
            form.getGuardianEmployerName(),
            form.getGuardianWorkPhone(),
            form.getGuardianWorkEmail(),
            form.getGuardianWorkAddressLine1(),
            form.getGuardianWorkAddressLine2(),
            form.getGuardianWorkCity(),
            form.getGuardianWorkState(),
            form.getGuardianWorkPostalCode(),
            form.getGuardianGender(),
            form.getGuardianEthnicity(),
            form.getGuardianCitizenshipStatus(),
            form.getGuardianCountryOfCitizenship(),
            form.isGuardianVisaRequired(),
            form.getGuardianVisaType(),
            form.getGuardianVisaNumber(),
            form.getGuardianVisaIssueDate(),
            form.getGuardianVisaExpirationDate(),
            form.getMaritalStatus(),
            form.getSecondaryGuardianName(),
            form.getSecondaryGuardianEmail(),
            form.getSecondaryGuardianPhone(),
            form.getSecondaryMailingAddressLine1(),
            form.getSecondaryMailingAddressLine2(),
            form.getSecondaryMailingCity(),
            form.getSecondaryMailingState(),
            form.getSecondaryMailingPostalCode(),
            form.getSecondaryEmployerName(),
            form.getSecondaryWorkPhone(),
            form.getSecondaryWorkEmail(),
            form.getSecondaryWorkAddressLine1(),
            form.getSecondaryWorkAddressLine2(),
            form.getSecondaryWorkCity(),
            form.getSecondaryWorkState(),
            form.getSecondaryWorkPostalCode(),
            form.getSecondaryGender(),
            form.getSecondaryEthnicity(),
            form.getSecondaryCitizenshipStatus(),
            form.getSecondaryCountryOfCitizenship(),
            form.isSecondaryVisaRequired(),
            form.getSecondaryVisaType(),
            form.getSecondaryVisaNumber(),
            form.getSecondaryVisaIssueDate(),
            form.getSecondaryVisaExpirationDate(),
            form.isSecondaryGuardianPortalAccess(),
            form.isPrimaryGuardianBillingRecipient(),
            form.getStudentCitizenshipStatus(),
            form.getStudentCountryOfCitizenship(),
            form.isStudentVisaRequired(),
            form.getStudentVisaType(),
            form.getStudentVisaNumber(),
            form.getStudentVisaIssueDate(),
            form.getStudentVisaExpirationDate(),
            form.isStudentF1Required(),
            form.getStudentI20Status(),
            form.getPreviousSchoolName(),
            form.getPreviousSchoolCity(),
            form.getPreviousSchoolState(),
            form.getPreviousSchoolCountry(),
            form.getPreviousSchoolLastGradeCompleted(),
            form.getRequestedGradeLevel(),
            LocalDate.now()
        );
        familyAccount.updateGuardianProfile(
            form.getGuardianName(),
            form.getGuardianEmail(),
            form.getGuardianPhone(),
            form.getGuardianMailingAddressLine1(),
            form.getGuardianMailingAddressLine2(),
            form.getGuardianMailingCity(),
            form.getGuardianMailingState(),
            form.getGuardianMailingPostalCode(),
            form.getGuardianEmployerName(),
            form.getGuardianWorkPhone(),
            form.getGuardianWorkEmail(),
            form.getGuardianWorkAddressLine1(),
            form.getGuardianWorkAddressLine2(),
            form.getGuardianWorkCity(),
            form.getGuardianWorkState(),
            form.getGuardianWorkPostalCode(),
            form.getGuardianGender(),
            form.getGuardianEthnicity(),
            form.getGuardianCitizenshipStatus(),
            form.getGuardianCountryOfCitizenship(),
            form.isGuardianVisaRequired(),
            form.getGuardianVisaType(),
            form.getGuardianVisaNumber(),
            form.getGuardianVisaIssueDate(),
            form.getGuardianVisaExpirationDate(),
            form.getMaritalStatus(),
            form.getSecondaryGuardianName(),
            form.getSecondaryGuardianEmail(),
            form.getSecondaryGuardianPhone(),
            form.getSecondaryMailingAddressLine1(),
            form.getSecondaryMailingAddressLine2(),
            form.getSecondaryMailingCity(),
            form.getSecondaryMailingState(),
            form.getSecondaryMailingPostalCode(),
            form.getSecondaryEmployerName(),
            form.getSecondaryWorkPhone(),
            form.getSecondaryWorkEmail(),
            form.getSecondaryWorkAddressLine1(),
            form.getSecondaryWorkAddressLine2(),
            form.getSecondaryWorkCity(),
            form.getSecondaryWorkState(),
            form.getSecondaryWorkPostalCode(),
            form.getSecondaryGender(),
            form.getSecondaryEthnicity(),
            form.getSecondaryCitizenshipStatus(),
            form.getSecondaryCountryOfCitizenship(),
            form.isSecondaryVisaRequired(),
            form.getSecondaryVisaType(),
            form.getSecondaryVisaNumber(),
            form.getSecondaryVisaIssueDate(),
            form.getSecondaryVisaExpirationDate(),
            form.isSecondaryGuardianPortalAccess(),
            form.isPrimaryGuardianBillingRecipient()
        );
        EnrollmentRequest savedRequest = enrollmentRequestRepository.save(request);
        savedRequest.replaceStudentLanguages(
            form.getStudentLanguages().stream()
                .filter(language -> language.getLanguageName() != null && !language.getLanguageName().isBlank())
                .sorted(Comparator.comparing(language ->
                    language.getPreferenceRank() == null ? Integer.MAX_VALUE : language.getPreferenceRank()))
                .map(language -> new EnrollmentRequestLanguage(
                    savedRequest,
                    language.getLanguageName().trim(),
                    language.getProficiencyLevel(),
                    language.getPreferenceRank()))
                .toList()
        );
        enrollmentRequestRepository.save(savedRequest);
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.VACCINATION_CARD, form.getVaccinationRecordFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.HEALTH_CERTIFICATE, form.getHealthCertificateFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.PREVIOUS_SCHOOL_TRANSCRIPT, form.getPreviousTranscriptFile());
    }

    private void applyGuardianProfile(GuardianEnrollmentForm form, FamilyAccount familyAccount) {
        form.setGuardianName(familyAccount.getPrimaryGuardianName());
        form.setGuardianEmail(familyAccount.getPrimaryGuardianEmail());
        form.setGuardianPhone(familyAccount.getPrimaryGuardianPhone());
        form.setGuardianMailingAddressLine1(familyAccount.getMailingAddressLine1());
        form.setGuardianMailingAddressLine2(familyAccount.getMailingAddressLine2());
        form.setGuardianMailingCity(familyAccount.getMailingCity());
        form.setGuardianMailingState(familyAccount.getMailingState());
        form.setGuardianMailingPostalCode(familyAccount.getMailingPostalCode());
        form.setGuardianEmployerName(familyAccount.getEmployerName());
        form.setGuardianWorkPhone(familyAccount.getWorkPhone());
        form.setGuardianWorkEmail(familyAccount.getWorkEmail());
        form.setGuardianWorkAddressLine1(familyAccount.getWorkAddressLine1());
        form.setGuardianWorkAddressLine2(familyAccount.getWorkAddressLine2());
        form.setGuardianWorkCity(familyAccount.getWorkCity());
        form.setGuardianWorkState(familyAccount.getWorkState());
        form.setGuardianWorkPostalCode(familyAccount.getWorkPostalCode());
        form.setGuardianGender(familyAccount.getGender());
        form.setGuardianEthnicity(familyAccount.getEthnicity());
        form.setGuardianCitizenshipStatus(familyAccount.getCitizenshipStatus());
        form.setGuardianCountryOfCitizenship(familyAccount.getCountryOfCitizenship());
        form.setGuardianVisaRequired(familyAccount.isVisaRequired());
        form.setGuardianVisaType(familyAccount.getVisaType());
        form.setGuardianVisaNumber(familyAccount.getVisaNumber());
        form.setGuardianVisaIssueDate(familyAccount.getVisaIssueDate());
        form.setGuardianVisaExpirationDate(familyAccount.getVisaExpirationDate());
        form.setMaritalStatus(familyAccount.getMaritalStatus());
        form.setSecondaryGuardianName(familyAccount.getSecondaryGuardianName());
        form.setSecondaryGuardianEmail(familyAccount.getSecondaryGuardianEmail());
        form.setSecondaryGuardianPhone(familyAccount.getSecondaryGuardianPhone());
        form.setSecondaryMailingAddressLine1(familyAccount.getSecondaryMailingAddressLine1());
        form.setSecondaryMailingAddressLine2(familyAccount.getSecondaryMailingAddressLine2());
        form.setSecondaryMailingCity(familyAccount.getSecondaryMailingCity());
        form.setSecondaryMailingState(familyAccount.getSecondaryMailingState());
        form.setSecondaryMailingPostalCode(familyAccount.getSecondaryMailingPostalCode());
        form.setSecondaryEmployerName(familyAccount.getSecondaryEmployerName());
        form.setSecondaryWorkPhone(familyAccount.getSecondaryWorkPhone());
        form.setSecondaryWorkEmail(familyAccount.getSecondaryWorkEmail());
        form.setSecondaryWorkAddressLine1(familyAccount.getSecondaryWorkAddressLine1());
        form.setSecondaryWorkAddressLine2(familyAccount.getSecondaryWorkAddressLine2());
        form.setSecondaryWorkCity(familyAccount.getSecondaryWorkCity());
        form.setSecondaryWorkState(familyAccount.getSecondaryWorkState());
        form.setSecondaryWorkPostalCode(familyAccount.getSecondaryWorkPostalCode());
        form.setSecondaryGender(familyAccount.getSecondaryGender());
        form.setSecondaryEthnicity(familyAccount.getSecondaryEthnicity());
        form.setSecondaryCitizenshipStatus(familyAccount.getSecondaryCitizenshipStatus());
        form.setSecondaryCountryOfCitizenship(familyAccount.getSecondaryCountryOfCitizenship());
        form.setSecondaryVisaRequired(familyAccount.isSecondaryVisaRequired());
        form.setSecondaryVisaType(familyAccount.getSecondaryVisaType());
        form.setSecondaryVisaNumber(familyAccount.getSecondaryVisaNumber());
        form.setSecondaryVisaIssueDate(familyAccount.getSecondaryVisaIssueDate());
        form.setSecondaryVisaExpirationDate(familyAccount.getSecondaryVisaExpirationDate());
        form.setSecondaryGuardianPortalAccess(familyAccount.isSecondaryGuardianPortalAccess());
        form.setPrimaryGuardianBillingRecipient(familyAccount.isPrimaryGuardianBillingRecipient());
    }

    private void applyGuardianProfile(GuardianProfileForm form, FamilyAccount familyAccount) {
        form.setGuardianName(familyAccount.getPrimaryGuardianName());
        form.setGuardianEmail(familyAccount.getPrimaryGuardianEmail());
        form.setGuardianPhone(familyAccount.getPrimaryGuardianPhone());
        form.setGuardianMailingAddressLine1(familyAccount.getMailingAddressLine1());
        form.setGuardianMailingAddressLine2(familyAccount.getMailingAddressLine2());
        form.setGuardianMailingCity(familyAccount.getMailingCity());
        form.setGuardianMailingState(familyAccount.getMailingState());
        form.setGuardianMailingPostalCode(familyAccount.getMailingPostalCode());
        form.setGuardianEmployerName(familyAccount.getEmployerName());
        form.setGuardianWorkPhone(familyAccount.getWorkPhone());
        form.setGuardianWorkEmail(familyAccount.getWorkEmail());
        form.setGuardianWorkAddressLine1(familyAccount.getWorkAddressLine1());
        form.setGuardianWorkAddressLine2(familyAccount.getWorkAddressLine2());
        form.setGuardianWorkCity(familyAccount.getWorkCity());
        form.setGuardianWorkState(familyAccount.getWorkState());
        form.setGuardianWorkPostalCode(familyAccount.getWorkPostalCode());
        form.setGuardianGender(familyAccount.getGender());
        form.setGuardianEthnicity(familyAccount.getEthnicity());
        form.setGuardianCitizenshipStatus(familyAccount.getCitizenshipStatus());
        form.setGuardianCountryOfCitizenship(familyAccount.getCountryOfCitizenship());
        form.setGuardianVisaRequired(familyAccount.isVisaRequired());
        form.setGuardianVisaType(familyAccount.getVisaType());
        form.setGuardianVisaNumber(familyAccount.getVisaNumber());
        form.setGuardianVisaIssueDate(familyAccount.getVisaIssueDate());
        form.setGuardianVisaExpirationDate(familyAccount.getVisaExpirationDate());
        form.setMaritalStatus(familyAccount.getMaritalStatus());
        form.setSecondaryGuardianName(familyAccount.getSecondaryGuardianName());
        form.setSecondaryGuardianEmail(familyAccount.getSecondaryGuardianEmail());
        form.setSecondaryGuardianPhone(familyAccount.getSecondaryGuardianPhone());
        form.setSecondaryMailingAddressLine1(familyAccount.getSecondaryMailingAddressLine1());
        form.setSecondaryMailingAddressLine2(familyAccount.getSecondaryMailingAddressLine2());
        form.setSecondaryMailingCity(familyAccount.getSecondaryMailingCity());
        form.setSecondaryMailingState(familyAccount.getSecondaryMailingState());
        form.setSecondaryMailingPostalCode(familyAccount.getSecondaryMailingPostalCode());
        form.setSecondaryEmployerName(familyAccount.getSecondaryEmployerName());
        form.setSecondaryWorkPhone(familyAccount.getSecondaryWorkPhone());
        form.setSecondaryWorkEmail(familyAccount.getSecondaryWorkEmail());
        form.setSecondaryWorkAddressLine1(familyAccount.getSecondaryWorkAddressLine1());
        form.setSecondaryWorkAddressLine2(familyAccount.getSecondaryWorkAddressLine2());
        form.setSecondaryWorkCity(familyAccount.getSecondaryWorkCity());
        form.setSecondaryWorkState(familyAccount.getSecondaryWorkState());
        form.setSecondaryWorkPostalCode(familyAccount.getSecondaryWorkPostalCode());
        form.setSecondaryGender(familyAccount.getSecondaryGender());
        form.setSecondaryEthnicity(familyAccount.getSecondaryEthnicity());
        form.setSecondaryCitizenshipStatus(familyAccount.getSecondaryCitizenshipStatus());
        form.setSecondaryCountryOfCitizenship(familyAccount.getSecondaryCountryOfCitizenship());
        form.setSecondaryVisaRequired(familyAccount.isSecondaryVisaRequired());
        form.setSecondaryVisaType(familyAccount.getSecondaryVisaType());
        form.setSecondaryVisaNumber(familyAccount.getSecondaryVisaNumber());
        form.setSecondaryVisaIssueDate(familyAccount.getSecondaryVisaIssueDate());
        form.setSecondaryVisaExpirationDate(familyAccount.getSecondaryVisaExpirationDate());
        form.setSecondaryGuardianPortalAccess(familyAccount.isSecondaryGuardianPortalAccess());
        form.setPrimaryGuardianBillingRecipient(familyAccount.isPrimaryGuardianBillingRecipient());
    }

    private Student findGuardianStudent(String username, Long studentId) {
        FamilyAccount familyAccount = resolveFamilyAccount(username);
        return studentRepository.findById(studentId)
            .filter(student -> student.getFamilyAccount().getId().equals(familyAccount.getId()))
            .orElseThrow();
    }

    private void storeEnrollmentDocument(EnrollmentRequest enrollmentRequest, EnrollmentDocumentType documentType,
                                         org.springframework.web.multipart.MultipartFile file) {
        var storedDocument = enrollmentDocumentStorageService.store(enrollmentRequest, documentType, file);
        if (storedDocument == null) {
            return;
        }
        enrollmentDocumentRepository.save(new EnrollmentDocument(
            enrollmentRequest,
            enrollmentRequest.getStudent(),
            documentType,
            storedDocument.storedFilename(),
            storedDocument.storedFilename(),
            storedDocument.contentType(),
            storedDocument.storagePath(),
            java.time.LocalDateTime.now()
        ));
    }
}
