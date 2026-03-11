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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeLevel requestedGradeLevel;

    @Column(nullable = false)
    private LocalDate submittedOn;

    protected EnrollmentRequest() {
    }

    public EnrollmentRequest(FamilyAccount familyAccount, Student student, Campus campus, EnrollmentRequestType requestType,
                             EnrollmentRequestStatus status, String schoolYear, String studentFirstName,
                             String studentLastName, GradeLevel requestedGradeLevel, LocalDate submittedOn) {
        this.familyAccount = familyAccount;
        this.student = student;
        this.campus = campus;
        this.requestType = requestType;
        this.status = status;
        this.schoolYear = schoolYear;
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
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
