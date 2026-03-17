package org.gca.schoolms.settings;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SchoolYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String label;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDate firstDayOfClasses;

    @Column(nullable = false)
    private LocalDate lastDayOfClasses;

    protected SchoolYear() {
    }

    public SchoolYear(String label, LocalDate startDate, LocalDate endDate,
                      LocalDate firstDayOfClasses, LocalDate lastDayOfClasses) {
        this.label = label;
        this.startDate = startDate;
        this.endDate = endDate;
        this.firstDayOfClasses = firstDayOfClasses;
        this.lastDayOfClasses = lastDayOfClasses;
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getFirstDayOfClasses() {
        return firstDayOfClasses;
    }

    public LocalDate getLastDayOfClasses() {
        return lastDayOfClasses;
    }
}
