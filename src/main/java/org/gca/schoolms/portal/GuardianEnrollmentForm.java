package org.gca.schoolms.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.gca.schoolms.enrollment.EnrollmentRequestType;
import org.gca.schoolms.finance.MaritalStatus;
import org.gca.schoolms.records.GradeLevel;
import org.springframework.web.multipart.MultipartFile;

public class GuardianEnrollmentForm {

    public GuardianEnrollmentForm() {
        if (studentLanguages.isEmpty()) {
            studentLanguages.add(new StudentLanguageFormRow());
            studentLanguages.add(new StudentLanguageFormRow());
            studentLanguages.add(new StudentLanguageFormRow());
        }
        if (emergencyContacts.isEmpty()) {
            emergencyContacts.add(new EmergencyContactFormRow());
            emergencyContacts.add(new EmergencyContactFormRow());
        }
    }

    @NotBlank
    private String schoolYear = "2026-2027";

    private Long enrollmentRequestId;

    @NotBlank
    private String studentFirstName;

    private String studentMiddleName;

    @NotBlank
    private String studentLastName;

    private String studentSuffix;

    private String studentAlias;

    private LocalDate studentDateOfBirth;

    private String studentReligiousAffiliation;

    private String studentChurchAttending;

    private List<String> studentEthnicBackgrounds = new ArrayList<>();

    private String studentEthnicBackgroundOther;

    private List<StudentLanguageFormRow> studentLanguages = new ArrayList<>();

    private boolean childPottyTrained;

    private String pottyAccidentFrequency;

    @NotBlank
    private String guardianName;

    @NotBlank
    private String guardianEmail;

    @NotBlank
    private String guardianPhone;

    @NotBlank
    private String guardianMailingAddressLine1;

    private String guardianMailingAddressLine2;

    @NotBlank
    private String guardianMailingCity;

    @NotBlank
    private String guardianMailingState;

    @NotBlank
    private String guardianMailingPostalCode;

    private String guardianEmployerName;

    private String guardianWorkPhone;

    private String guardianWorkEmail;

    private String guardianWorkAddressLine1;

    private String guardianWorkAddressLine2;

    private String guardianWorkCity;

    private String guardianWorkState;

    private String guardianWorkPostalCode;

    private String guardianGender;

    private String guardianEthnicity;

    private String guardianCitizenshipStatus;

    private String guardianCountryOfCitizenship;

    private boolean guardianVisaRequired;

    private String guardianVisaType;

    private String guardianVisaNumber;

    private LocalDate guardianVisaIssueDate;

    private LocalDate guardianVisaExpirationDate;

    private MaritalStatus maritalStatus = MaritalStatus.MARRIED;

    private String secondaryGuardianName;

    private String secondaryGuardianEmail;

    private String secondaryGuardianPhone;

    private String secondaryMailingAddressLine1;

    private String secondaryMailingAddressLine2;

    private String secondaryMailingCity;

    private String secondaryMailingState;

    private String secondaryMailingPostalCode;

    private String secondaryEmployerName;

    private String secondaryWorkPhone;

    private String secondaryWorkEmail;

    private String secondaryWorkAddressLine1;

    private String secondaryWorkAddressLine2;

    private String secondaryWorkCity;

    private String secondaryWorkState;

    private String secondaryWorkPostalCode;

    private String secondaryGender;

    private String secondaryEthnicity;

    private String secondaryCitizenshipStatus;

    private String secondaryCountryOfCitizenship;

    private boolean secondaryVisaRequired;

    private String secondaryVisaType;

    private String secondaryVisaNumber;

    private LocalDate secondaryVisaIssueDate;

    private LocalDate secondaryVisaExpirationDate;

    private boolean secondaryGuardianPortalAccess = true;

    private boolean primaryGuardianBillingRecipient = true;

    private String studentCitizenshipStatus;

    private String studentCountryOfCitizenship;

    private boolean studentVisaRequired;

    private String studentVisaType;

    private String studentVisaNumber;

    private LocalDate studentVisaIssueDate;

    private LocalDate studentVisaExpirationDate;

    private boolean studentF1Required;

    private String studentI20Status;

    private String primaryPhysicianName;

    private String physicianClinicName;

    private String physicianPhone;

    private String preferredHospital;

    private String insuranceProvider;

    private String insurancePolicyNumber;

    private String studentAllergies;

    private String studentChronicConditions;

    private String studentMedications;

    private String studentDietaryRestrictions;

    private String studentActivityRestrictions;

    private String studentMedicalNotes;

    private List<EmergencyContactFormRow> emergencyContacts = new ArrayList<>();

    private boolean emergencyTreatmentConsent;

    private boolean medicationAdministrationConsent;

    private boolean emergencyContactReleaseConsent;

    private boolean allowTylenol;

    private boolean allowPeptoBismol;

    private boolean allowRobitussin;

    private boolean allowTums;

    private boolean allowHydrocortisone;

    private boolean allowAspirin;

    private String otherApprovedMedications;

    private boolean parentAttestationConfirmed;

    private String parentAttestationInitials;

    private String previousSchoolName;

    private String previousSchoolCity;

    private String previousSchoolState;

    private String previousSchoolCountry;

    private String previousSchoolLastGradeCompleted;

    private MultipartFile vaccinationRecordFile;

    private boolean vaccinationRecordOnFile;

    private MultipartFile healthCertificateFile;

    private boolean healthCertificateOnFile;

    private MultipartFile previousTranscriptFile;

    private boolean previousTranscriptOnFile;

    @NotNull
    private GradeLevel requestedGradeLevel;

    @NotNull
    private Long campusId;

    @NotNull
    private EnrollmentRequestType requestType = EnrollmentRequestType.NEW_STUDENT;

    private Long existingStudentId;

    private boolean reenrollmentPrefill;

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public Long getEnrollmentRequestId() {
        return enrollmentRequestId;
    }

    public void setEnrollmentRequestId(Long enrollmentRequestId) {
        this.enrollmentRequestId = enrollmentRequestId;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentMiddleName() {
        return studentMiddleName;
    }

    public void setStudentMiddleName(String studentMiddleName) {
        this.studentMiddleName = studentMiddleName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }

    public String getStudentSuffix() {
        return studentSuffix;
    }

    public void setStudentSuffix(String studentSuffix) {
        this.studentSuffix = studentSuffix;
    }

    public String getStudentAlias() {
        return studentAlias;
    }

    public void setStudentAlias(String studentAlias) {
        this.studentAlias = studentAlias;
    }

    public LocalDate getStudentDateOfBirth() {
        return studentDateOfBirth;
    }

    public void setStudentDateOfBirth(LocalDate studentDateOfBirth) {
        this.studentDateOfBirth = studentDateOfBirth;
    }

    public String getStudentReligiousAffiliation() {
        return studentReligiousAffiliation;
    }

    public void setStudentReligiousAffiliation(String studentReligiousAffiliation) {
        this.studentReligiousAffiliation = studentReligiousAffiliation;
    }

    public String getStudentChurchAttending() {
        return studentChurchAttending;
    }

    public void setStudentChurchAttending(String studentChurchAttending) {
        this.studentChurchAttending = studentChurchAttending;
    }

    public List<String> getStudentEthnicBackgrounds() {
        return studentEthnicBackgrounds;
    }

    public void setStudentEthnicBackgrounds(List<String> studentEthnicBackgrounds) {
        this.studentEthnicBackgrounds = studentEthnicBackgrounds;
    }

    public String getStudentEthnicBackgroundOther() {
        return studentEthnicBackgroundOther;
    }

    public void setStudentEthnicBackgroundOther(String studentEthnicBackgroundOther) {
        this.studentEthnicBackgroundOther = studentEthnicBackgroundOther;
    }

    public List<StudentLanguageFormRow> getStudentLanguages() {
        return studentLanguages;
    }

    public void setStudentLanguages(List<StudentLanguageFormRow> studentLanguages) {
        this.studentLanguages = studentLanguages == null ? new ArrayList<>() : studentLanguages;
        while (this.studentLanguages.size() < 3) {
            this.studentLanguages.add(new StudentLanguageFormRow());
        }
    }

    public boolean isChildPottyTrained() {
        return childPottyTrained;
    }

    public void setChildPottyTrained(boolean childPottyTrained) {
        this.childPottyTrained = childPottyTrained;
    }

    public String getPottyAccidentFrequency() {
        return pottyAccidentFrequency;
    }

    public void setPottyAccidentFrequency(String pottyAccidentFrequency) {
        this.pottyAccidentFrequency = pottyAccidentFrequency;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianEmail() {
        return guardianEmail;
    }

    public void setGuardianEmail(String guardianEmail) {
        this.guardianEmail = guardianEmail;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
    }

    public String getGuardianMailingAddressLine1() {
        return guardianMailingAddressLine1;
    }

    public void setGuardianMailingAddressLine1(String guardianMailingAddressLine1) {
        this.guardianMailingAddressLine1 = guardianMailingAddressLine1;
    }

    public String getGuardianMailingAddressLine2() {
        return guardianMailingAddressLine2;
    }

    public void setGuardianMailingAddressLine2(String guardianMailingAddressLine2) {
        this.guardianMailingAddressLine2 = guardianMailingAddressLine2;
    }

    public String getGuardianMailingCity() {
        return guardianMailingCity;
    }

    public void setGuardianMailingCity(String guardianMailingCity) {
        this.guardianMailingCity = guardianMailingCity;
    }

    public String getGuardianMailingState() {
        return guardianMailingState;
    }

    public void setGuardianMailingState(String guardianMailingState) {
        this.guardianMailingState = guardianMailingState;
    }

    public String getGuardianMailingPostalCode() {
        return guardianMailingPostalCode;
    }

    public void setGuardianMailingPostalCode(String guardianMailingPostalCode) {
        this.guardianMailingPostalCode = guardianMailingPostalCode;
    }

    public String getGuardianEmployerName() {
        return guardianEmployerName;
    }

    public void setGuardianEmployerName(String guardianEmployerName) {
        this.guardianEmployerName = guardianEmployerName;
    }

    public String getGuardianWorkPhone() {
        return guardianWorkPhone;
    }

    public void setGuardianWorkPhone(String guardianWorkPhone) {
        this.guardianWorkPhone = guardianWorkPhone;
    }

    public String getGuardianWorkEmail() {
        return guardianWorkEmail;
    }

    public void setGuardianWorkEmail(String guardianWorkEmail) {
        this.guardianWorkEmail = guardianWorkEmail;
    }

    public String getGuardianWorkAddressLine1() {
        return guardianWorkAddressLine1;
    }

    public void setGuardianWorkAddressLine1(String guardianWorkAddressLine1) {
        this.guardianWorkAddressLine1 = guardianWorkAddressLine1;
    }

    public String getGuardianWorkAddressLine2() {
        return guardianWorkAddressLine2;
    }

    public void setGuardianWorkAddressLine2(String guardianWorkAddressLine2) {
        this.guardianWorkAddressLine2 = guardianWorkAddressLine2;
    }

    public String getGuardianWorkCity() {
        return guardianWorkCity;
    }

    public void setGuardianWorkCity(String guardianWorkCity) {
        this.guardianWorkCity = guardianWorkCity;
    }

    public String getGuardianWorkState() {
        return guardianWorkState;
    }

    public void setGuardianWorkState(String guardianWorkState) {
        this.guardianWorkState = guardianWorkState;
    }

    public String getGuardianWorkPostalCode() {
        return guardianWorkPostalCode;
    }

    public void setGuardianWorkPostalCode(String guardianWorkPostalCode) {
        this.guardianWorkPostalCode = guardianWorkPostalCode;
    }

    public String getGuardianGender() {
        return guardianGender;
    }

    public void setGuardianGender(String guardianGender) {
        this.guardianGender = guardianGender;
    }

    public String getGuardianEthnicity() {
        return guardianEthnicity;
    }

    public void setGuardianEthnicity(String guardianEthnicity) {
        this.guardianEthnicity = guardianEthnicity;
    }

    public String getGuardianCitizenshipStatus() {
        return guardianCitizenshipStatus;
    }

    public void setGuardianCitizenshipStatus(String guardianCitizenshipStatus) {
        this.guardianCitizenshipStatus = guardianCitizenshipStatus;
    }

    public String getGuardianCountryOfCitizenship() {
        return guardianCountryOfCitizenship;
    }

    public void setGuardianCountryOfCitizenship(String guardianCountryOfCitizenship) {
        this.guardianCountryOfCitizenship = guardianCountryOfCitizenship;
    }

    public boolean isGuardianVisaRequired() {
        return guardianVisaRequired;
    }

    public void setGuardianVisaRequired(boolean guardianVisaRequired) {
        this.guardianVisaRequired = guardianVisaRequired;
    }

    public String getGuardianVisaType() {
        return guardianVisaType;
    }

    public void setGuardianVisaType(String guardianVisaType) {
        this.guardianVisaType = guardianVisaType;
    }

    public String getGuardianVisaNumber() {
        return guardianVisaNumber;
    }

    public void setGuardianVisaNumber(String guardianVisaNumber) {
        this.guardianVisaNumber = guardianVisaNumber;
    }

    public LocalDate getGuardianVisaIssueDate() {
        return guardianVisaIssueDate;
    }

    public void setGuardianVisaIssueDate(LocalDate guardianVisaIssueDate) {
        this.guardianVisaIssueDate = guardianVisaIssueDate;
    }

    public LocalDate getGuardianVisaExpirationDate() {
        return guardianVisaExpirationDate;
    }

    public void setGuardianVisaExpirationDate(LocalDate guardianVisaExpirationDate) {
        this.guardianVisaExpirationDate = guardianVisaExpirationDate;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getSecondaryGuardianName() {
        return secondaryGuardianName;
    }

    public void setSecondaryGuardianName(String secondaryGuardianName) {
        this.secondaryGuardianName = secondaryGuardianName;
    }

    public String getSecondaryGuardianEmail() {
        return secondaryGuardianEmail;
    }

    public void setSecondaryGuardianEmail(String secondaryGuardianEmail) {
        this.secondaryGuardianEmail = secondaryGuardianEmail;
    }

    public String getSecondaryGuardianPhone() {
        return secondaryGuardianPhone;
    }

    public void setSecondaryGuardianPhone(String secondaryGuardianPhone) {
        this.secondaryGuardianPhone = secondaryGuardianPhone;
    }

    public String getSecondaryMailingAddressLine1() { return secondaryMailingAddressLine1; }
    public void setSecondaryMailingAddressLine1(String secondaryMailingAddressLine1) { this.secondaryMailingAddressLine1 = secondaryMailingAddressLine1; }
    public String getSecondaryMailingAddressLine2() { return secondaryMailingAddressLine2; }
    public void setSecondaryMailingAddressLine2(String secondaryMailingAddressLine2) { this.secondaryMailingAddressLine2 = secondaryMailingAddressLine2; }
    public String getSecondaryMailingCity() { return secondaryMailingCity; }
    public void setSecondaryMailingCity(String secondaryMailingCity) { this.secondaryMailingCity = secondaryMailingCity; }
    public String getSecondaryMailingState() { return secondaryMailingState; }
    public void setSecondaryMailingState(String secondaryMailingState) { this.secondaryMailingState = secondaryMailingState; }
    public String getSecondaryMailingPostalCode() { return secondaryMailingPostalCode; }
    public void setSecondaryMailingPostalCode(String secondaryMailingPostalCode) { this.secondaryMailingPostalCode = secondaryMailingPostalCode; }
    public String getSecondaryEmployerName() { return secondaryEmployerName; }
    public void setSecondaryEmployerName(String secondaryEmployerName) { this.secondaryEmployerName = secondaryEmployerName; }
    public String getSecondaryWorkPhone() { return secondaryWorkPhone; }
    public void setSecondaryWorkPhone(String secondaryWorkPhone) { this.secondaryWorkPhone = secondaryWorkPhone; }
    public String getSecondaryWorkEmail() { return secondaryWorkEmail; }
    public void setSecondaryWorkEmail(String secondaryWorkEmail) { this.secondaryWorkEmail = secondaryWorkEmail; }
    public String getSecondaryWorkAddressLine1() { return secondaryWorkAddressLine1; }
    public void setSecondaryWorkAddressLine1(String secondaryWorkAddressLine1) { this.secondaryWorkAddressLine1 = secondaryWorkAddressLine1; }
    public String getSecondaryWorkAddressLine2() { return secondaryWorkAddressLine2; }
    public void setSecondaryWorkAddressLine2(String secondaryWorkAddressLine2) { this.secondaryWorkAddressLine2 = secondaryWorkAddressLine2; }
    public String getSecondaryWorkCity() { return secondaryWorkCity; }
    public void setSecondaryWorkCity(String secondaryWorkCity) { this.secondaryWorkCity = secondaryWorkCity; }
    public String getSecondaryWorkState() { return secondaryWorkState; }
    public void setSecondaryWorkState(String secondaryWorkState) { this.secondaryWorkState = secondaryWorkState; }
    public String getSecondaryWorkPostalCode() { return secondaryWorkPostalCode; }
    public void setSecondaryWorkPostalCode(String secondaryWorkPostalCode) { this.secondaryWorkPostalCode = secondaryWorkPostalCode; }
    public String getSecondaryGender() { return secondaryGender; }
    public void setSecondaryGender(String secondaryGender) { this.secondaryGender = secondaryGender; }
    public String getSecondaryEthnicity() { return secondaryEthnicity; }
    public void setSecondaryEthnicity(String secondaryEthnicity) { this.secondaryEthnicity = secondaryEthnicity; }
    public String getSecondaryCitizenshipStatus() { return secondaryCitizenshipStatus; }
    public void setSecondaryCitizenshipStatus(String secondaryCitizenshipStatus) { this.secondaryCitizenshipStatus = secondaryCitizenshipStatus; }
    public String getSecondaryCountryOfCitizenship() { return secondaryCountryOfCitizenship; }
    public void setSecondaryCountryOfCitizenship(String secondaryCountryOfCitizenship) { this.secondaryCountryOfCitizenship = secondaryCountryOfCitizenship; }
    public boolean isSecondaryVisaRequired() { return secondaryVisaRequired; }
    public void setSecondaryVisaRequired(boolean secondaryVisaRequired) { this.secondaryVisaRequired = secondaryVisaRequired; }
    public String getSecondaryVisaType() { return secondaryVisaType; }
    public void setSecondaryVisaType(String secondaryVisaType) { this.secondaryVisaType = secondaryVisaType; }
    public String getSecondaryVisaNumber() { return secondaryVisaNumber; }
    public void setSecondaryVisaNumber(String secondaryVisaNumber) { this.secondaryVisaNumber = secondaryVisaNumber; }
    public LocalDate getSecondaryVisaIssueDate() { return secondaryVisaIssueDate; }
    public void setSecondaryVisaIssueDate(LocalDate secondaryVisaIssueDate) { this.secondaryVisaIssueDate = secondaryVisaIssueDate; }
    public LocalDate getSecondaryVisaExpirationDate() { return secondaryVisaExpirationDate; }
    public void setSecondaryVisaExpirationDate(LocalDate secondaryVisaExpirationDate) { this.secondaryVisaExpirationDate = secondaryVisaExpirationDate; }

    public boolean isSecondaryGuardianPortalAccess() {
        return secondaryGuardianPortalAccess;
    }

    public void setSecondaryGuardianPortalAccess(boolean secondaryGuardianPortalAccess) {
        this.secondaryGuardianPortalAccess = secondaryGuardianPortalAccess;
    }

    public boolean isPrimaryGuardianBillingRecipient() {
        return primaryGuardianBillingRecipient;
    }

    public void setPrimaryGuardianBillingRecipient(boolean primaryGuardianBillingRecipient) {
        this.primaryGuardianBillingRecipient = primaryGuardianBillingRecipient;
    }

    public String getStudentCitizenshipStatus() {
        return studentCitizenshipStatus;
    }

    public void setStudentCitizenshipStatus(String studentCitizenshipStatus) {
        this.studentCitizenshipStatus = studentCitizenshipStatus;
    }

    public String getStudentCountryOfCitizenship() {
        return studentCountryOfCitizenship;
    }

    public void setStudentCountryOfCitizenship(String studentCountryOfCitizenship) {
        this.studentCountryOfCitizenship = studentCountryOfCitizenship;
    }

    public boolean isStudentVisaRequired() {
        return studentVisaRequired;
    }

    public void setStudentVisaRequired(boolean studentVisaRequired) {
        this.studentVisaRequired = studentVisaRequired;
    }

    public String getStudentVisaType() {
        return studentVisaType;
    }

    public void setStudentVisaType(String studentVisaType) {
        this.studentVisaType = studentVisaType;
    }

    public String getStudentVisaNumber() {
        return studentVisaNumber;
    }

    public void setStudentVisaNumber(String studentVisaNumber) {
        this.studentVisaNumber = studentVisaNumber;
    }

    public LocalDate getStudentVisaIssueDate() {
        return studentVisaIssueDate;
    }

    public void setStudentVisaIssueDate(LocalDate studentVisaIssueDate) {
        this.studentVisaIssueDate = studentVisaIssueDate;
    }

    public LocalDate getStudentVisaExpirationDate() {
        return studentVisaExpirationDate;
    }

    public void setStudentVisaExpirationDate(LocalDate studentVisaExpirationDate) {
        this.studentVisaExpirationDate = studentVisaExpirationDate;
    }

    public boolean isStudentF1Required() {
        return studentF1Required;
    }

    public void setStudentF1Required(boolean studentF1Required) {
        this.studentF1Required = studentF1Required;
    }

    public String getStudentI20Status() {
        return studentI20Status;
    }

    public void setStudentI20Status(String studentI20Status) {
        this.studentI20Status = studentI20Status;
    }

    public String getPrimaryPhysicianName() {
        return primaryPhysicianName;
    }

    public void setPrimaryPhysicianName(String primaryPhysicianName) {
        this.primaryPhysicianName = primaryPhysicianName;
    }

    public String getPhysicianClinicName() {
        return physicianClinicName;
    }

    public void setPhysicianClinicName(String physicianClinicName) {
        this.physicianClinicName = physicianClinicName;
    }

    public String getPhysicianPhone() {
        return physicianPhone;
    }

    public void setPhysicianPhone(String physicianPhone) {
        this.physicianPhone = physicianPhone;
    }

    public String getPreferredHospital() {
        return preferredHospital;
    }

    public void setPreferredHospital(String preferredHospital) {
        this.preferredHospital = preferredHospital;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }

    public String getStudentAllergies() {
        return studentAllergies;
    }

    public void setStudentAllergies(String studentAllergies) {
        this.studentAllergies = studentAllergies;
    }

    public String getStudentChronicConditions() {
        return studentChronicConditions;
    }

    public void setStudentChronicConditions(String studentChronicConditions) {
        this.studentChronicConditions = studentChronicConditions;
    }

    public String getStudentMedications() {
        return studentMedications;
    }

    public void setStudentMedications(String studentMedications) {
        this.studentMedications = studentMedications;
    }

    public String getStudentDietaryRestrictions() {
        return studentDietaryRestrictions;
    }

    public void setStudentDietaryRestrictions(String studentDietaryRestrictions) {
        this.studentDietaryRestrictions = studentDietaryRestrictions;
    }

    public String getStudentActivityRestrictions() {
        return studentActivityRestrictions;
    }

    public void setStudentActivityRestrictions(String studentActivityRestrictions) {
        this.studentActivityRestrictions = studentActivityRestrictions;
    }

    public String getStudentMedicalNotes() {
        return studentMedicalNotes;
    }

    public void setStudentMedicalNotes(String studentMedicalNotes) {
        this.studentMedicalNotes = studentMedicalNotes;
    }

    public List<EmergencyContactFormRow> getEmergencyContacts() {
        return emergencyContacts;
    }

    public void setEmergencyContacts(List<EmergencyContactFormRow> emergencyContacts) {
        this.emergencyContacts = emergencyContacts == null ? new ArrayList<>() : emergencyContacts;
        while (this.emergencyContacts.size() < 2) {
            this.emergencyContacts.add(new EmergencyContactFormRow());
        }
    }

    public boolean isEmergencyTreatmentConsent() {
        return emergencyTreatmentConsent;
    }

    public void setEmergencyTreatmentConsent(boolean emergencyTreatmentConsent) {
        this.emergencyTreatmentConsent = emergencyTreatmentConsent;
    }

    public boolean isMedicationAdministrationConsent() {
        return medicationAdministrationConsent;
    }

    public void setMedicationAdministrationConsent(boolean medicationAdministrationConsent) {
        this.medicationAdministrationConsent = medicationAdministrationConsent;
    }

    public boolean isEmergencyContactReleaseConsent() {
        return emergencyContactReleaseConsent;
    }

    public void setEmergencyContactReleaseConsent(boolean emergencyContactReleaseConsent) {
        this.emergencyContactReleaseConsent = emergencyContactReleaseConsent;
    }

    public boolean isAllowTylenol() {
        return allowTylenol;
    }

    public void setAllowTylenol(boolean allowTylenol) {
        this.allowTylenol = allowTylenol;
    }

    public boolean isAllowPeptoBismol() {
        return allowPeptoBismol;
    }

    public void setAllowPeptoBismol(boolean allowPeptoBismol) {
        this.allowPeptoBismol = allowPeptoBismol;
    }

    public boolean isAllowRobitussin() {
        return allowRobitussin;
    }

    public void setAllowRobitussin(boolean allowRobitussin) {
        this.allowRobitussin = allowRobitussin;
    }

    public boolean isAllowTums() {
        return allowTums;
    }

    public void setAllowTums(boolean allowTums) {
        this.allowTums = allowTums;
    }

    public boolean isAllowHydrocortisone() {
        return allowHydrocortisone;
    }

    public void setAllowHydrocortisone(boolean allowHydrocortisone) {
        this.allowHydrocortisone = allowHydrocortisone;
    }

    public boolean isAllowAspirin() {
        return allowAspirin;
    }

    public void setAllowAspirin(boolean allowAspirin) {
        this.allowAspirin = allowAspirin;
    }

    public String getOtherApprovedMedications() {
        return otherApprovedMedications;
    }

    public void setOtherApprovedMedications(String otherApprovedMedications) {
        this.otherApprovedMedications = otherApprovedMedications;
    }

    public boolean isParentAttestationConfirmed() {
        return parentAttestationConfirmed;
    }

    public void setParentAttestationConfirmed(boolean parentAttestationConfirmed) {
        this.parentAttestationConfirmed = parentAttestationConfirmed;
    }

    public String getParentAttestationInitials() {
        return parentAttestationInitials;
    }

    public void setParentAttestationInitials(String parentAttestationInitials) {
        this.parentAttestationInitials = parentAttestationInitials;
    }

    public String getPreviousSchoolName() {
        return previousSchoolName;
    }

    public void setPreviousSchoolName(String previousSchoolName) {
        this.previousSchoolName = previousSchoolName;
    }

    public String getPreviousSchoolCity() {
        return previousSchoolCity;
    }

    public void setPreviousSchoolCity(String previousSchoolCity) {
        this.previousSchoolCity = previousSchoolCity;
    }

    public String getPreviousSchoolState() {
        return previousSchoolState;
    }

    public void setPreviousSchoolState(String previousSchoolState) {
        this.previousSchoolState = previousSchoolState;
    }

    public String getPreviousSchoolCountry() {
        return previousSchoolCountry;
    }

    public void setPreviousSchoolCountry(String previousSchoolCountry) {
        this.previousSchoolCountry = previousSchoolCountry;
    }

    public String getPreviousSchoolLastGradeCompleted() {
        return previousSchoolLastGradeCompleted;
    }

    public void setPreviousSchoolLastGradeCompleted(String previousSchoolLastGradeCompleted) {
        this.previousSchoolLastGradeCompleted = previousSchoolLastGradeCompleted;
    }

    public MultipartFile getVaccinationRecordFile() {
        return vaccinationRecordFile;
    }

    public void setVaccinationRecordFile(MultipartFile vaccinationRecordFile) {
        this.vaccinationRecordFile = vaccinationRecordFile;
    }

    public boolean isVaccinationRecordOnFile() {
        return vaccinationRecordOnFile;
    }

    public void setVaccinationRecordOnFile(boolean vaccinationRecordOnFile) {
        this.vaccinationRecordOnFile = vaccinationRecordOnFile;
    }

    public MultipartFile getHealthCertificateFile() {
        return healthCertificateFile;
    }

    public void setHealthCertificateFile(MultipartFile healthCertificateFile) {
        this.healthCertificateFile = healthCertificateFile;
    }

    public boolean isHealthCertificateOnFile() {
        return healthCertificateOnFile;
    }

    public void setHealthCertificateOnFile(boolean healthCertificateOnFile) {
        this.healthCertificateOnFile = healthCertificateOnFile;
    }

    public MultipartFile getPreviousTranscriptFile() {
        return previousTranscriptFile;
    }

    public void setPreviousTranscriptFile(MultipartFile previousTranscriptFile) {
        this.previousTranscriptFile = previousTranscriptFile;
    }

    public boolean isPreviousTranscriptOnFile() {
        return previousTranscriptOnFile;
    }

    public void setPreviousTranscriptOnFile(boolean previousTranscriptOnFile) {
        this.previousTranscriptOnFile = previousTranscriptOnFile;
    }

    public GradeLevel getRequestedGradeLevel() {
        return requestedGradeLevel;
    }

    public void setRequestedGradeLevel(GradeLevel requestedGradeLevel) {
        this.requestedGradeLevel = requestedGradeLevel;
    }

    public Long getCampusId() {
        return campusId;
    }

    public void setCampusId(Long campusId) {
        this.campusId = campusId;
    }

    public EnrollmentRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(EnrollmentRequestType requestType) {
        this.requestType = requestType;
    }

    public Long getExistingStudentId() {
        return existingStudentId;
    }

    public void setExistingStudentId(Long existingStudentId) {
        this.existingStudentId = existingStudentId;
    }

    public boolean isReenrollmentPrefill() {
        return reenrollmentPrefill;
    }

    public void setReenrollmentPrefill(boolean reenrollmentPrefill) {
        this.reenrollmentPrefill = reenrollmentPrefill;
    }
}
