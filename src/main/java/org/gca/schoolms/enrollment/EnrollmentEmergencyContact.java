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
public class EnrollmentEmergencyContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_request_id", nullable = false)
    private EnrollmentRequest enrollmentRequest;

    @Column(nullable = false)
    private String contactName;

    private String relationshipToStudent;

    @Column(nullable = false)
    private String primaryPhone;

    private String secondaryPhone;

    private String email;

    @Column(nullable = false)
    private boolean pickupAuthorized;

    protected EnrollmentEmergencyContact() {
    }

    public EnrollmentEmergencyContact(EnrollmentRequest enrollmentRequest, String contactName,
                                      String relationshipToStudent, String primaryPhone,
                                      String secondaryPhone, String email, boolean pickupAuthorized) {
        this.enrollmentRequest = enrollmentRequest;
        this.contactName = contactName;
        this.relationshipToStudent = relationshipToStudent;
        this.primaryPhone = primaryPhone;
        this.secondaryPhone = secondaryPhone;
        this.email = email;
        this.pickupAuthorized = pickupAuthorized;
    }

    public Long getId() {
        return id;
    }

    public String getContactName() {
        return contactName;
    }

    public String getRelationshipToStudent() {
        return relationshipToStudent;
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public String getEmail() {
        return email;
    }

    public boolean isPickupAuthorized() {
        return pickupAuthorized;
    }
}
