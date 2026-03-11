package org.gca.schoolms.enrollment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class EnrollmentAttestation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_request_id", nullable = false, unique = true)
    private EnrollmentRequest enrollmentRequest;

    @Column(nullable = false)
    private boolean confirmedTrueAndCorrect;

    @Column(nullable = false)
    private String parentInitials;

    @Column(nullable = false)
    private LocalDateTime attestedOn;

    protected EnrollmentAttestation() {
    }

    public EnrollmentAttestation(EnrollmentRequest enrollmentRequest, boolean confirmedTrueAndCorrect,
                                 String parentInitials, LocalDateTime attestedOn) {
        this.enrollmentRequest = enrollmentRequest;
        this.confirmedTrueAndCorrect = confirmedTrueAndCorrect;
        this.parentInitials = parentInitials;
        this.attestedOn = attestedOn;
    }

    public String getParentInitials() {
        return parentInitials;
    }

    public boolean isConfirmedTrueAndCorrect() {
        return confirmedTrueAndCorrect;
    }

    public LocalDateTime getAttestedOn() {
        return attestedOn;
    }
}
