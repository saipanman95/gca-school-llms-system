package org.gca.schoolms.finance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PayerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    private String middleName;

    @Column(nullable = false)
    private String lastName;

    private String businessName;

    private String emailAddress;

    private String phoneNumber;

    private String mailingAddressLine1;

    private String mailingAddressLine2;

    private String mailingCity;

    private String mailingState;

    private String mailingPostalCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "family_account_id")
    private FamilyAccount familyAccount;

    protected PayerProfile() {
    }

    public PayerProfile(String firstName, String middleName, String lastName, String businessName, String emailAddress,
                        String phoneNumber, String mailingAddressLine1, String mailingAddressLine2,
                        String mailingCity, String mailingState, String mailingPostalCode,
                        FamilyAccount familyAccount) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.businessName = businessName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.mailingAddressLine1 = mailingAddressLine1;
        this.mailingAddressLine2 = mailingAddressLine2;
        this.mailingCity = mailingCity;
        this.mailingState = mailingState;
        this.mailingPostalCode = mailingPostalCode;
        this.familyAccount = familyAccount;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBusinessName() {
        return businessName;
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

    public FamilyAccount getFamilyAccount() {
        return familyAccount;
    }

    public String getDisplayName() {
        StringBuilder builder = new StringBuilder(firstName);
        if (middleName != null && !middleName.isBlank()) {
            builder.append(' ').append(middleName.trim());
        }
        builder.append(' ').append(lastName);
        return builder.toString().trim();
    }

    public String getLookupLabel() {
        String displayName = getDisplayName();
        if (businessName != null && !businessName.isBlank()) {
            displayName = displayName + " / " + businessName.trim();
        }
        if (familyAccount != null) {
            return displayName + " (" + familyAccount.getAccountName() + ")";
        }
        return displayName;
    }
}
