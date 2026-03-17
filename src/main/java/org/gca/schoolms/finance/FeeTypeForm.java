package org.gca.schoolms.finance;

import java.math.BigDecimal;

public class FeeTypeForm {

    private Long id;
    private String code;
    private String name;
    private BigDecimal defaultAmount;
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
