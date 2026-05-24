package org.gca.schoolms.policy;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class GradingScaleSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate effectiveStartDate;

    private LocalDate effectiveEndDate;

    @Column(nullable = false)
    private boolean active;

    protected GradingScaleSet() {
    }

    public GradingScaleSet(String code, String name, LocalDate effectiveStartDate, LocalDate effectiveEndDate, boolean active) {
        this.code = code;
        this.name = name;
        this.effectiveStartDate = effectiveStartDate;
        this.effectiveEndDate = effectiveEndDate;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public LocalDate getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public LocalDate getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public boolean isActive() {
        return active;
    }
}
