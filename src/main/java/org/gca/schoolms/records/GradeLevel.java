package org.gca.schoolms.records;

public enum GradeLevel {
    K4("K4"),
    K5("K5"),
    GRADE_1("1"),
    GRADE_2("2"),
    GRADE_3("3"),
    GRADE_4("4"),
    GRADE_5("5"),
    GRADE_6("6"),
    GRADE_7("7"),
    GRADE_8("8"),
    GRADE_9("9"),
    GRADE_10("10"),
    GRADE_11("11"),
    GRADE_12("12");

    private final String label;

    GradeLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
