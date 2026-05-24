package org.gca.schoolms.integration.powerschool;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "section_teacher_local", indexes = {
    @Index(name = "idx_section_teacher_section", columnList = "section_id"),
    @Index(name = "idx_section_teacher_teacher", columnList = "teacher_id")
})
public class PowerSchoolSectionTeacher {

    @Id
    @Column(name = "section_teacher_id")
    private Integer sectionTeacherId;

    @Column(name = "section_id")
    private Integer sectionId;

    @Column(name = "teacher_id")
    private Integer teacherId;

    @Column(name = "priority_order")
    private Integer priorityOrder;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}
