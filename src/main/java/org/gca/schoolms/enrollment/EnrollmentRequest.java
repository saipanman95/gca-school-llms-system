package org.gca.schoolms.enrollment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import org.gca.schoolms.finance.FamilyAccount;
import org.gca.schoolms.finance.MaritalStatus;
import org.gca.schoolms.organization.Campus;
import org.gca.schoolms.records.GradeLevel;
import org.gca.schoolms.records.Student;

@Entity
public class EnrollmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "family_account_id", nullable = false)
    private FamilyAccount familyAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentRequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentRequestStatus status;

    @Column(nullable = false)
    private String schoolYear;

    @Column(nullable = false)
    private String studentFirstName;

    @Column(nullable = false)
    private String studentLastName;

    @Column(nullable = false)
    private String guardianName;

    @Column(nullable = false)
    private String guardianEmail;

    @Column(nullable = false)
    private String guardianPhone;

    @Column(nullable = false)
    private String guardianMailingAddressLine1;

    private String guardianMailingAddressLine2;

    @Column(nullable = false)
    private String guardianMailingCity;

    @Column(nullable = false)
    private String guardianMailingState;

    @Column(nullable = false)
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

    @Column(nullable = false)
    private boolean guardianVisaRequired;

    private String guardianVisaType;

    private String guardianVisaNumber;

    private LocalDate guardianVisaIssueDate;

    private LocalDate guardianVisaExpirationDate;

    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

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

    @Column(nullable = false)
    private boolean secondaryVisaRequired;

    private String secondaryVisaType;

    private String secondaryVisaNumber;

    private LocalDate secondaryVisaIssueDate;

    private LocalDate secondaryVisaExpirationDate;

    @Column(nullable = false)
    private boolean secondaryGuardianPortalAccess;

    @Column(nullable = false)
    private boolean primaryGuardianBillingRecipient;

    private String studentCitizenshipStatus;

    private String studentCountryOfCitizenship;

    @Column(nullable = false)
    private boolean studentVisaRequired;

    private String studentVisaType;

    private String studentVisaNumber;

    private LocalDate studentVisaIssueDate;

    private LocalDate studentVisaExpirationDate;

    @Column(nullable = false)
    private boolean studentF1Required;

    private String studentI20Status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeLevel requestedGradeLevel;

    @Column(nullable = false)
    private LocalDate submittedOn;

    protected EnrollmentRequest() {
    }

    public EnrollmentRequest(FamilyAccount familyAccount, Student student, Campus campus, EnrollmentRequestType requestType,
                             EnrollmentRequestStatus status, String schoolYear, String studentFirstName,
                             String studentLastName, String guardianName, String guardianEmail, String guardianPhone,
                             String guardianMailingAddressLine1, String guardianMailingAddressLine2,
                             String guardianMailingCity, String guardianMailingState, String guardianMailingPostalCode,
                             String guardianEmployerName, String guardianWorkPhone, String guardianWorkEmail,
                             String guardianWorkAddressLine1, String guardianWorkAddressLine2, String guardianWorkCity,
                             String guardianWorkState, String guardianWorkPostalCode, String guardianGender,
                             String guardianEthnicity, String guardianCitizenshipStatus,
                             String guardianCountryOfCitizenship, boolean guardianVisaRequired,
                             String guardianVisaType, String guardianVisaNumber,
                             LocalDate guardianVisaIssueDate, LocalDate guardianVisaExpirationDate,
                             MaritalStatus maritalStatus, String secondaryGuardianName,
                             String secondaryGuardianEmail, String secondaryGuardianPhone,
                             String secondaryMailingAddressLine1, String secondaryMailingAddressLine2,
                             String secondaryMailingCity, String secondaryMailingState, String secondaryMailingPostalCode,
                             String secondaryEmployerName, String secondaryWorkPhone, String secondaryWorkEmail,
                             String secondaryWorkAddressLine1, String secondaryWorkAddressLine2,
                             String secondaryWorkCity, String secondaryWorkState, String secondaryWorkPostalCode,
                             String secondaryGender, String secondaryEthnicity,
                             String secondaryCitizenshipStatus, String secondaryCountryOfCitizenship,
                             boolean secondaryVisaRequired, String secondaryVisaType, String secondaryVisaNumber,
                             LocalDate secondaryVisaIssueDate, LocalDate secondaryVisaExpirationDate,
                             boolean secondaryGuardianPortalAccess, boolean primaryGuardianBillingRecipient,
                             String studentCitizenshipStatus, String studentCountryOfCitizenship,
                             boolean studentVisaRequired, String studentVisaType, String studentVisaNumber,
                             LocalDate studentVisaIssueDate, LocalDate studentVisaExpirationDate,
                             boolean studentF1Required, String studentI20Status,
                             GradeLevel requestedGradeLevel, LocalDate submittedOn) {
        this.familyAccount = familyAccount;
        this.student = student;
        this.campus = campus;
        this.requestType = requestType;
        this.status = status;
        this.schoolYear = schoolYear;
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
        this.guardianName = guardianName;
        this.guardianEmail = guardianEmail;
        this.guardianPhone = guardianPhone;
        this.guardianMailingAddressLine1 = guardianMailingAddressLine1;
        this.guardianMailingAddressLine2 = guardianMailingAddressLine2;
        this.guardianMailingCity = guardianMailingCity;
        this.guardianMailingState = guardianMailingState;
        this.guardianMailingPostalCode = guardianMailingPostalCode;
        this.guardianEmployerName = guardianEmployerName;
        this.guardianWorkPhone = guardianWorkPhone;
        this.guardianWorkEmail = guardianWorkEmail;
        this.guardianWorkAddressLine1 = guardianWorkAddressLine1;
        this.guardianWorkAddressLine2 = guardianWorkAddressLine2;
        this.guardianWorkCity = guardianWorkCity;
        this.guardianWorkState = guardianWorkState;
        this.guardianWorkPostalCode = guardianWorkPostalCode;
        this.guardianGender = guardianGender;
        this.guardianEthnicity = guardianEthnicity;
        this.guardianCitizenshipStatus = guardianCitizenshipStatus;
        this.guardianCountryOfCitizenship = guardianCountryOfCitizenship;
        this.guardianVisaRequired = guardianVisaRequired;
        this.guardianVisaType = guardianVisaType;
        this.guardianVisaNumber = guardianVisaNumber;
        this.guardianVisaIssueDate = guardianVisaIssueDate;
        this.guardianVisaExpirationDate = guardianVisaExpirationDate;
        this.maritalStatus = maritalStatus;
        this.secondaryGuardianName = secondaryGuardianName;
        this.secondaryGuardianEmail = secondaryGuardianEmail;
        this.secondaryGuardianPhone = secondaryGuardianPhone;
        this.secondaryMailingAddressLine1 = secondaryMailingAddressLine1;
        this.secondaryMailingAddressLine2 = secondaryMailingAddressLine2;
        this.secondaryMailingCity = secondaryMailingCity;
        this.secondaryMailingState = secondaryMailingState;
        this.secondaryMailingPostalCode = secondaryMailingPostalCode;
        this.secondaryEmployerName = secondaryEmployerName;
        this.secondaryWorkPhone = secondaryWorkPhone;
        this.secondaryWorkEmail = secondaryWorkEmail;
        this.secondaryWorkAddressLine1 = secondaryWorkAddressLine1;
        this.secondaryWorkAddressLine2 = secondaryWorkAddressLine2;
        this.secondaryWorkCity = secondaryWorkCity;
        this.secondaryWorkState = secondaryWorkState;
        this.secondaryWorkPostalCode = secondaryWorkPostalCode;
        this.secondaryGender = secondaryGender;
        this.secondaryEthnicity = secondaryEthnicity;
        this.secondaryCitizenshipStatus = secondaryCitizenshipStatus;
        this.secondaryCountryOfCitizenship = secondaryCountryOfCitizenship;
        this.secondaryVisaRequired = secondaryVisaRequired;
        this.secondaryVisaType = secondaryVisaType;
        this.secondaryVisaNumber = secondaryVisaNumber;
        this.secondaryVisaIssueDate = secondaryVisaIssueDate;
        this.secondaryVisaExpirationDate = secondaryVisaExpirationDate;
        this.secondaryGuardianPortalAccess = secondaryGuardianPortalAccess;
        this.primaryGuardianBillingRecipient = primaryGuardianBillingRecipient;
        this.studentCitizenshipStatus = studentCitizenshipStatus;
        this.studentCountryOfCitizenship = studentCountryOfCitizenship;
        this.studentVisaRequired = studentVisaRequired;
        this.studentVisaType = studentVisaType;
        this.studentVisaNumber = studentVisaNumber;
        this.studentVisaIssueDate = studentVisaIssueDate;
        this.studentVisaExpirationDate = studentVisaExpirationDate;
        this.studentF1Required = studentF1Required;
        this.studentI20Status = studentI20Status;
        this.requestedGradeLevel = requestedGradeLevel;
        this.submittedOn = submittedOn;
    }

    public Long getId() {
        return id;
    }

    public FamilyAccount getFamilyAccount() {
        return familyAccount;
    }

    public Student getStudent() {
        return student;
    }

    public Campus getCampus() {
        return campus;
    }

    public EnrollmentRequestType getRequestType() {
        return requestType;
    }

    public EnrollmentRequestStatus getStatus() {
        return status;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public String getGuardianEmail() {
        return guardianEmail;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public String getGuardianMailingAddressLine1() {
        return guardianMailingAddressLine1;
    }

    public String getGuardianMailingAddressLine2() {
        return guardianMailingAddressLine2;
    }

    public String getGuardianMailingCity() {
        return guardianMailingCity;
    }

    public String getGuardianMailingState() {
        return guardianMailingState;
    }

    public String getGuardianMailingPostalCode() {
        return guardianMailingPostalCode;
    }

    public String getGuardianEmployerName() {
        return guardianEmployerName;
    }

    public String getGuardianWorkPhone() {
        return guardianWorkPhone;
    }

    public String getGuardianWorkEmail() {
        return guardianWorkEmail;
    }

    public String getGuardianWorkAddressLine1() {
        return guardianWorkAddressLine1;
    }

    public String getGuardianWorkAddressLine2() {
        return guardianWorkAddressLine2;
    }

    public String getGuardianWorkCity() {
        return guardianWorkCity;
    }

    public String getGuardianWorkState() {
        return guardianWorkState;
    }

    public String getGuardianWorkPostalCode() {
        return guardianWorkPostalCode;
    }

    public String getGuardianGender() {
        return guardianGender;
    }

    public String getGuardianEthnicity() {
        return guardianEthnicity;
    }

    public String getGuardianCitizenshipStatus() {
        return guardianCitizenshipStatus;
    }

    public String getGuardianCountryOfCitizenship() {
        return guardianCountryOfCitizenship;
    }

    public boolean isGuardianVisaRequired() {
        return guardianVisaRequired;
    }

    public String getGuardianVisaType() {
        return guardianVisaType;
    }

    public String getGuardianVisaNumber() {
        return guardianVisaNumber;
    }

    public LocalDate getGuardianVisaIssueDate() {
        return guardianVisaIssueDate;
    }

    public LocalDate getGuardianVisaExpirationDate() {
        return guardianVisaExpirationDate;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public String getSecondaryGuardianName() {
        return secondaryGuardianName;
    }

    public String getSecondaryGuardianEmail() {
        return secondaryGuardianEmail;
    }

    public String getSecondaryGuardianPhone() {
        return secondaryGuardianPhone;
    }

    public String getSecondaryMailingAddressLine1() { return secondaryMailingAddressLine1; }
    public String getSecondaryMailingAddressLine2() { return secondaryMailingAddressLine2; }
    public String getSecondaryMailingCity() { return secondaryMailingCity; }
    public String getSecondaryMailingState() { return secondaryMailingState; }
    public String getSecondaryMailingPostalCode() { return secondaryMailingPostalCode; }
    public String getSecondaryEmployerName() { return secondaryEmployerName; }
    public String getSecondaryWorkPhone() { return secondaryWorkPhone; }
    public String getSecondaryWorkEmail() { return secondaryWorkEmail; }
    public String getSecondaryWorkAddressLine1() { return secondaryWorkAddressLine1; }
    public String getSecondaryWorkAddressLine2() { return secondaryWorkAddressLine2; }
    public String getSecondaryWorkCity() { return secondaryWorkCity; }
    public String getSecondaryWorkState() { return secondaryWorkState; }
    public String getSecondaryWorkPostalCode() { return secondaryWorkPostalCode; }
    public String getSecondaryGender() { return secondaryGender; }
    public String getSecondaryEthnicity() { return secondaryEthnicity; }
    public String getSecondaryCitizenshipStatus() { return secondaryCitizenshipStatus; }
    public String getSecondaryCountryOfCitizenship() { return secondaryCountryOfCitizenship; }
    public boolean isSecondaryVisaRequired() { return secondaryVisaRequired; }
    public String getSecondaryVisaType() { return secondaryVisaType; }
    public String getSecondaryVisaNumber() { return secondaryVisaNumber; }
    public LocalDate getSecondaryVisaIssueDate() { return secondaryVisaIssueDate; }
    public LocalDate getSecondaryVisaExpirationDate() { return secondaryVisaExpirationDate; }

    public boolean isSecondaryGuardianPortalAccess() {
        return secondaryGuardianPortalAccess;
    }

    public boolean isPrimaryGuardianBillingRecipient() {
        return primaryGuardianBillingRecipient;
    }

    public String getStudentCitizenshipStatus() { return studentCitizenshipStatus; }
    public String getStudentCountryOfCitizenship() { return studentCountryOfCitizenship; }
    public boolean isStudentVisaRequired() { return studentVisaRequired; }
    public String getStudentVisaType() { return studentVisaType; }
    public String getStudentVisaNumber() { return studentVisaNumber; }
    public LocalDate getStudentVisaIssueDate() { return studentVisaIssueDate; }
    public LocalDate getStudentVisaExpirationDate() { return studentVisaExpirationDate; }
    public boolean isStudentF1Required() { return studentF1Required; }
    public String getStudentI20Status() { return studentI20Status; }

    public GradeLevel getRequestedGradeLevel() {
        return requestedGradeLevel;
    }

    public LocalDate getSubmittedOn() {
        return submittedOn;
    }

    public String getStudentDisplayName() {
        return studentFirstName + " " + studentLastName;
    }
}
