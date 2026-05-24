package org.gca.schoolms.integration.powerschool;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "powerschool_grade_scale_item_local", indexes = {
    @Index(name = "idx_ps_grade_item_scale_cutoff", columnList = "grade_scale_id, cutoff_percent"),
    @Index(name = "idx_ps_grade_item_scale_label", columnList = "grade_scale_id, grade_label")
})
public class PowerSchoolGradeScaleItem {

    @Id
    @Column(name = "grade_item_id")
    private Integer gradeItemId;

    @Column(name = "admin_use_only")
    private Boolean adminUseOnly;

    @Column(name = "cutoff_percent", precision = 8, scale = 3)
    private BigDecimal cutoffPercent;

    @Column(name = "default_zero_cutoff")
    private Boolean defaultZeroCutoff;

    private String description;

    @Column(name = "grade_label")
    private String gradeLabel;

    @Column(name = "grade_scale_id")
    private Integer gradeScaleId;

    @Column(name = "is_special")
    private Boolean isSpecial;

    @Column(name = "percent_value", precision = 8, scale = 3)
    private BigDecimal percentValue;

    @Column(name = "points_value", precision = 8, scale = 3)
    private BigDecimal pointsValue;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
