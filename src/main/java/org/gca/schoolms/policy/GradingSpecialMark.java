package org.gca.schoolms.policy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class GradingSpecialMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scale_set_id", nullable = false)
    private GradingScaleSet scaleSet;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private boolean countsForGpa;

    @Column(nullable = false)
    private boolean countsForCredit;

    @Column(nullable = false)
    private int sortOrder;

    protected GradingSpecialMark() {
    }

    public GradingSpecialMark(GradingScaleSet scaleSet, String code, String label,
                              boolean countsForGpa, boolean countsForCredit, int sortOrder) {
        this.scaleSet = scaleSet;
        this.code = code;
        this.label = label;
        this.countsForGpa = countsForGpa;
        this.countsForCredit = countsForCredit;
        this.sortOrder = sortOrder;
    }
}
