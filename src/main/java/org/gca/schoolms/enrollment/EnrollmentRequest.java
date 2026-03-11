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

    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    private String secondaryGuardianName;

    private String secondaryGuardianEmail;

    private String secondaryGuardianPhone;

    @Column(nullable = false)
    private boolean secondaryGuardianPortalAccess;

    @Column(nullable = false)
    private boolean primaryGuardianBillingRecipient;

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
                             String guardianEthnicity, MaritalStatus maritalStatus, String secondaryGuardianName,
                             String secondaryGuardianEmail, String secondaryGuardianPhone,
                             boolean secondaryGuardianPortalAccess, boolean primaryGuardianBillingRecipient,
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
        this.maritalStatus = maritalStatus;
        this.secondaryGuardianName = secondaryGuardianName;
        this.secondaryGuardianEmail = secondaryGuardianEmail;
        this.secondaryGuardianPhone = secondaryGuardianPhone;
        this.secondaryGuardianPortalAccess = secondaryGuardianPortalAccess;
        this.primaryGuardianBillingRecipient = primaryGuardianBillingRecipient;
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

    public boolean isSecondaryGuardianPortalAccess() {
        return secondaryGuardianPortalAccess;
    }

    public boolean isPrimaryGuardianBillingRecipient() {
        return primaryGuardianBillingRecipient;
    }

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
