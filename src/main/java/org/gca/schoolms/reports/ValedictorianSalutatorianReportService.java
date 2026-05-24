package org.gca.schoolms.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ValedictorianSalutatorianReportService {

    private static final Set<String> QUARTER_TERMS = Set.of("Q1", "Q2", "Q3", "Q4");
    private static final int MINIMUM_NUMERIC_GRADE = 83;
    private static final int CONSISTENT_PRINCIPALS_LIST_MINIMUM = 95;
    private static final int CONSISTENT_HONOR_ROLL_MINIMUM = 80;
    private static final BigDecimal CONSISTENT_HIGH_GPA_MINIMUM = BigDecimal.valueOf(3.5);
    private static final Set<String> EXCLUDED_GPA_SCALE_NAMES = Set.of(
        "Pass Fail Grading",
        "Citizenship Course",
        "Citizenship",
        "K4 Grade Scale",
        "A Numeric Example Scale"
    );
    private static final List<CohortDefinition> COHORT_DEFINITIONS = List.of(
        new CohortDefinition(12, 9, 12, "12th Grade Cohort", RankingMetric.CUMULATIVE_AVERAGE, AwardMode.LEGACY_GRADUATION),
        new CohortDefinition(8, 8, 8, "8th Grade Cohort", RankingMetric.CUMULATIVE_GPA, AwardMode.TOP_THREE_NUMERIC_AVERAGE),
        new CohortDefinition(5, 3, 5, "5th Grade Cohort", RankingMetric.CUMULATIVE_AVERAGE, AwardMode.LEGACY_GRADUATION),
        new CohortDefinition(0, 0, 0, "K5 Cohort", RankingMetric.CUMULATIVE_GPA, AwardMode.TOP_THREE_NUMERIC_AVERAGE)
    );

    private final JdbcTemplate jdbcTemplate;

    public ValedictorianSalutatorianReportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<GraduationRankingCohortView> loadReport(String evaluationQuarter, LocalDate cutoffDate) {
        return COHORT_DEFINITIONS.stream()
            .map(definition -> buildCohort(definition, evaluationQuarter, cutoffDate))
            .toList();
    }

    public GraduationRankingStudentBreakdown loadStudentBreakdown(Long studentId, String evaluationQuarter, LocalDate cutoffDate) {
        if (studentId == null) {
            return null;
        }
        Integer currentGradeLevel = jdbcTemplate.query(
            """
                SELECT grade_level
                FROM students_local
                WHERE student_id = ?
                """,
            new Object[]{studentId},
            rs -> rs.next() ? rs.getInt("grade_level") : null
        );
        if (currentGradeLevel == null) {
            return null;
        }
        CohortDefinition definition = definitionFor(currentGradeLevel);
        if (definition == null) {
            return null;
        }
        int startGrade = definition.startGrade();
        int endGrade = definition.endGrade();

        List<RawBreakdownRow> rows = jdbcTemplate.query(
            """
                SELECT
                    s.student_id,
                    s.first_name,
                    s.middle_name,
                    s.last_name,
                    s.grade_level AS current_grade_level,
                    sg.grade_level AS historical_grade_level,
                    sg.store_code,
                    sg.course_name,
                    sg.percent,
                    sg.gpa_points,
                    sg.grade_scale_name
                FROM students_local s
                JOIN stored_grades_local sg
                  ON sg.student_id = s.student_id
                WHERE s.student_id = ?
                  AND COALESCE(s.exclude_from_rank, FALSE) = FALSE
                  AND sg.grade_level BETWEEN ? AND ?
                  AND sg.store_code IN ('Q1', 'Q2', 'Q3', 'Q4')
                  AND sg.date_stored <= ?
                  AND sg.percent IS NOT NULL
                  AND COALESCE(sg.exclude_from_gpa, FALSE) = FALSE
                  AND COALESCE(sg.exclude_from_honor_roll, FALSE) = FALSE
                ORDER BY sg.grade_level, sg.store_code, sg.course_name
                """,
            new Object[]{studentId, startGrade, endGrade, cutoffDate},
            (rs, rowNum) -> new RawBreakdownRow(
                rs.getLong("student_id"),
                buildDisplayName(
                    rs.getString("first_name"),
                    rs.getString("middle_name"),
                    rs.getString("last_name")
                ),
                rs.getInt("current_grade_level"),
                rs.getInt("historical_grade_level"),
                rs.getString("store_code"),
                rs.getString("course_name"),
                rs.getBigDecimal("percent"),
                rs.getBigDecimal("gpa_points"),
                rs.getString("grade_scale_name")
            )
        );

        ScaleRuleLookup scaleRuleLookup = loadScaleRuleLookup();
        StudentBreakdownAccumulator accumulator = null;
        for (RawBreakdownRow row : rows) {
            if (!shouldInclude(row.historicalGradeLevel(), row.storeCode(), row.currentGradeLevel(), evaluationQuarter)) {
                continue;
            }
            if (isExcludedScale(row.gradeScaleName())) {
                continue;
            }
            if (accumulator == null) {
                accumulator = new StudentBreakdownAccumulator(
                    row.studentId(),
                    row.displayName(),
                    row.currentGradeLevel(),
                    startGrade,
                    endGrade
                );
            }
            accumulator.accept(
                row.historicalGradeLevel(),
                row.storeCode(),
                row.courseName(),
                row.percent(),
                scaleRuleLookup.resolveGpaPoints(
                    new RawCandidateRow(
                        row.studentId(),
                        row.displayName(),
                        row.currentGradeLevel(),
                        row.historicalGradeLevel(),
                        row.storeCode(),
                        cutoffDate,
                        row.percent(),
                        row.storedGpaPoints(),
                        row.gradeScaleName()
                    )
                ),
                row.gradeScaleName()
            );
        }
        return accumulator == null ? null : accumulator.toView();
    }

    private GraduationRankingCohortView buildCohort(CohortDefinition definition, String evaluationQuarter, LocalDate cutoffDate) {
        List<RawCandidateRow> rows = jdbcTemplate.query(
            """
                SELECT
                    s.student_id,
                    s.first_name,
                    s.middle_name,
                    s.last_name,
                    s.grade_level AS current_grade_level,
                    sg.grade_level AS historical_grade_level,
                    sg.store_code,
                    sg.date_stored,
                    sg.percent,
                    sg.gpa_points,
                    sg.grade_scale_name
                FROM students_local s
                JOIN stored_grades_local sg
                  ON sg.student_id = s.student_id
                WHERE s.grade_level = ?
                  AND s.enroll_status = 0
                  AND COALESCE(s.exclude_from_rank, FALSE) = FALSE
                  AND sg.grade_level BETWEEN ? AND ?
                  AND sg.store_code IN ('Q1', 'Q2', 'Q3', 'Q4')
                  AND sg.date_stored <= ?
                  AND sg.percent IS NOT NULL
                  AND COALESCE(sg.exclude_from_gpa, FALSE) = FALSE
                  AND COALESCE(sg.exclude_from_honor_roll, FALSE) = FALSE
                ORDER BY s.last_name, s.first_name, s.middle_name, sg.grade_level, sg.date_stored, sg.store_code, sg.percent
                """,
            new Object[]{definition.currentGradeLevel(), definition.startGrade(), definition.endGrade(), cutoffDate},
            (rs, rowNum) -> new RawCandidateRow(
                rs.getLong("student_id"),
                buildDisplayName(
                    rs.getString("first_name"),
                    rs.getString("middle_name"),
                    rs.getString("last_name")
                ),
                rs.getInt("current_grade_level"),
                rs.getInt("historical_grade_level"),
                rs.getString("store_code"),
                rs.getObject("date_stored", LocalDate.class),
                rs.getBigDecimal("percent"),
                rs.getBigDecimal("gpa_points"),
                rs.getString("grade_scale_name")
            )
        );

        ScaleRuleLookup scaleRuleLookup = loadScaleRuleLookup();
        Set<String> allowedCurrentYearTerms = allowedQuarterTerms(evaluationQuarter);

        Map<Long, CandidateAccumulator> candidateByStudentId = new LinkedHashMap<>();
        for (RawCandidateRow row : rows) {
            if (!shouldInclude(row, evaluationQuarter)) {
                continue;
            }
            if (isExcludedScale(row.gradeScaleName())) {
                continue;
            }
            CandidateAccumulator accumulator = candidateByStudentId.computeIfAbsent(
                row.studentId(),
                ignored -> new CandidateAccumulator(
                    row.studentId(),
                    row.displayName(),
                    row.currentGradeLevel(),
                    definition.startGrade(),
                    definition.endGrade(),
                    definition,
                    allowedCurrentYearTerms
                )
            );
            accumulator.accept(row.historicalGradeLevel(), row.percent(), scaleRuleLookup.resolveGpaPoints(row), row.storeCode());
        }

        Comparator<CandidateAccumulator> rankingComparator = rankingComparator();

        List<CandidateAccumulator> rankedCandidates = candidateByStudentId.values().stream()
            .filter(candidate -> candidate.rankingValue() != null)
            .sorted(rankingComparator)
            .toList();

        int rank = 1;
        for (CandidateAccumulator accumulator : rankedCandidates) {
            accumulator.rank = rank++;
        }

        List<GraduationRankingCandidate> candidates = candidateByStudentId.values().stream()
            .sorted(rankingComparator)
            .map(CandidateAccumulator::toView)
            .toList();

        return new GraduationRankingCohortView(
            definition.currentGradeLevel(),
            definition.cohortLabel(),
            definition.startGrade(),
            definition.endGrade(),
            MINIMUM_NUMERIC_GRADE,
            candidates
        );
    }

    private Comparator<CandidateAccumulator> rankingComparator() {
        return Comparator.comparing(
                CandidateAccumulator::rankingValue,
                Comparator.nullsLast(Comparator.reverseOrder())
            )
            .thenComparing(CandidateAccumulator::cumulativeAverage, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(CandidateAccumulator::cumulativeGpa, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(candidate -> candidate.displayName, String.CASE_INSENSITIVE_ORDER);
    }

    private CohortDefinition definitionFor(int currentGradeLevel) {
        return COHORT_DEFINITIONS.stream()
            .filter(definition -> definition.currentGradeLevel() == currentGradeLevel)
            .findFirst()
            .orElse(null);
    }

    private boolean shouldInclude(RawCandidateRow row, String evaluationQuarter) {
        return shouldInclude(row.historicalGradeLevel(), row.storeCode(), row.currentGradeLevel(), evaluationQuarter);
    }

    private boolean shouldInclude(int historicalGradeLevel, String storeCode, int currentGradeLevel, String evaluationQuarter) {
        if (historicalGradeLevel < currentGradeLevel) {
            return true;
        }
        return allowedQuarterTerms(evaluationQuarter).contains(storeCode);
    }

    private boolean isExcludedScale(String gradeScaleName) {
        return gradeScaleName != null && EXCLUDED_GPA_SCALE_NAMES.contains(gradeScaleName);
    }

    private Set<String> allowedQuarterTerms(String evaluationQuarter) {
        return switch (evaluationQuarter) {
            case "Q1" -> Set.of("Q1");
            case "Q2" -> Set.of("Q1", "Q2");
            case "Q3" -> Set.of("Q1", "Q2", "Q3");
            default -> QUARTER_TERMS;
        };
    }

    private String buildDisplayName(String firstName, String middleName, String lastName) {
        List<String> parts = new ArrayList<>();
        if (firstName != null && !firstName.isBlank()) {
            parts.add(firstName.trim());
        }
        if (middleName != null && !middleName.isBlank()) {
            parts.add(middleName.trim());
        }
        if (lastName != null && !lastName.isBlank()) {
            parts.add(lastName.trim());
        }
        return String.join(" ", parts);
    }

    private record RawCandidateRow(
        long studentId,
        String displayName,
        int currentGradeLevel,
        int historicalGradeLevel,
        String storeCode,
        LocalDate dateStored,
        BigDecimal percent,
        BigDecimal storedGpaPoints,
        String gradeScaleName
    ) {
    }

    private record RawBreakdownRow(
        long studentId,
        String displayName,
        int currentGradeLevel,
        int historicalGradeLevel,
        String storeCode,
        String courseName,
        BigDecimal percent,
        BigDecimal storedGpaPoints,
        String gradeScaleName
    ) {
    }

    private ScaleRuleLookup loadScaleRuleLookup() {
        Map<String, List<ScaleRule>> rulesByScaleName = new HashMap<>();
        jdbcTemplate.query(
            """
                SELECT
                    gs.name,
                    gi.cutoff_percent,
                    gi.points_value
                FROM powerschool_grade_scale_local gs
                JOIN powerschool_grade_scale_item_local gi
                  ON gi.grade_scale_id = gs.grade_scale_id
                WHERE COALESCE(gs.is_numeric, FALSE) = TRUE
                  AND COALESCE(gi.is_special, FALSE) = FALSE
                  AND gi.cutoff_percent IS NOT NULL
                  AND gi.points_value IS NOT NULL
                ORDER BY gs.name, gi.cutoff_percent DESC
                """,
            rs -> {
                String name = rs.getString("name");
                if (name == null || name.isBlank()) {
                    return;
                }
                rulesByScaleName.computeIfAbsent(name, ignored -> new ArrayList<>()).add(
                    new ScaleRule(
                        rs.getBigDecimal("cutoff_percent"),
                        rs.getBigDecimal("points_value")
                    )
                );
            }
        );
        return new ScaleRuleLookup(rulesByScaleName);
    }

    private record ScaleRule(BigDecimal cutoffPercent, BigDecimal pointsValue) {
    }

    private enum RankingMetric {
        CUMULATIVE_AVERAGE,
        CUMULATIVE_GPA
    }

    private enum AwardMode {
        LEGACY_GRADUATION,
        TOP_THREE_NUMERIC_AVERAGE
    }

    private record CohortDefinition(
        int currentGradeLevel,
        int startGrade,
        int endGrade,
        String cohortLabel,
        RankingMetric rankingMetric,
        AwardMode awardMode
    ) {
    }

    private static final class ScaleRuleLookup {
        private final Map<String, List<ScaleRule>> rulesByScaleName;

        private ScaleRuleLookup(Map<String, List<ScaleRule>> rulesByScaleName) {
            this.rulesByScaleName = rulesByScaleName;
        }

        private BigDecimal resolveGpaPoints(RawCandidateRow row) {
            if (row.percent() == null) {
                return row.storedGpaPoints();
            }
            List<ScaleRule> rules = row.gradeScaleName() == null ? null : rulesByScaleName.get(row.gradeScaleName());
            if (rules != null) {
                for (ScaleRule rule : rules) {
                    if (row.percent().compareTo(rule.cutoffPercent()) >= 0) {
                        return rule.pointsValue();
                    }
                }
            }
            return row.storedGpaPoints();
        }
    }

    private static final class CandidateAccumulator {
        private final long studentId;
        private final String displayName;
        private final int currentGradeLevel;
        private final int startGrade;
        private final int endGrade;
        private final CohortDefinition cohortDefinition;
        private final Set<String> allowedCurrentYearTerms;
        private final Set<Integer> coveredGrades = new java.util.TreeSet<>();
        private final Set<String> currentYearQuartersCovered = new LinkedHashSet<>();
        private BigDecimal minimumNumericGrade;
        private BigDecimal currentYearMinimumNumericGrade;
        private final Map<GradeQuarterKey, QuarterAccumulator> quarterAccumulators = new LinkedHashMap<>();
        private int rank;

        private CandidateAccumulator(
            long studentId,
            String displayName,
            int currentGradeLevel,
            int startGrade,
            int endGrade,
            CohortDefinition cohortDefinition,
            Set<String> allowedCurrentYearTerms
        ) {
            this.studentId = studentId;
            this.displayName = displayName;
            this.currentGradeLevel = currentGradeLevel;
            this.startGrade = startGrade;
            this.endGrade = endGrade;
            this.cohortDefinition = cohortDefinition;
            this.allowedCurrentYearTerms = allowedCurrentYearTerms;
        }

        private void accept(int historicalGradeLevel, BigDecimal percent, BigDecimal gpaPoints, String storeCode) {
            coveredGrades.add(historicalGradeLevel);
            if (percent != null && (minimumNumericGrade == null || percent.compareTo(minimumNumericGrade) < 0)) {
                minimumNumericGrade = percent;
            }
            if (historicalGradeLevel == currentGradeLevel && allowedCurrentYearTerms.contains(storeCode)) {
                currentYearQuartersCovered.add(storeCode);
                if (percent != null && (currentYearMinimumNumericGrade == null || percent.compareTo(currentYearMinimumNumericGrade) < 0)) {
                    currentYearMinimumNumericGrade = percent;
                }
            }
            if (gpaPoints == null) {
                return;
            }
            quarterAccumulators.computeIfAbsent(
                new GradeQuarterKey(historicalGradeLevel, storeCode),
                ignored -> new QuarterAccumulator()
            ).accept(gpaPoints, percent);
        }

        private boolean isEligible() {
            return distinctGradesCovered() == requiredGradeCount()
                && minimumNumericGrade != null
                && minimumNumericGrade.compareTo(BigDecimal.valueOf(MINIMUM_NUMERIC_GRADE)) >= 0
                && quarterGradeCount() > 0;
        }

        private int quarterGradeCount() {
            return quarterAccumulators.size();
        }

        private int distinctGradesCovered() {
            return coveredGrades.size();
        }

        private int requiredGradeCount() {
            return (endGrade - startGrade) + 1;
        }

        private BigDecimal cumulativeAverage() {
            List<BigDecimal> yearlyNumericAverages = yearlyNumericAverages();
            if (yearlyNumericAverages.isEmpty()) {
                return null;
            }
            return average(yearlyNumericAverages);
        }

        private BigDecimal cumulativeGpa() {
            List<BigDecimal> yearlyGpas = yearlyGpas();
            if (yearlyGpas.isEmpty()) {
                return null;
            }
            return average(yearlyGpas);
        }

        private List<BigDecimal> yearlyGpas() {
            Map<Integer, List<BigDecimal>> byGrade = new LinkedHashMap<>();
            for (Map.Entry<GradeQuarterKey, QuarterAccumulator> entry : quarterAccumulators.entrySet()) {
                BigDecimal quarterGpa = entry.getValue().average();
                if (quarterGpa == null) {
                    continue;
                }
                byGrade.computeIfAbsent(entry.getKey().gradeLevel(), ignored -> new ArrayList<>()).add(quarterGpa);
            }
            List<BigDecimal> yearlyGpas = new ArrayList<>();
            for (int grade = startGrade; grade <= endGrade; grade++) {
                List<BigDecimal> quarterGpas = byGrade.get(grade);
                if (quarterGpas == null || quarterGpas.isEmpty()) {
                    continue;
                }
                yearlyGpas.add(average(quarterGpas));
            }
            return yearlyGpas;
        }

        private List<BigDecimal> yearlyNumericAverages() {
            Map<Integer, List<BigDecimal>> byGrade = new LinkedHashMap<>();
            for (Map.Entry<GradeQuarterKey, QuarterAccumulator> entry : quarterAccumulators.entrySet()) {
                BigDecimal quarterNumericAverage = entry.getValue().numericAverage();
                if (quarterNumericAverage == null) {
                    continue;
                }
                byGrade.computeIfAbsent(entry.getKey().gradeLevel(), ignored -> new ArrayList<>()).add(quarterNumericAverage);
            }
            List<BigDecimal> yearlyNumericAverages = new ArrayList<>();
            for (int grade = startGrade; grade <= endGrade; grade++) {
                List<BigDecimal> quarterNumericAverages = byGrade.get(grade);
                if (quarterNumericAverages == null || quarterNumericAverages.isEmpty()) {
                    continue;
                }
                yearlyNumericAverages.add(average(quarterNumericAverages));
            }
            return yearlyNumericAverages;
        }

        private String awardLabel() {
            return switch (cohortDefinition.awardMode()) {
                case LEGACY_GRADUATION -> {
                    Set<String> labels = new LinkedHashSet<>();
                    String rankAward = legacyGraduationAwardLabel();
                    if (!rankAward.isBlank()) {
                        labels.add(rankAward);
                    }
                    if (isConsistentPrincipalsListEligible()) {
                        labels.add("CPA");
                    } else if (isConsistentHonorRollEligible()) {
                        labels.add("CHR");
                    }
                    if (isConsistentHighGpaAwardEligible()) {
                        labels.add("CHGPA");
                    }
                    yield String.join(", ", labels);
                }
                case TOP_THREE_NUMERIC_AVERAGE -> {
                    Set<String> labels = new LinkedHashSet<>();
                    String rankAward = topThreeNumericAverageAwardLabel();
                    if (!rankAward.isBlank()) {
                        labels.add(rankAward);
                    }
                    if (isConsistentPrincipalsListEligible()) {
                        labels.add("CPA");
                    } else if (isConsistentHonorRollEligible()) {
                        labels.add("CHR");
                    }
                    yield String.join(", ", labels);
                }
            };
        }

        private BigDecimal rankingValue() {
            return switch (cohortDefinition.rankingMetric()) {
                case CUMULATIVE_AVERAGE -> cumulativeAverage();
                case CUMULATIVE_GPA -> cumulativeGpa();
            };
        }

        private String legacyGraduationAwardLabel() {
            return switch (rank) {
                case 1 -> isEligible() ? "Valedictorian" : "";
                case 2 -> isEligible() ? "Salutatorian" : "";
                case 3 -> hasGraduatingWithHonorsResidencyProxy() ? "3rd Graduating with Honors" : "";
                case 4 -> hasGraduatingWithHonorsResidencyProxy() ? "4th Graduating with Honors" : "";
                case 5 -> hasGraduatingWithHonorsResidencyProxy() ? "5th Graduating with Honors" : "";
                default -> "";
            };
        }

        private String topThreeNumericAverageAwardLabel() {
            if (!isEligible()) {
                return "";
            }
            return switch (rank) {
                case 1 -> "1st Highest Numerical Average";
                case 2 -> "2nd Highest Numerical Average";
                case 3 -> "3rd Highest Numerical Average";
                default -> "";
            };
        }

        private boolean isConsistentHighGpaAwardEligible() {
            BigDecimal cumulativeGpa = cumulativeGpa();
            return cumulativeGpa != null
                && cumulativeGpa.compareTo(CONSISTENT_HIGH_GPA_MINIMUM) >= 0
                && hasRequiredChgpaResidencyProxy();
        }

        private boolean isConsistentPrincipalsListEligible() {
            return currentYearMinimumNumericGrade != null
                && currentYearMinimumNumericGrade.compareTo(BigDecimal.valueOf(CONSISTENT_PRINCIPALS_LIST_MINIMUM)) >= 0
                && currentYearQuartersCovered.containsAll(allowedCurrentYearTerms);
        }

        private boolean isConsistentHonorRollEligible() {
            return currentYearMinimumNumericGrade != null
                && currentYearMinimumNumericGrade.compareTo(BigDecimal.valueOf(CONSISTENT_HONOR_ROLL_MINIMUM)) >= 0
                && currentYearQuartersCovered.containsAll(allowedCurrentYearTerms);
        }

        private boolean hasRequiredChgpaResidencyProxy() {
            return switch (currentGradeLevel) {
                case 12 -> coversGradeRange(11, 12);
                case 5 -> coversGradeRange(4, 5);
                default -> distinctGradesCovered() >= 2;
            };
        }

        private boolean hasGraduatingWithHonorsResidencyProxy() {
            return switch (currentGradeLevel) {
                case 12 -> coversGradeRange(10, 12);
                case 5 -> coversGradeRange(3, 5);
                default -> distinctGradesCovered() >= 3;
            };
        }

        private boolean coversGradeRange(int requiredStartGrade, int requiredEndGrade) {
            for (int grade = requiredStartGrade; grade <= requiredEndGrade; grade++) {
                if (!coveredGrades.contains(grade)) {
                    return false;
                }
            }
            return true;
        }

        private String ineligibilityReason() {
            if (isEligible()) {
                return "";
            }
            if (distinctGradesCovered() != requiredGradeCount()) {
                return "Missing grade history across the full GCA residency window.";
            }
            if (minimumNumericGrade != null && minimumNumericGrade.compareTo(BigDecimal.valueOf(MINIMUM_NUMERIC_GRADE)) < 0) {
                return "Has a quarterly numeric grade below " + MINIMUM_NUMERIC_GRADE + ".";
            }
            return "No qualifying quarterly numeric grades found.";
        }

        private GraduationRankingCandidate toView() {
            return new GraduationRankingCandidate(
                studentId,
                displayName,
                currentGradeLevel,
                startGrade,
                endGrade,
                rank,
                cumulativeAverage() == null ? null : cumulativeAverage().setScale(2, RoundingMode.HALF_UP),
                cumulativeGpa() == null ? null : cumulativeGpa().setScale(4, RoundingMode.HALF_UP),
                minimumNumericGrade == null ? null : minimumNumericGrade.setScale(2, RoundingMode.HALF_UP),
                quarterGradeCount(),
                distinctGradesCovered(),
                isEligible(),
                awardLabel(),
                ineligibilityReason()
            );
        }

        private BigDecimal average(Collection<BigDecimal> values) {
            if (values.isEmpty()) {
                return null;
            }
            BigDecimal total = BigDecimal.ZERO;
            for (BigDecimal value : values) {
                total = total.add(value);
            }
            return total.divide(BigDecimal.valueOf(values.size()), 4, RoundingMode.HALF_UP);
        }
    }

    private record GradeQuarterKey(int gradeLevel, String quarter) {
    }

    private static final class QuarterAccumulator {
        private BigDecimal totalPoints = BigDecimal.ZERO;
        private BigDecimal totalPercent = BigDecimal.ZERO;
        private int rowCount;

        private void accept(BigDecimal gpaPoints, BigDecimal percent) {
            totalPoints = totalPoints.add(gpaPoints);
            if (percent != null) {
                totalPercent = totalPercent.add(percent);
            }
            rowCount++;
        }

        private BigDecimal average() {
            if (rowCount == 0) {
                return null;
            }
            return totalPoints.divide(BigDecimal.valueOf(rowCount), 4, RoundingMode.HALF_UP);
        }

        private BigDecimal numericAverage() {
            if (rowCount == 0) {
                return null;
            }
            return totalPercent.divide(BigDecimal.valueOf(rowCount), 4, RoundingMode.HALF_UP);
        }
    }

    private static final class StudentBreakdownAccumulator {
        private final long studentId;
        private final String displayName;
        private final int currentGradeLevel;
        private final int startGrade;
        private final int endGrade;
        private final Map<Integer, Map<String, QuarterBreakdownAccumulator>> byGrade = new LinkedHashMap<>();
        private BigDecimal minimumNumericGrade;

        private StudentBreakdownAccumulator(long studentId, String displayName, int currentGradeLevel, int startGrade, int endGrade) {
            this.studentId = studentId;
            this.displayName = displayName;
            this.currentGradeLevel = currentGradeLevel;
            this.startGrade = startGrade;
            this.endGrade = endGrade;
        }

        private void accept(int gradeLevel, String quarter, String courseName, BigDecimal percent, BigDecimal gpa, String gradeScaleName) {
            if (percent != null && (minimumNumericGrade == null || percent.compareTo(minimumNumericGrade) < 0)) {
                minimumNumericGrade = percent;
            }
            byGrade.computeIfAbsent(gradeLevel, ignored -> new LinkedHashMap<>())
                .computeIfAbsent(quarter, ignored -> new QuarterBreakdownAccumulator(quarter))
                .accept(courseName, percent, gradeScaleName, gpa);
        }

        private GraduationRankingStudentBreakdown toView() {
            List<GraduationRankingYearBreakdown> years = new ArrayList<>();
            List<BigDecimal> yearGpas = new ArrayList<>();
            List<BigDecimal> yearNumericAverages = new ArrayList<>();
            for (int grade = startGrade; grade <= endGrade; grade++) {
                Map<String, QuarterBreakdownAccumulator> quarters = byGrade.get(grade);
                if (quarters == null || quarters.isEmpty()) {
                    continue;
                }
                List<GraduationRankingQuarterBreakdown> quarterViews = new ArrayList<>();
                List<BigDecimal> quarterGpas = new ArrayList<>();
                List<BigDecimal> quarterNumericAverages = new ArrayList<>();
                for (String quarter : List.of("Q1", "Q2", "Q3", "Q4")) {
                    QuarterBreakdownAccumulator quarterAccumulator = quarters.get(quarter);
                    if (quarterAccumulator == null) {
                        continue;
                    }
                    GraduationRankingQuarterBreakdown quarterView = quarterAccumulator.toView();
                    quarterViews.add(quarterView);
                    if (quarterView.quarterGpa() != null) {
                        quarterGpas.add(quarterView.quarterGpa());
                    }
                    if (quarterView.quarterNumericAverage() != null) {
                        quarterNumericAverages.add(quarterView.quarterNumericAverage());
                    }
                }
                BigDecimal yearGpa = average(quarterGpas);
                BigDecimal yearNumericAverage = average(quarterNumericAverages);
                if (yearGpa != null) {
                    yearGpas.add(yearGpa);
                }
                if (yearNumericAverage != null) {
                    yearNumericAverages.add(yearNumericAverage);
                }
                years.add(new GraduationRankingYearBreakdown(grade, yearGpa, yearNumericAverage, quarterViews));
            }
            return new GraduationRankingStudentBreakdown(
                studentId,
                displayName,
                currentGradeLevel,
                startGrade,
                endGrade,
                average(yearGpas),
                average(yearNumericAverages),
                minimumNumericGrade,
                years
            );
        }

        private BigDecimal average(List<BigDecimal> values) {
            if (values.isEmpty()) {
                return null;
            }
            BigDecimal total = BigDecimal.ZERO;
            for (BigDecimal value : values) {
                total = total.add(value);
            }
            return total.divide(BigDecimal.valueOf(values.size()), 6, RoundingMode.HALF_UP);
        }
    }

    private static final class QuarterBreakdownAccumulator {
        private final String quarter;
        private final List<GraduationRankingCourseBreakdown> courses = new ArrayList<>();
        private BigDecimal totalGpa = BigDecimal.ZERO;
        private BigDecimal totalPercent = BigDecimal.ZERO;
        private int courseCount;

        private QuarterBreakdownAccumulator(String quarter) {
            this.quarter = quarter;
        }

        private void accept(String courseName, BigDecimal percent, String gradeScaleName, BigDecimal gpa) {
            courses.add(new GraduationRankingCourseBreakdown(courseName, percent, gradeScaleName, gpa));
            if (percent != null) {
                totalPercent = totalPercent.add(percent);
            }
            if (gpa != null) {
                totalGpa = totalGpa.add(gpa);
            }
            courseCount++;
        }

        private GraduationRankingQuarterBreakdown toView() {
            BigDecimal quarterGpa = courseCount == 0 ? null : totalGpa.divide(BigDecimal.valueOf(courseCount), 6, RoundingMode.HALF_UP);
            BigDecimal quarterNumericAverage = courseCount == 0 ? null : totalPercent.divide(BigDecimal.valueOf(courseCount), 6, RoundingMode.HALF_UP);
            return new GraduationRankingQuarterBreakdown(
                quarter,
                quarterGpa,
                quarterNumericAverage,
                courseCount,
                courses.stream()
                    .sorted(Comparator.comparing(GraduationRankingCourseBreakdown::courseName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                    .toList()
            );
        }
    }
}
