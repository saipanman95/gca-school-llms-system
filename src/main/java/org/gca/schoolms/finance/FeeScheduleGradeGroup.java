package org.gca.schoolms.finance;

import org.gca.schoolms.records.GradeLevel;

public enum FeeScheduleGradeGroup {
    ALL_STUDENTS("All students"),
    DAYCARE("Daycare"),
    ELEMENTARY("Elementary"),
    JUNIOR_HIGH("Junior High"),
    HIGH_SCHOOL("High School");

    private final String label;

    FeeScheduleGradeGroup(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean matches(GradeLevel gradeLevel) {
        if (gradeLevel == null) {
            return false;
        }
        return switch (this) {
            case ALL_STUDENTS -> true;
            case DAYCARE -> gradeLevel == GradeLevel.K4 || gradeLevel == GradeLevel.K5;
            case ELEMENTARY -> switch (gradeLevel) {
                case GRADE_1, GRADE_2, GRADE_3, GRADE_4, GRADE_5, GRADE_6 -> true;
                default -> false;
            };
            case JUNIOR_HIGH -> switch (gradeLevel) {
                case GRADE_7, GRADE_8 -> true;
                default -> false;
            };
            case HIGH_SCHOOL -> switch (gradeLevel) {
                case GRADE_9, GRADE_10, GRADE_11, GRADE_12 -> true;
                default -> false;
            };
        };
    }
}
