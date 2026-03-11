package org.gca.schoolms.enrollment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class EnrollmentRequestLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_request_id", nullable = false)
    private EnrollmentRequest enrollmentRequest;

    @Column(nullable = false)
    private String languageName;

    private String proficiencyLevel;

    private Integer preferenceRank;

    protected EnrollmentRequestLanguage() {
    }

    public EnrollmentRequestLanguage(EnrollmentRequest enrollmentRequest, String languageName,
                                     String proficiencyLevel, Integer preferenceRank) {
        this.enrollmentRequest = enrollmentRequest;
        this.languageName = languageName;
        this.proficiencyLevel = proficiencyLevel;
        this.preferenceRank = preferenceRank;
    }

    public Long getId() {
        return id;
    }

    public EnrollmentRequest getEnrollmentRequest() {
        return enrollmentRequest;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public Integer getPreferenceRank() {
        return preferenceRank;
    }
}
