package org.gca.schoolms.records;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import org.gca.schoolms.finance.FamilyAccount;
import org.gca.schoolms.organization.Campus;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentNumber;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeLevel gradeLevel;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "family_account_id", nullable = false)
    private FamilyAccount familyAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentStatus status = StudentStatus.ACTIVE;

    protected Student() {
    }

    public Student(String studentNumber, String firstName, String lastName, LocalDate dateOfBirth, GradeLevel gradeLevel,
                   Campus campus, FamilyAccount familyAccount, StudentStatus status) {
        this.studentNumber = studentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gradeLevel = gradeLevel;
        this.campus = campus;
        this.familyAccount = familyAccount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public GradeLevel getGradeLevel() {
        return gradeLevel;
    }

    public Campus getCampus() {
        return campus;
    }

    public FamilyAccount getFamilyAccount() {
        return familyAccount;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public String getDisplayName() {
        return firstName + " " + lastName;
    }
}
