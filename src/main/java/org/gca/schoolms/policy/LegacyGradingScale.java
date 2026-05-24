package org.gca.schoolms.policy;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "grading_scale")
public class LegacyGradingScale {

    @Id
    @Column(name = "grade_code")
    private String gradeCode;

    @Column(name = "min_percent", nullable = false)
    private BigDecimal minPercent;

    @Column(name = "max_percent", nullable = false)
    private BigDecimal maxPercent;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    protected LegacyGradingScale() {
    }

    public LegacyGradingScale(String gradeCode, BigDecimal minPercent, BigDecimal maxPercent, int displayOrder) {
        this.gradeCode = gradeCode;
        this.minPercent = minPercent;
        this.maxPercent = maxPercent;
        this.displayOrder = displayOrder;
    }

    public String getGradeCode() {
        return gradeCode;
    }

    public BigDecimal getMinPercent() {
        return minPercent;
    }

    public BigDecimal getMaxPercent() {
        return maxPercent;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}
