package org.gca.schoolms.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record GraduationRankingCandidate(
    long studentId,
    String displayName,
    int currentGradeLevel,
    int cohortStartGrade,
    int cohortEndGrade,
    int rank,
    BigDecimal cumulativeAverage,
    BigDecimal cumulativeGpa,
    BigDecimal minimumNumericGrade,
    int quarterGradeCount,
    int distinctGradesCovered,
    boolean eligible,
    String awardLabel,
    String ineligibilityReason
) {

    public String cumulativeAverageDisplay() {
        return cumulativeAverage == null ? "-" : cumulativeAverage.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public String cumulativeGpaDisplay() {
        return cumulativeGpa == null ? "-" : cumulativeGpa.setScale(4, RoundingMode.HALF_UP).toPlainString();
    }

    public String minimumNumericGradeDisplay() {
        return minimumNumericGrade == null ? "-" : minimumNumericGrade.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
