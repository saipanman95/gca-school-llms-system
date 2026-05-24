package org.gca.schoolms.reports;

import java.util.List;

public record GraduationRankingCohortView(
    int currentGradeLevel,
    String cohortLabel,
    int startGrade,
    int endGrade,
    int requiredMinimumNumericGrade,
    List<GraduationRankingCandidate> candidates
) {

    public String gradeWindowLabel() {
        if (startGrade == endGrade) {
            return gradeLabel(startGrade);
        }
        return gradeLabel(startGrade) + "-" + gradeLabel(endGrade);
    }

    private static String gradeLabel(int gradeLevel) {
        if (gradeLevel == 0) {
            return "K5";
        }
        return gradeLevel + ordinalSuffix(gradeLevel);
    }

    private static String ordinalSuffix(int value) {
        if (value % 100 >= 11 && value % 100 <= 13) {
            return "th";
        }
        return switch (value % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }
}
