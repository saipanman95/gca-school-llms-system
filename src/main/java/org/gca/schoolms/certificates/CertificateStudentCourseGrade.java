package org.gca.schoolms.certificates;

import java.math.BigDecimal;

public record CertificateStudentCourseGrade(
    Long studentId,
    String studentLName,
    String studentFName,
    String studentMName,
    Integer gradeLevel,
    String courseName,
    BigDecimal percent,
    String letterGrade,
    Boolean excludeFromGpa,
    Long teacherId,
    String teacherName,
    String teacherLastName,
    String teacherFirstName,
    BigDecimal gpaPoints,
    String gradeScaleName,
    String academicTrack,
    BigDecimal computedGpa
) {
    public String computedGpaDisplay() {
        return computedGpa == null ? "-" : computedGpa.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}
