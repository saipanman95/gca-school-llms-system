package org.gca.schoolms.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.gca.schoolms.enrollment.EnrollmentRequestType;
import org.gca.schoolms.records.GradeLevel;

public class GuardianEnrollmentForm {

    @NotBlank
    private String schoolYear = "2026-2027";

    @NotBlank
    private String studentFirstName;

    @NotBlank
    private String studentLastName;

    @NotNull
    private GradeLevel requestedGradeLevel;

    @NotNull
    private Long campusId;

    @NotNull
    private EnrollmentRequestType requestType = EnrollmentRequestType.NEW_STUDENT;

    private Long existingStudentId;

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }

    public GradeLevel getRequestedGradeLevel() {
        return requestedGradeLevel;
    }

    public void setRequestedGradeLevel(GradeLevel requestedGradeLevel) {
        this.requestedGradeLevel = requestedGradeLevel;
    }

    public Long getCampusId() {
        return campusId;
    }

    public void setCampusId(Long campusId) {
        this.campusId = campusId;
    }

    public EnrollmentRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(EnrollmentRequestType requestType) {
        this.requestType = requestType;
    }

    public Long getExistingStudentId() {
        return existingStudentId;
    }

    public void setExistingStudentId(Long existingStudentId) {
        this.existingStudentId = existingStudentId;
    }
}
