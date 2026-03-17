package org.gca.schoolms.portal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import org.gca.schoolms.enrollment.EnrollmentAttestation;
import org.gca.schoolms.enrollment.EnrollmentDocument;
import org.gca.schoolms.enrollment.EnrollmentDocumentRepository;
import org.gca.schoolms.enrollment.EnrollmentDocumentStorageService;
import org.gca.schoolms.enrollment.EnrollmentDocumentType;
import org.gca.schoolms.enrollment.EnrollmentEmergencyContact;
import org.gca.schoolms.enrollment.EnrollmentRequest;
import org.gca.schoolms.enrollment.EnrollmentRequestLanguage;
import org.gca.schoolms.enrollment.EnrollmentRequestRepository;
import org.gca.schoolms.enrollment.EnrollmentRequestStatus;
import org.gca.schoolms.enrollment.EnrollmentRequestType;
import org.gca.schoolms.enrollment.RegistrarReviewStatus;
import org.gca.schoolms.finance.FamilyAccount;
import org.gca.schoolms.finance.FamilyAccountRepository;
import org.gca.schoolms.finance.FinanceLedgerService;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.records.Student;
import org.gca.schoolms.records.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuardianPortalService {

    private static final EnumSet<EnrollmentRequestStatus> PARENT_MANAGEABLE_STATUSES =
        EnumSet.of(EnrollmentRequestStatus.DRAFT, EnrollmentRequestStatus.SUBMITTED);

    private final FamilyAccountRepository familyAccountRepository;
    private final StudentRepository studentRepository;
    private final FinanceLedgerService financeLedgerService;
    private final EnrollmentRequestRepository enrollmentRequestRepository;
    private final EnrollmentDocumentRepository enrollmentDocumentRepository;
    private final EnrollmentDocumentStorageService enrollmentDocumentStorageService;
    private final CampusRepository campusRepository;

    public GuardianPortalService(FamilyAccountRepository familyAccountRepository, StudentRepository studentRepository,
                                 FinanceLedgerService financeLedgerService, EnrollmentRequestRepository enrollmentRequestRepository,
                                 EnrollmentDocumentRepository enrollmentDocumentRepository,
                                 EnrollmentDocumentStorageService enrollmentDocumentStorageService,
                                 CampusRepository campusRepository) {
        this.familyAccountRepository = familyAccountRepository;
        this.studentRepository = studentRepository;
        this.financeLedgerService = financeLedgerService;
        this.enrollmentRequestRepository = enrollmentRequestRepository;
        this.enrollmentDocumentRepository = enrollmentDocumentRepository;
        this.enrollmentDocumentStorageService = enrollmentDocumentStorageService;
        this.campusRepository = campusRepository;
    }

    @Transactional(readOnly = true)
    public GuardianDashboardView loadDashboard(String username) {
        FamilyAccount familyAccount = resolveFamilyAccount(username);
        List<Student> students = studentRepository.findByFamilyAccountOrderByLastNameAscFirstNameAsc(familyAccount);
        List<GuardianEnrollmentActivityView> enrollmentRequests = enrollmentRequestRepository
            .findByFamilyAccountOrderBySubmittedOnDesc(familyAccount).stream()
            .map(request -> {
                EnrollmentCompletionView completion = calculateCompletion(request);
                boolean editable = PARENT_MANAGEABLE_STATUSES.contains(request.getStatus());
                return new GuardianEnrollmentActivityView(
                    request.getId(),
                    request.getStudentDisplayName(),
                    request.getRequestType(),
                    request.getSchoolYear(),
                    request.getRequestedGradeLevel(),
                    request.getStatus(),
                    buildParentStatusLabel(request, completion),
                    request.getRegistrarComment(),
                    completion.completionPercentage(),
                    editable,
                    editable
                );
            })
            .toList();
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
            financeLedgerService.outstandingBalanceForFamily(familyAccount),
            dashboardStudents,
            enrollmentRequests
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

    @Transactional(readOnly = true)
    public GuardianEnrollmentForm buildEnrollmentForm(String username, Long studentId, Long requestId) {
        GuardianEnrollmentForm form = new GuardianEnrollmentForm();
        FamilyAccount familyAccount = resolveFamilyAccount(username);
        applyGuardianProfile(form, familyAccount);
        if (requestId != null) {
            EnrollmentRequest request = findManageableEnrollmentRequest(username, requestId);
            applyDocumentFlags(form, request);
            form.setEnrollmentRequestId(request.getId());
            form.setExistingStudentId(request.getStudent() == null ? null : request.getStudent().getId());
            form.setRequestType(request.getRequestType());
            form.setSchoolYear(request.getSchoolYear());
            form.setStudentFirstName(request.getStudentFirstName());
            form.setStudentMiddleName(request.getStudentMiddleName());
            form.setStudentLastName(request.getStudentLastName());
            form.setStudentSuffix(request.getStudentSuffix());
            form.setStudentAlias(request.getStudentAlias());
            form.setStudentDateOfBirth(request.getStudentDateOfBirth());
            form.setStudentReligiousAffiliation(request.getStudentReligiousAffiliation());
            form.setStudentChurchAttending(request.getStudentChurchAttending());
            form.setStudentEthnicBackgrounds(splitCommaSeparatedValues(request.getStudentEthnicBackgrounds()));
            form.setStudentEthnicBackgroundOther(request.getStudentEthnicBackgroundOther());
            form.setStudentLanguages(request.getStudentLanguages().stream().map(language -> {
                StudentLanguageFormRow row = new StudentLanguageFormRow();
                row.setLanguageName(language.getLanguageName());
                row.setProficiencyLevel(language.getProficiencyLevel());
                row.setPreferenceRank(language.getPreferenceRank());
                return row;
            }).collect(Collectors.toCollection(ArrayList::new)));
            form.setChildPottyTrained(request.isChildPottyTrained());
            form.setPottyAccidentFrequency(request.getPottyAccidentFrequency());
            form.setStudentCitizenshipStatus(request.getStudentCitizenshipStatus());
            form.setStudentCountryOfCitizenship(request.getStudentCountryOfCitizenship());
            form.setStudentVisaRequired(request.isStudentVisaRequired());
            form.setStudentVisaType(request.getStudentVisaType());
            form.setStudentVisaNumber(request.getStudentVisaNumber());
            form.setStudentVisaIssueDate(request.getStudentVisaIssueDate());
            form.setStudentVisaExpirationDate(request.getStudentVisaExpirationDate());
            form.setStudentF1Required(request.isStudentF1Required());
            form.setStudentI20Status(request.getStudentI20Status());
            form.setPrimaryPhysicianName(request.getPrimaryPhysicianName());
            form.setPhysicianClinicName(request.getPhysicianClinicName());
            form.setPhysicianPhone(request.getPhysicianPhone());
            form.setPreferredHospital(request.getPreferredHospital());
            form.setInsuranceProvider(request.getInsuranceProvider());
            form.setInsurancePolicyNumber(request.getInsurancePolicyNumber());
            form.setStudentAllergies(request.getStudentAllergies());
            form.setStudentChronicConditions(request.getStudentChronicConditions());
            form.setStudentMedications(request.getStudentMedications());
            form.setStudentDietaryRestrictions(request.getStudentDietaryRestrictions());
            form.setStudentActivityRestrictions(request.getStudentActivityRestrictions());
            form.setStudentMedicalNotes(request.getStudentMedicalNotes());
            form.setEmergencyContacts(request.getEmergencyContacts().stream().map(contact -> {
                EmergencyContactFormRow row = new EmergencyContactFormRow();
                row.setContactName(contact.getContactName());
                row.setRelationshipToStudent(contact.getRelationshipToStudent());
                row.setPrimaryPhone(contact.getPrimaryPhone());
                row.setSecondaryPhone(contact.getSecondaryPhone());
                row.setEmail(contact.getEmail());
                row.setPickupAuthorized(contact.isPickupAuthorized());
                return row;
            }).collect(Collectors.toCollection(ArrayList::new)));
            form.setEmergencyTreatmentConsent(request.isEmergencyTreatmentConsent());
            form.setMedicationAdministrationConsent(request.isMedicationAdministrationConsent());
            form.setEmergencyContactReleaseConsent(request.isEmergencyContactReleaseConsent());
            form.setAllowTylenol(request.isAllowTylenol());
            form.setAllowPeptoBismol(request.isAllowPeptoBismol());
            form.setAllowRobitussin(request.isAllowRobitussin());
            form.setAllowTums(request.isAllowTums());
            form.setAllowHydrocortisone(request.isAllowHydrocortisone());
            form.setAllowAspirin(request.isAllowAspirin());
            form.setOtherApprovedMedications(request.getOtherApprovedMedications());
            if (request.getAttestation() != null) {
                form.setParentAttestationConfirmed(request.getAttestation().isConfirmedTrueAndCorrect());
                form.setParentAttestationInitials(request.getAttestation().getParentInitials());
            }
            form.setPreviousSchoolName(request.getPreviousSchoolName());
            form.setPreviousSchoolCity(request.getPreviousSchoolCity());
            form.setPreviousSchoolState(request.getPreviousSchoolState());
            form.setPreviousSchoolCountry(request.getPreviousSchoolCountry());
            form.setPreviousSchoolLastGradeCompleted(request.getPreviousSchoolLastGradeCompleted());
            form.setCampusId(request.getCampus().getId());
            form.setRequestedGradeLevel(request.getRequestedGradeLevel());
            form.setReenrollmentPrefill(request.getStudent() != null);
            return form;
        }
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
            financeLedgerService.outstandingBalanceForFamily(familyAccount),
            billingRecipient,
            financeLedgerService.feesForFamily(familyAccount),
            financeLedgerService.paymentRowsForFamily(familyAccount)
        );
    }

    @Transactional
    public EnrollmentCompletionView calculateCompletion(GuardianEnrollmentForm form) {
        List<String> missingFields = new ArrayList<>();
        addMissing(missingFields, form.getSchoolYear(), "School year");
        addMissing(missingFields, form.getStudentFirstName(), "Student first name");
        addMissing(missingFields, form.getStudentLastName(), "Student last name");
        if (form.getStudentDateOfBirth() == null) {
            missingFields.add("Date of birth");
        }
        if (form.getRequestedGradeLevel() == null) {
            missingFields.add("Requested grade level");
        }
        if (form.getCampusId() == null) {
            missingFields.add("Campus");
        }
        addMissing(missingFields, form.getGuardianName(), "Parent/guardian name");
        addMissing(missingFields, form.getGuardianEmail(), "Parent/guardian email");
        addMissing(missingFields, form.getGuardianPhone(), "Parent/guardian phone");
        if (form.getStudentLanguages().stream().noneMatch(language ->
            language.getLanguageName() != null && !language.getLanguageName().isBlank())) {
            missingFields.add("Languages spoken");
        }
        if (form.getStudentEthnicBackgrounds() == null || form.getStudentEthnicBackgrounds().isEmpty()) {
            missingFields.add("Student ethnic background");
        }
        if (form.getEmergencyContacts().stream().noneMatch(contact ->
            contact.getContactName() != null && !contact.getContactName().isBlank()
                && contact.getPrimaryPhone() != null && !contact.getPrimaryPhone().isBlank())) {
            missingFields.add("Emergency contact");
        }
        if (!form.isParentAttestationConfirmed()) {
            missingFields.add("Parent confirmation");
        }
        addMissing(missingFields, form.getParentAttestationInitials(), "Parent initials");
        int totalFields = 14;
        int completeFields = totalFields - missingFields.size();
        int completionPercentage = (int) Math.round((completeFields * 100.0) / totalFields);
        List<String> missingDocuments = missingDocumentLabels(form);
        boolean documentsComplete = missingDocuments.isEmpty();
        if (!documentsComplete) {
            missingFields.add("Required documents");
        }
        return new EnrollmentCompletionView(completionPercentage, missingFields, missingDocuments, documentsComplete);
    }

    public EnrollmentCompletionView calculateCompletion(EnrollmentRequest request) {
        GuardianEnrollmentForm form = new GuardianEnrollmentForm();
        form.setSchoolYear(request.getSchoolYear());
        form.setStudentFirstName(request.getStudentFirstName());
        form.setStudentLastName(request.getStudentLastName());
        form.setStudentDateOfBirth(request.getStudentDateOfBirth());
        form.setRequestedGradeLevel(request.getRequestedGradeLevel());
        form.setCampusId(request.getCampus().getId());
        form.setGuardianName(request.getGuardianName());
        form.setGuardianEmail(request.getGuardianEmail());
        form.setGuardianPhone(request.getGuardianPhone());
        form.setStudentEthnicBackgrounds(splitCommaSeparatedValues(request.getStudentEthnicBackgrounds()));
        form.setStudentLanguages(request.getStudentLanguages().stream().map(language -> {
            StudentLanguageFormRow row = new StudentLanguageFormRow();
            row.setLanguageName(language.getLanguageName());
            row.setProficiencyLevel(language.getProficiencyLevel());
            row.setPreferenceRank(language.getPreferenceRank());
            return row;
        }).collect(Collectors.toCollection(ArrayList::new)));
        form.setEmergencyContacts(request.getEmergencyContacts().stream().map(contact -> {
            EmergencyContactFormRow row = new EmergencyContactFormRow();
            row.setContactName(contact.getContactName());
            row.setRelationshipToStudent(contact.getRelationshipToStudent());
            row.setPrimaryPhone(contact.getPrimaryPhone());
            row.setSecondaryPhone(contact.getSecondaryPhone());
            row.setEmail(contact.getEmail());
            row.setPickupAuthorized(contact.isPickupAuthorized());
            return row;
        }).collect(Collectors.toCollection(ArrayList::new)));
        if (request.getAttestation() != null) {
            form.setParentAttestationConfirmed(request.getAttestation().isConfirmedTrueAndCorrect());
            form.setParentAttestationInitials(request.getAttestation().getParentInitials());
        }
        return calculateCompletion(form);
    }

    @Transactional
    public void saveEnrollmentDraft(String username, GuardianEnrollmentForm form) {
        saveEnrollment(username, form, EnrollmentRequestStatus.DRAFT);
    }

    @Transactional
    public void submitEnrollmentRequest(String username, GuardianEnrollmentForm form) {
        saveEnrollment(username, form, EnrollmentRequestStatus.SUBMITTED);
    }

    @Transactional
    public void deleteEnrollmentRequest(String username, Long requestId) {
        EnrollmentRequest request = findManageableEnrollmentRequest(username, requestId);
        enrollmentDocumentRepository.deleteByEnrollmentRequest(request);
        enrollmentRequestRepository.delete(request);
    }

    private void saveEnrollment(String username, GuardianEnrollmentForm form, EnrollmentRequestStatus targetStatus) {
        FamilyAccount familyAccount = resolveFamilyAccount(username);
        Student existingStudent = form.getExistingStudentId() == null ? null :
            findGuardianStudent(username, form.getExistingStudentId());
        EnrollmentRequest request = form.getEnrollmentRequestId() == null
            ? new EnrollmentRequest(
                familyAccount,
                existingStudent,
                campusRepository.findById(form.getCampusId()).orElseThrow(),
                existingStudent == null ? form.getRequestType() : EnrollmentRequestType.REENROLLMENT,
                targetStatus,
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
            )
            : findManageableEnrollmentRequest(username, form.getEnrollmentRequestId());
        if (form.getEnrollmentRequestId() != null) {
            request.updateDraftOrSubmission(
                campusRepository.findById(form.getCampusId()).orElseThrow(),
                existingStudent,
                existingStudent == null ? form.getRequestType() : EnrollmentRequestType.REENROLLMENT,
                targetStatus,
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
        }
        request.updateMedicalAndEmergency(
            form.getPrimaryPhysicianName(),
            form.getPhysicianClinicName(),
            form.getPhysicianPhone(),
            form.getPreferredHospital(),
            form.getInsuranceProvider(),
            form.getInsurancePolicyNumber(),
            form.getStudentAllergies(),
            form.getStudentChronicConditions(),
            form.getStudentMedications(),
            form.getStudentDietaryRestrictions(),
            form.getStudentActivityRestrictions(),
            form.getStudentMedicalNotes(),
            form.isEmergencyTreatmentConsent(),
            form.isMedicationAdministrationConsent(),
            form.isEmergencyContactReleaseConsent(),
            form.isAllowTylenol(),
            form.isAllowPeptoBismol(),
            form.isAllowRobitussin(),
            form.isAllowTums(),
            form.isAllowHydrocortisone(),
            form.isAllowAspirin(),
            form.getOtherApprovedMedications()
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
        savedRequest.replaceEmergencyContacts(
            form.getEmergencyContacts().stream()
                .filter(contact -> contact.getContactName() != null && !contact.getContactName().isBlank())
                .filter(contact -> contact.getPrimaryPhone() != null && !contact.getPrimaryPhone().isBlank())
                .map(contact -> new EnrollmentEmergencyContact(
                    savedRequest,
                    contact.getContactName().trim(),
                    contact.getRelationshipToStudent(),
                    contact.getPrimaryPhone().trim(),
                    contact.getSecondaryPhone(),
                    contact.getEmail(),
                    contact.isPickupAuthorized()))
                .toList()
        );
        if (form.isParentAttestationConfirmed() && form.getParentAttestationInitials() != null
            && !form.getParentAttestationInitials().isBlank()) {
            if (savedRequest.getAttestation() == null) {
                savedRequest.replaceAttestation(new EnrollmentAttestation(
                    savedRequest,
                    true,
                    form.getParentAttestationInitials().trim().toUpperCase(),
                    LocalDateTime.now()
                ));
            } else {
                savedRequest.getAttestation().update(
                    true,
                    form.getParentAttestationInitials().trim().toUpperCase(),
                    LocalDateTime.now()
                );
            }
        } else {
            savedRequest.replaceAttestation(null);
        }
        enrollmentRequestRepository.save(savedRequest);
        if (targetStatus != EnrollmentRequestStatus.DRAFT) {
            financeLedgerService.ensureEnrollmentRequestFee(savedRequest);
        }
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.BIRTH_CERTIFICATE, form.getBirthCertificateFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.VACCINATION_CARD, form.getVaccinationRecordFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.HEALTH_CERTIFICATE, form.getHealthCertificateFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.CHC_BLUE_CARE, form.getChcBlueCareFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.HEALTH_PROFILE, form.getHealthProfileFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.RECENT_PHOTOGRAPH, form.getRecentPhotographFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.REPORT_CARD, form.getReportCardFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.OFFICIAL_TRANSCRIPT, form.getOfficialTranscriptFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.PREVIOUS_SCHOOL_TRANSCRIPT, form.getPreviousTranscriptFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.STUDENT_PASSPORT, form.getStudentPassportFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.BANK_CERTIFICATE, form.getBankCertificateFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.GUARDIANSHIP_DOCUMENT, form.getGuardianshipDocumentFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.STUDENT_VISA, form.getStudentVisaFile());
        storeEnrollmentDocument(savedRequest, EnrollmentDocumentType.PREVIOUS_SCHOOL_I20, form.getPreviousSchoolI20File());
    }

    private EnrollmentRequest findManageableEnrollmentRequest(String username, Long requestId) {
        FamilyAccount familyAccount = resolveFamilyAccount(username);
        return enrollmentRequestRepository.findById(requestId)
            .filter(request -> request.getFamilyAccount().getId().equals(familyAccount.getId()))
            .filter(request -> PARENT_MANAGEABLE_STATUSES.contains(request.getStatus()))
            .orElseThrow();
    }

    private List<String> splitCommaSeparatedValues(String values) {
        if (values == null || values.isBlank()) {
            return new ArrayList<>();
        }
        return java.util.Arrays.stream(values.split(","))
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .collect(Collectors.toCollection(ArrayList::new));
    }

    private void addMissing(List<String> missingFields, String value, String label) {
        if (value == null || value.isBlank()) {
            missingFields.add(label);
        }
    }

    public String buildParentStatusLabel(EnrollmentRequest request, EnrollmentCompletionView completion) {
        if (request.getRegistrarReviewStatus() == RegistrarReviewStatus.MISSING_DETAILS) {
            return "Registrar requested updates";
        }
        if (request.getStatus() == EnrollmentRequestStatus.FINANCE_HOLD) {
            return "Finance hold";
        }
        if (request.getStatus() == EnrollmentRequestStatus.READY_FOR_FINANCE) {
            return "Awaiting finance clearance";
        }
        if (request.getStatus() == EnrollmentRequestStatus.READY_TO_ENROLL) {
            return "Ready to enroll";
        }
        if (request.getStatus() == EnrollmentRequestStatus.ENROLLED) {
            return "Enrolled";
        }
        if (!completion.documentsComplete()) {
            return "Needs documents";
        }
        if (!completion.missingFields().isEmpty()) {
            return "Missing details";
        }
        if (request.getStatus() == EnrollmentRequestStatus.DRAFT) {
            return "Complete draft";
        }
        return switch (request.getStatus()) {
            case SUBMITTED -> "Submitted";
            case READY_FOR_FINANCE -> "Awaiting finance clearance";
            case FINANCE_HOLD -> "Finance hold";
            case READY_TO_ENROLL -> "Ready to enroll";
            case ENROLLED -> "Enrolled";
            case DRAFT -> "Draft";
        };
    }

    public List<String> missingDocumentLabels(GuardianEnrollmentForm form) {
        List<String> missingDocuments = new ArrayList<>();
        addMissingDocument(missingDocuments, form.isBirthCertificateOnFile() || hasFile(form.getBirthCertificateFile()), "Birth certificate");
        addMissingDocument(missingDocuments, form.isVaccinationRecordOnFile() || hasFile(form.getVaccinationRecordFile()), "Immunization record");
        addMissingDocument(missingDocuments,
            (form.isHealthCertificateOnFile() || hasFile(form.getHealthCertificateFile()))
                || (form.isChcBlueCareOnFile() || hasFile(form.getChcBlueCareFile())),
            "School entrance health certificate or CHC Blue Care");
        addMissingDocument(missingDocuments, form.isHealthProfileOnFile() || hasFile(form.getHealthProfileFile()), "Health profile");
        addMissingDocument(missingDocuments, form.isRecentPhotographOnFile() || hasFile(form.getRecentPhotographFile()), "Recent photograph");

        if (form.isStudentVisaRequired()) {
            addMissingDocument(missingDocuments, form.isStudentPassportOnFile() || hasFile(form.getStudentPassportFile()), "Copy of passport");
            addMissingDocument(missingDocuments, form.isBankCertificateOnFile() || hasFile(form.getBankCertificateFile()), "Bank certificate from parents");
            addMissingDocument(missingDocuments, form.isGuardianshipDocumentOnFile() || hasFile(form.getGuardianshipDocumentFile()), "Guardianship document");
            addMissingDocument(missingDocuments, form.isStudentVisaOnFile() || hasFile(form.getStudentVisaFile()), "Copy of F1 Visa");
            addMissingDocument(missingDocuments, form.isPreviousSchoolI20OnFile() || hasFile(form.getPreviousSchoolI20File()), "Copy of I-20 from previous school");
        }

        if (requiresTransferSchoolDocuments(form)) {
            addMissingDocument(missingDocuments, form.isReportCardOnFile() || hasFile(form.getReportCardFile()), "Report card");
            addMissingDocument(missingDocuments,
                (form.isOfficialTranscriptOnFile() || hasFile(form.getOfficialTranscriptFile()))
                    || (form.isPreviousTranscriptOnFile() || hasFile(form.getPreviousTranscriptFile())),
                "Official transcript");
        }
        return missingDocuments;
    }

    public List<String> missingDocumentLabels(EnrollmentRequest request) {
        EnumSet<EnrollmentDocumentType> presentTypes = enrollmentDocumentRepository.findByEnrollmentRequest(request).stream()
            .map(document -> document.getRecordType())
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(EnrollmentDocumentType.class)));
        List<String> missingDocuments = new ArrayList<>();
        addMissingDocument(missingDocuments, presentTypes.contains(EnrollmentDocumentType.BIRTH_CERTIFICATE), "Birth certificate");
        addMissingDocument(missingDocuments, presentTypes.contains(EnrollmentDocumentType.VACCINATION_CARD), "Immunization record");
        addMissingDocument(missingDocuments,
            presentTypes.contains(EnrollmentDocumentType.HEALTH_CERTIFICATE)
                || presentTypes.contains(EnrollmentDocumentType.CHC_BLUE_CARE),
            "School entrance health certificate or CHC Blue Care");
        addMissingDocument(missingDocuments, presentTypes.contains(EnrollmentDocumentType.HEALTH_PROFILE), "Health profile");
        addMissingDocument(missingDocuments, presentTypes.contains(EnrollmentDocumentType.RECENT_PHOTOGRAPH), "Recent photograph");

        if (request.isStudentVisaRequired()) {
            addMissingDocument(missingDocuments, presentTypes.contains(EnrollmentDocumentType.STUDENT_PASSPORT), "Copy of passport");
            addMissingDocument(missingDocuments, presentTypes.contains(EnrollmentDocumentType.BANK_CERTIFICATE), "Bank certificate from parents");
            addMissingDocument(missingDocuments, presentTypes.contains(EnrollmentDocumentType.GUARDIANSHIP_DOCUMENT), "Guardianship document");
            addMissingDocument(missingDocuments, presentTypes.contains(EnrollmentDocumentType.STUDENT_VISA), "Copy of F1 Visa");
            addMissingDocument(missingDocuments, presentTypes.contains(EnrollmentDocumentType.PREVIOUS_SCHOOL_I20), "Copy of I-20 from previous school");
        }

        if (requiresTransferSchoolDocuments(request)) {
            addMissingDocument(missingDocuments, presentTypes.contains(EnrollmentDocumentType.REPORT_CARD), "Report card");
            addMissingDocument(missingDocuments,
                presentTypes.contains(EnrollmentDocumentType.OFFICIAL_TRANSCRIPT)
                    || presentTypes.contains(EnrollmentDocumentType.PREVIOUS_SCHOOL_TRANSCRIPT),
                "Official transcript");
        }
        return missingDocuments;
    }

    private boolean hasFile(org.springframework.web.multipart.MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private void addMissingDocument(List<String> missingDocuments, boolean present, String label) {
        if (!present) {
            missingDocuments.add(label);
        }
    }

    private void applyDocumentFlags(GuardianEnrollmentForm form, EnrollmentRequest request) {
        EnumSet<EnrollmentDocumentType> presentTypes = enrollmentDocumentRepository.findByEnrollmentRequest(request).stream()
            .map(EnrollmentDocument::getRecordType)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(EnrollmentDocumentType.class)));
        form.setBirthCertificateOnFile(presentTypes.contains(EnrollmentDocumentType.BIRTH_CERTIFICATE));
        form.setVaccinationRecordOnFile(presentTypes.contains(EnrollmentDocumentType.VACCINATION_CARD));
        form.setHealthCertificateOnFile(presentTypes.contains(EnrollmentDocumentType.HEALTH_CERTIFICATE));
        form.setChcBlueCareOnFile(presentTypes.contains(EnrollmentDocumentType.CHC_BLUE_CARE));
        form.setHealthProfileOnFile(presentTypes.contains(EnrollmentDocumentType.HEALTH_PROFILE));
        form.setRecentPhotographOnFile(presentTypes.contains(EnrollmentDocumentType.RECENT_PHOTOGRAPH));
        form.setPreviousTranscriptOnFile(presentTypes.contains(EnrollmentDocumentType.PREVIOUS_SCHOOL_TRANSCRIPT));
        form.setReportCardOnFile(presentTypes.contains(EnrollmentDocumentType.REPORT_CARD));
        form.setOfficialTranscriptOnFile(presentTypes.contains(EnrollmentDocumentType.OFFICIAL_TRANSCRIPT));
        form.setStudentPassportOnFile(presentTypes.contains(EnrollmentDocumentType.STUDENT_PASSPORT));
        form.setBankCertificateOnFile(presentTypes.contains(EnrollmentDocumentType.BANK_CERTIFICATE));
        form.setGuardianshipDocumentOnFile(presentTypes.contains(EnrollmentDocumentType.GUARDIANSHIP_DOCUMENT));
        form.setStudentVisaOnFile(presentTypes.contains(EnrollmentDocumentType.STUDENT_VISA));
        form.setPreviousSchoolI20OnFile(presentTypes.contains(EnrollmentDocumentType.PREVIOUS_SCHOOL_I20));
    }

    private boolean requiresTransferSchoolDocuments(GuardianEnrollmentForm form) {
        return form.getPreviousSchoolName() != null && !form.getPreviousSchoolName().isBlank();
    }

    private boolean requiresTransferSchoolDocuments(EnrollmentRequest request) {
        return request.getPreviousSchoolName() != null && !request.getPreviousSchoolName().isBlank();
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
