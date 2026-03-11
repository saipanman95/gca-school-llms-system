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

    @NotBlank
    private String guardianName;

    @NotBlank
    private String guardianEmail;

    @NotBlank
    private String guardianPhone;

    @NotBlank
    private String guardianMailingAddressLine1;

    private String guardianMailingAddressLine2;

    @NotBlank
    private String guardianMailingCity;

    @NotBlank
    private String guardianMailingState;

    @NotBlank
    private String guardianMailingPostalCode;

    private String guardianEmployerName;

    private String guardianWorkPhone;

    private String guardianWorkEmail;

    private String guardianWorkAddressLine1;

    private String guardianWorkAddressLine2;

    private String guardianWorkCity;

    private String guardianWorkState;

    private String guardianWorkPostalCode;

    private String guardianGender;

    private String guardianEthnicity;

    @NotNull
    private GradeLevel requestedGradeLevel;

    @NotNull
    private Long campusId;

    @NotNull
    private EnrollmentRequestType requestType = EnrollmentRequestType.NEW_STUDENT;

    private Long existingStudentId;

    private boolean reenrollmentPrefill;

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

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianEmail() {
        return guardianEmail;
    }

    public void setGuardianEmail(String guardianEmail) {
        this.guardianEmail = guardianEmail;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
    }

    public String getGuardianMailingAddressLine1() {
        return guardianMailingAddressLine1;
    }

    public void setGuardianMailingAddressLine1(String guardianMailingAddressLine1) {
        this.guardianMailingAddressLine1 = guardianMailingAddressLine1;
    }

    public String getGuardianMailingAddressLine2() {
        return guardianMailingAddressLine2;
    }

    public void setGuardianMailingAddressLine2(String guardianMailingAddressLine2) {
        this.guardianMailingAddressLine2 = guardianMailingAddressLine2;
    }

    public String getGuardianMailingCity() {
        return guardianMailingCity;
    }

    public void setGuardianMailingCity(String guardianMailingCity) {
        this.guardianMailingCity = guardianMailingCity;
    }

    public String getGuardianMailingState() {
        return guardianMailingState;
    }

    public void setGuardianMailingState(String guardianMailingState) {
        this.guardianMailingState = guardianMailingState;
    }

    public String getGuardianMailingPostalCode() {
        return guardianMailingPostalCode;
    }

    public void setGuardianMailingPostalCode(String guardianMailingPostalCode) {
        this.guardianMailingPostalCode = guardianMailingPostalCode;
    }

    public String getGuardianEmployerName() {
        return guardianEmployerName;
    }

    public void setGuardianEmployerName(String guardianEmployerName) {
        this.guardianEmployerName = guardianEmployerName;
    }

    public String getGuardianWorkPhone() {
        return guardianWorkPhone;
    }

    public void setGuardianWorkPhone(String guardianWorkPhone) {
        this.guardianWorkPhone = guardianWorkPhone;
    }

    public String getGuardianWorkEmail() {
        return guardianWorkEmail;
    }

    public void setGuardianWorkEmail(String guardianWorkEmail) {
        this.guardianWorkEmail = guardianWorkEmail;
    }

    public String getGuardianWorkAddressLine1() {
        return guardianWorkAddressLine1;
    }

    public void setGuardianWorkAddressLine1(String guardianWorkAddressLine1) {
        this.guardianWorkAddressLine1 = guardianWorkAddressLine1;
    }

    public String getGuardianWorkAddressLine2() {
        return guardianWorkAddressLine2;
    }

    public void setGuardianWorkAddressLine2(String guardianWorkAddressLine2) {
        this.guardianWorkAddressLine2 = guardianWorkAddressLine2;
    }

    public String getGuardianWorkCity() {
        return guardianWorkCity;
    }

    public void setGuardianWorkCity(String guardianWorkCity) {
        this.guardianWorkCity = guardianWorkCity;
    }

    public String getGuardianWorkState() {
        return guardianWorkState;
    }

    public void setGuardianWorkState(String guardianWorkState) {
        this.guardianWorkState = guardianWorkState;
    }

    public String getGuardianWorkPostalCode() {
        return guardianWorkPostalCode;
    }

    public void setGuardianWorkPostalCode(String guardianWorkPostalCode) {
        this.guardianWorkPostalCode = guardianWorkPostalCode;
    }

    public String getGuardianGender() {
        return guardianGender;
    }

    public void setGuardianGender(String guardianGender) {
        this.guardianGender = guardianGender;
    }

    public String getGuardianEthnicity() {
        return guardianEthnicity;
    }

    public void setGuardianEthnicity(String guardianEthnicity) {
        this.guardianEthnicity = guardianEthnicity;
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

    public boolean isReenrollmentPrefill() {
        return reenrollmentPrefill;
    }

    public void setReenrollmentPrefill(boolean reenrollmentPrefill) {
        this.reenrollmentPrefill = reenrollmentPrefill;
    }
}
