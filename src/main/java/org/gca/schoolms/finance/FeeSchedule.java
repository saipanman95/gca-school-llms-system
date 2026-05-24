package org.gca.schoolms.finance;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.gca.schoolms.organization.Campus;

@Entity
public class FeeSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String schoolYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeScheduleGradeGroup gradeGroup;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "feeSchedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("sortOrder ASC, id ASC")
    private List<FeeScheduleItem> items = new ArrayList<>();

    protected FeeSchedule() {
    }

    public FeeSchedule(String name, String schoolYear, FeeScheduleGradeGroup gradeGroup, Campus campus, boolean active) {
        this.name = name;
        this.schoolYear = schoolYear;
        this.gradeGroup = gradeGroup;
        this.campus = campus;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public FeeScheduleGradeGroup getGradeGroup() {
        return gradeGroup;
    }

    public Campus getCampus() {
        return campus;
    }

    public boolean isActive() {
        return active;
    }

    public List<FeeScheduleItem> getItems() {
        return items;
    }

    public void update(String name, String schoolYear, FeeScheduleGradeGroup gradeGroup, Campus campus, boolean active) {
        this.name = name;
        this.schoolYear = schoolYear;
        this.gradeGroup = gradeGroup;
        this.campus = campus;
        this.active = active;
    }
}
