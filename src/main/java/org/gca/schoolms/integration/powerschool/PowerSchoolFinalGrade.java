package org.gca.schoolms.integration.powerschool;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "pg_final_grades_local", indexes = {
    @Index(name = "idx_pg_final_grades_student_term", columnList = "student_id, final_grade_name"),
    @Index(name = "idx_pg_final_grades_section", columnList = "section_id")
})
public class PowerSchoolFinalGrade {

    @Id
    @Column(name = "pg_final_grade_id")
    private Integer finalGradeId;

    private Integer dcid;

    private String citizenship;

    @Column(name = "final_grade_name")
    private String finalGradeName;

    @Column(name = "grade_value")
    private String gradeValue;

    @Column(precision = 6, scale = 2)
    private BigDecimal percent;

    @Column(name = "section_id")
    private Integer sectionId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "student_id")
    private Integer studentId;
}
