package org.gca.schoolms.finance;

import java.math.BigDecimal;

public class FeeTypeForm {

    private Long id;
    private String code;
    private String name;
    private BigDecimal defaultAmount;
    private FeeBillingSchedule billingSchedule = FeeBillingSchedule.ONE_TIME;
    private Integer billingMonthCount;
    private Integer maxAssessmentsPerStudentPerSchoolYear;
    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getDefaultAmount() {
        return defaultAmount;
    }

    public void setDefaultAmount(BigDecimal defaultAmount) {
        this.defaultAmount = defaultAmount;
    }

    public FeeBillingSchedule getBillingSchedule() {
        return billingSchedule;
    }

    public void setBillingSchedule(FeeBillingSchedule billingSchedule) {
        this.billingSchedule = billingSchedule;
    }

    public Integer getBillingMonthCount() {
        return billingMonthCount;
    }

    public void setBillingMonthCount(Integer billingMonthCount) {
        this.billingMonthCount = billingMonthCount;
    }

    public Integer getMaxAssessmentsPerStudentPerSchoolYear() {
        return maxAssessmentsPerStudentPerSchoolYear;
    }

    public void setMaxAssessmentsPerStudentPerSchoolYear(Integer maxAssessmentsPerStudentPerSchoolYear) {
        this.maxAssessmentsPerStudentPerSchoolYear = maxAssessmentsPerStudentPerSchoolYear;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
