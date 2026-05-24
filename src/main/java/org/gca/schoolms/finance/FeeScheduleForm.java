package org.gca.schoolms.finance;

public class FeeScheduleForm {

    private Long id;
    private String name;
    private String schoolYear;
    private FeeScheduleGradeGroup gradeGroup = FeeScheduleGradeGroup.ALL_STUDENTS;
    private Long campusId;
    private String itemLines;
    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public FeeScheduleGradeGroup getGradeGroup() {
        return gradeGroup;
    }

    public void setGradeGroup(FeeScheduleGradeGroup gradeGroup) {
        this.gradeGroup = gradeGroup;
    }

    public Long getCampusId() {
        return campusId;
    }

    public void setCampusId(Long campusId) {
        this.campusId = campusId;
    }

    public String getItemLines() {
        return itemLines;
    }

    public void setItemLines(String itemLines) {
        this.itemLines = itemLines;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
