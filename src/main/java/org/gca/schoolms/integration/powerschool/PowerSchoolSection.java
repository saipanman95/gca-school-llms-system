package org.gca.schoolms.integration.powerschool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "sections_local", indexes = {
    @Index(name = "idx_sections_course_number", columnList = "course_number")
})
public class PowerSchoolSection {

    @Id
    @Column(name = "section_id")
    private Integer sectionId;

    private Integer dcid;

    @Column(name = "course_number")
    private String courseNumber;

    @Column(name = "grade_level")
    private String gradeLevel;

    @Column(name = "teacher_id")
    private Integer teacherId;

    @Column(name = "teacher_descr")
    private String teacherDescr;

    @Column(name = "school_id")
    private Integer schoolId;

    @Column(name = "term_id")
    private Integer termId;

    @Column(name = "grade_scale_id")
    private Integer gradeScaleId;
}
