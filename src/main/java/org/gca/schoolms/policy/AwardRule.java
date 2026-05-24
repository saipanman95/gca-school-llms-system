package org.gca.schoolms.policy;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class AwardRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_set_id", nullable = false)
    private AwardRuleSet ruleSet;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String awardCategory;

    private Integer cumulativeStartGrade;

    private Integer cumulativeEndGrade;

    private Integer residencyStartGrade;

    private Integer residencyEndGrade;

    private Integer minimumNumericGrade;

    @Column(precision = 6, scale = 2)
    private BigDecimal minimumGpa;

    private Integer minimumRank;

    private Integer maximumRank;

    @Column(nullable = false)
    private boolean rankingBased;

    @Column(nullable = false)
    private boolean requiresFullResidencyAcrossWindow;

    @Column(nullable = false)
    private boolean evaluatedPerQuarter;

    @Column(nullable = false)
    private boolean evaluatedPerSchoolYear;

    @Lob
    private String notes;

    protected AwardRule() {
    }

    public AwardRule(AwardRuleSet ruleSet, String code, String name, String awardCategory,
                     Integer cumulativeStartGrade, Integer cumulativeEndGrade,
                     Integer residencyStartGrade, Integer residencyEndGrade,
                     Integer minimumNumericGrade, BigDecimal minimumGpa,
                     Integer minimumRank, Integer maximumRank, boolean rankingBased,
                     boolean requiresFullResidencyAcrossWindow, boolean evaluatedPerQuarter,
                     boolean evaluatedPerSchoolYear, String notes) {
        this.ruleSet = ruleSet;
        this.code = code;
        this.name = name;
        this.awardCategory = awardCategory;
        this.cumulativeStartGrade = cumulativeStartGrade;
        this.cumulativeEndGrade = cumulativeEndGrade;
        this.residencyStartGrade = residencyStartGrade;
        this.residencyEndGrade = residencyEndGrade;
        this.minimumNumericGrade = minimumNumericGrade;
        this.minimumGpa = minimumGpa;
        this.minimumRank = minimumRank;
        this.maximumRank = maximumRank;
        this.rankingBased = rankingBased;
        this.requiresFullResidencyAcrossWindow = requiresFullResidencyAcrossWindow;
        this.evaluatedPerQuarter = evaluatedPerQuarter;
        this.evaluatedPerSchoolYear = evaluatedPerSchoolYear;
        this.notes = notes;
    }
}
