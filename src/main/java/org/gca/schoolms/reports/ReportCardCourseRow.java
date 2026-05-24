package org.gca.schoolms.reports;

public record ReportCardCourseRow(
    String courseName,
    String teacherName,
    String q1Grade,
    String q1Citizenship,
    String q2Grade,
    String q2Citizenship,
    String q3Grade,
    String q3Citizenship,
    String q4Grade,
    String q4Citizenship
) {

    public String q1GradeDisplay() {
        return display(q1Grade);
    }

    public String q1CitizenshipDisplay() {
        return display(q1Citizenship);
    }

    public String q2GradeDisplay() {
        return display(q2Grade);
    }

    public String q2CitizenshipDisplay() {
        return display(q2Citizenship);
    }

    public String q3GradeDisplay() {
        return display(q3Grade);
    }

    public String q3CitizenshipDisplay() {
        return display(q3Citizenship);
    }

    public String q4GradeDisplay() {
        return display(q4Grade);
    }

    public String q4CitizenshipDisplay() {
        return display(q4Citizenship);
    }

    private static String display(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
