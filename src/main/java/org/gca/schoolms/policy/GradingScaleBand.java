package org.gca.schoolms.policy;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class GradingScaleBand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scale_set_id", nullable = false)
    private GradingScaleSet scaleSet;

    @Column(nullable = false)
    private String trackCode;

    @Column(nullable = false)
    private String letterGrade;

    @Column(nullable = false)
    private BigDecimal minScore;

    @Column(nullable = false)
    private BigDecimal maxScore;

    @Column(nullable = false)
    private BigDecimal gpaPoints;

    @Column(nullable = false)
    private int sortOrder;

    protected GradingScaleBand() {
    }

    public GradingScaleBand(GradingScaleSet scaleSet, String trackCode, String letterGrade,
                            BigDecimal minScore, BigDecimal maxScore, BigDecimal gpaPoints, int sortOrder) {
        this.scaleSet = scaleSet;
        this.trackCode = trackCode;
        this.letterGrade = letterGrade;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.gpaPoints = gpaPoints;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public GradingScaleSet getScaleSet() {
        return scaleSet;
    }

    public String getTrackCode() {
        return trackCode;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public BigDecimal getMinScore() {
        return minScore;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public BigDecimal getGpaPoints() {
        return gpaPoints;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
