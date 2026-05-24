package org.gca.schoolms.integration.powerschool;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "students_local", indexes = {
    @Index(name = "idx_students_local_grade", columnList = "grade_level"),
    @Index(name = "idx_students_local_enroll_status", columnList = "enroll_status")
})
public class PowerSchoolStudent {

    @Id
    @Column(name = "student_id")
    private Integer studentId;

    @Column(nullable = false, unique = true)
    private Integer dcid;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "grade_level")
    private Integer gradeLevel;

    @Column(name = "class_of")
    private Integer classOf;

    @Column(name = "school_id")
    private Integer schoolId;

    @Column(name = "enroll_status")
    private Integer enrollStatus;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Column(name = "exit_date")
    private LocalDate exitDate;

    @Column(name = "cumulative_gpa", precision = 6, scale = 2)
    private BigDecimal cumulativeGpa;

    @Column(name = "exclude_from_rank")
    private Boolean excludeFromRank;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Integer getStudentId() {
        return studentId;
    }

    public Integer getDcid() {
        return dcid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getGradeLevel() {
        return gradeLevel;
    }

    public Integer getClassOf() {
        return classOf;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    public Integer getEnrollStatus() {
        return enrollStatus;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public LocalDate getExitDate() {
        return exitDate;
    }

    public BigDecimal getCumulativeGpa() {
        return cumulativeGpa;
    }

    public Boolean getExcludeFromRank() {
        return excludeFromRank;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
