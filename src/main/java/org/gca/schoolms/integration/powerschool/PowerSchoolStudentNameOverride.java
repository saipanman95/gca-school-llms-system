package org.gca.schoolms.integration.powerschool;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "student_name_override", uniqueConstraints = {
    @UniqueConstraint(name = "uq_student_id", columnNames = "student_id")
})
public class PowerSchoolStudentNameOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "override_id")
    private Long overrideId;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "legal_first_name", nullable = false)
    private String legalFirstName;

    @Column(name = "legal_middle_name")
    private String legalMiddleName;

    @Column(name = "legal_last_name", nullable = false)
    private String legalLastName;

    private String reason;

    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
