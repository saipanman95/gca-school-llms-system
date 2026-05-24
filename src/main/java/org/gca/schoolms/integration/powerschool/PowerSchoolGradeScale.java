package org.gca.schoolms.integration.powerschool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "powerschool_grade_scale_local", indexes = {
    @Index(name = "idx_ps_grade_scale_name", columnList = "name")
})
public class PowerSchoolGradeScale {

    @Id
    @Column(name = "grade_scale_id")
    private Integer gradeScaleId;

    @Column(name = "can_modify")
    private Boolean canModify;

    @Column(name = "content_group_id")
    private Integer contentGroupId;

    private String description;

    @Column(name = "grading_scale")
    private String gradingScale;

    @Column(name = "is_district_default")
    private Boolean isDistrictDefault;

    @Column(name = "is_numeric")
    private Boolean isNumeric;

    private String name;

    @Column(name = "numeric_max")
    private java.math.BigDecimal numericMax;

    @Column(name = "numeric_min")
    private java.math.BigDecimal numericMin;

    @Column(name = "numeric_precision")
    private Integer numericPrecision;

    @Column(name = "numeric_scale")
    private Integer numericScale;

    @Column(name = "parent_grade_scale_id")
    private Integer parentGradeScaleId;

    @Column(name = "school_id")
    private Integer schoolId;

    @Column(name = "teacher_id")
    private Integer teacherId;
}
