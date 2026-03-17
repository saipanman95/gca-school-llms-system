package org.gca.schoolms.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SchoolProfile {

    @Id
    private Long id;

    @Column(nullable = false)
    private String schoolName;

    private String emailAddress;

    private String phoneNumber;

    @Column(nullable = false)
    private String mailingAddressLine1;

    private String mailingAddressLine2;

    @Column(nullable = false)
    private String mailingCity;

    @Column(nullable = false)
    private String mailingState;

    @Column(nullable = false)
    private String mailingPostalCode;

    protected SchoolProfile() {
    }

    public SchoolProfile(Long id, String schoolName, String emailAddress, String phoneNumber,
                         String mailingAddressLine1, String mailingAddressLine2, String mailingCity,
                         String mailingState, String mailingPostalCode) {
        this.id = id;
        this.schoolName = schoolName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.mailingAddressLine1 = mailingAddressLine1;
        this.mailingAddressLine2 = mailingAddressLine2;
        this.mailingCity = mailingCity;
        this.mailingState = mailingState;
        this.mailingPostalCode = mailingPostalCode;
    }

    public Long getId() {
        return id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMailingAddressLine1() {
        return mailingAddressLine1;
    }

    public String getMailingAddressLine2() {
        return mailingAddressLine2;
    }

    public String getMailingCity() {
        return mailingCity;
    }

    public String getMailingState() {
        return mailingState;
    }

    public String getMailingPostalCode() {
        return mailingPostalCode;
    }

    public void updateFrom(SchoolProfileForm form) {
        this.schoolName = form.getSchoolName();
        this.emailAddress = form.getEmailAddress();
        this.phoneNumber = form.getPhoneNumber();
        this.mailingAddressLine1 = form.getMailingAddressLine1();
        this.mailingAddressLine2 = form.getMailingAddressLine2();
        this.mailingCity = form.getMailingCity();
        this.mailingState = form.getMailingState();
        this.mailingPostalCode = form.getMailingPostalCode();
    }
}
