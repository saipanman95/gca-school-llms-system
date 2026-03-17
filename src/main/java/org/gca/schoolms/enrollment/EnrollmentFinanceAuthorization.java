package org.gca.schoolms.enrollment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class EnrollmentFinanceAuthorization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_request_id", nullable = false)
    private EnrollmentRequest enrollmentRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentFinanceAuthorizationType authorizationType;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private LocalDate approvedOn;

    protected EnrollmentFinanceAuthorization() {
    }

    public EnrollmentFinanceAuthorization(EnrollmentRequest enrollmentRequest,
                                          EnrollmentFinanceAuthorizationType authorizationType,
                                          String notes,
                                          LocalDate approvedOn) {
        this.enrollmentRequest = enrollmentRequest;
        this.authorizationType = authorizationType;
        this.notes = notes == null || notes.isBlank() ? null : notes.trim();
        this.approvedOn = approvedOn;
    }

    public EnrollmentFinanceAuthorizationType getAuthorizationType() {
        return authorizationType;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDate getApprovedOn() {
        return approvedOn;
    }
}
