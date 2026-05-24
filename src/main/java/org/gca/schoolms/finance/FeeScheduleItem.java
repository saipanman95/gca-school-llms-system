package org.gca.schoolms.finance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class FeeScheduleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fee_schedule_id", nullable = false)
    private FeeSchedule feeSchedule;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fee_type_id", nullable = false)
    private FeeType feeType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer sortOrder;

    protected FeeScheduleItem() {
    }

    public FeeScheduleItem(FeeSchedule feeSchedule, FeeType feeType, BigDecimal amount, String description, Integer sortOrder) {
        this.feeSchedule = feeSchedule;
        this.feeType = feeType;
        this.amount = amount;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public FeeSchedule getFeeSchedule() {
        return feeSchedule;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }
}
