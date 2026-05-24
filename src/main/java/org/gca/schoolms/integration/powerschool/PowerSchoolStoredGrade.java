package org.gca.schoolms.integration.powerschool;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "stored_grades_local", indexes = {
    @Index(name = "idx_stored_grades_student_term", columnList = "student_id, store_code"),
    @Index(name = "idx_stored_grades_date", columnList = "date_stored")
})
public class PowerSchoolStoredGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Integer dcid;

    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "grade_level")
    private Integer gradeLevel;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "course_department")
    private String courseDepartment;

    @Column(name = "section_id")
    private Integer sectionId;

    @Column(name = "store_code")
    private String storeCode;

    @Column(precision = 6, scale = 2)
    private BigDecimal percent;

    @Column(name = "letter_grade")
    private String letterGrade;

    @Column(name = "earned_credit", precision = 8, scale = 2)
    private BigDecimal earnedCredit;

    @Column(name = "gpa_points", precision = 8, scale = 2)
    private BigDecimal gpaPoints;

    @Column(name = "grade_scale_name")
    private String gradeScaleName;

    @Column(name = "exclude_from_gpa")
    private Boolean excludeFromGpa;

    @Column(name = "exclude_from_honor_roll")
    private Boolean excludeFromHonorRoll;

    @Column(name = "school_id")
    private Integer schoolId;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "date_stored")
    private LocalDate dateStored;
}
