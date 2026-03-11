package org.gca.schoolms.finance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.gca.schoolms.organization.Campus;

@Entity
public class FamilyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String primaryGuardianName;

    @Column(nullable = false)
    private String primaryGuardianEmail;

    @Column(nullable = false)
    private String primaryGuardianPhone;

    @Column(nullable = false)
    private String mailingAddressLine1;

    private String mailingAddressLine2;

    @Column(nullable = false)
    private String mailingCity;

    @Column(nullable = false)
    private String mailingState;

    @Column(nullable = false)
    private String mailingPostalCode;

    private String employerName;

    private String workPhone;

    private String workEmail;

    private String workAddressLine1;

    private String workAddressLine2;

    private String workCity;

    private String workState;

    private String workPostalCode;

    private String gender;

    private String ethnicity;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;

    protected FamilyAccount() {
    }

    public FamilyAccount(String accountNumber, String accountName, String primaryGuardianName,
                         String primaryGuardianEmail, String primaryGuardianPhone,
                         String mailingAddressLine1, String mailingAddressLine2, String mailingCity,
                         String mailingState, String mailingPostalCode, String employerName,
                         String workPhone, String workEmail, String workAddressLine1, String workAddressLine2,
                         String workCity, String workState, String workPostalCode, String gender,
                         String ethnicity, Campus campus) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.primaryGuardianName = primaryGuardianName;
        this.primaryGuardianEmail = primaryGuardianEmail;
        this.primaryGuardianPhone = primaryGuardianPhone;
        this.mailingAddressLine1 = mailingAddressLine1;
        this.mailingAddressLine2 = mailingAddressLine2;
        this.mailingCity = mailingCity;
        this.mailingState = mailingState;
        this.mailingPostalCode = mailingPostalCode;
        this.employerName = employerName;
        this.workPhone = workPhone;
        this.workEmail = workEmail;
        this.workAddressLine1 = workAddressLine1;
        this.workAddressLine2 = workAddressLine2;
        this.workCity = workCity;
        this.workState = workState;
        this.workPostalCode = workPostalCode;
        this.gender = gender;
        this.ethnicity = ethnicity;
        this.campus = campus;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getPrimaryGuardianName() {
        return primaryGuardianName;
    }

    public String getPrimaryGuardianEmail() {
        return primaryGuardianEmail;
    }

    public String getPrimaryGuardianPhone() {
        return primaryGuardianPhone;
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

    public String getEmployerName() {
        return employerName;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public String getWorkAddressLine1() {
        return workAddressLine1;
    }

    public String getWorkAddressLine2() {
        return workAddressLine2;
    }

    public String getWorkCity() {
        return workCity;
    }

    public String getWorkState() {
        return workState;
    }

    public String getWorkPostalCode() {
        return workPostalCode;
    }

    public String getGender() {
        return gender;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public Campus getCampus() {
        return campus;
    }

    public void updateGuardianProfile(String primaryGuardianName, String primaryGuardianEmail, String primaryGuardianPhone,
                                      String mailingAddressLine1, String mailingAddressLine2, String mailingCity,
                                      String mailingState, String mailingPostalCode, String employerName,
                                      String workPhone, String workEmail, String workAddressLine1, String workAddressLine2,
                                      String workCity, String workState, String workPostalCode, String gender,
                                      String ethnicity) {
        this.primaryGuardianName = primaryGuardianName;
        this.primaryGuardianEmail = primaryGuardianEmail;
        this.primaryGuardianPhone = primaryGuardianPhone;
        this.mailingAddressLine1 = mailingAddressLine1;
        this.mailingAddressLine2 = mailingAddressLine2;
        this.mailingCity = mailingCity;
        this.mailingState = mailingState;
        this.mailingPostalCode = mailingPostalCode;
        this.employerName = employerName;
        this.workPhone = workPhone;
        this.workEmail = workEmail;
        this.workAddressLine1 = workAddressLine1;
        this.workAddressLine2 = workAddressLine2;
        this.workCity = workCity;
        this.workState = workState;
        this.workPostalCode = workPostalCode;
        this.gender = gender;
        this.ethnicity = ethnicity;
    }
}
