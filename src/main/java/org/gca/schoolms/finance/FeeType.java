package org.gca.schoolms.finance;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FeeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(precision = 12, scale = 2)
    private BigDecimal defaultAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeBillingSchedule billingSchedule = FeeBillingSchedule.ONE_TIME;

    private Integer billingMonthCount;

    private Integer maxAssessmentsPerStudentPerSchoolYear;

    @Column(nullable = false)
    private boolean active = true;

    protected FeeType() {
    }

    public FeeType(String code, String name, BigDecimal defaultAmount, boolean active) {
        this(code, name, defaultAmount, FeeBillingSchedule.ONE_TIME, null, null, active);
    }

    public FeeType(String code, String name, BigDecimal defaultAmount,
                   Integer maxAssessmentsPerStudentPerSchoolYear, boolean active) {
        this(code, name, defaultAmount, FeeBillingSchedule.ONE_TIME, null, maxAssessmentsPerStudentPerSchoolYear, active);
    }

    public FeeType(String code, String name, BigDecimal defaultAmount,
                   FeeBillingSchedule billingSchedule, Integer billingMonthCount,
                   Integer maxAssessmentsPerStudentPerSchoolYear, boolean active) {
        this.code = code;
        this.name = name;
        this.defaultAmount = defaultAmount;
        this.billingSchedule = billingSchedule;
        this.billingMonthCount = billingMonthCount;
        this.maxAssessmentsPerStudentPerSchoolYear = maxAssessmentsPerStudentPerSchoolYear;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void update(String code, String name, BigDecimal defaultAmount,
                       FeeBillingSchedule billingSchedule, Integer billingMonthCount,
                       Integer maxAssessmentsPerStudentPerSchoolYear) {
        this.code = code;
        this.name = name;
        this.defaultAmount = defaultAmount;
        this.billingSchedule = billingSchedule;
        this.billingMonthCount = billingMonthCount;
        this.maxAssessmentsPerStudentPerSchoolYear = maxAssessmentsPerStudentPerSchoolYear;
    }

    public BigDecimal getDefaultAmount() {
        return defaultAmount;
    }

    public boolean isActive() {
        return active;
    }

    public FeeBillingSchedule getBillingSchedule() {
        return billingSchedule;
    }

    public Integer getBillingMonthCount() {
        return billingMonthCount;
    }

    public Integer getMaxAssessmentsPerStudentPerSchoolYear() {
        return maxAssessmentsPerStudentPerSchoolYear;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
