package org.gca.schoolms.finance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import org.gca.schoolms.organization.Campus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

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

    private String citizenshipStatus;

    private String countryOfCitizenship;

    @Column(nullable = false)
    private boolean visaRequired;

    private String visaType;

    private String visaNumber;

    private LocalDate visaIssueDate;

    private LocalDate visaExpirationDate;

    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    private String secondaryGuardianName;

    private String secondaryGuardianEmail;

    private String secondaryGuardianPhone;

    private String secondaryMailingAddressLine1;

    private String secondaryMailingAddressLine2;

    private String secondaryMailingCity;

    private String secondaryMailingState;

    private String secondaryMailingPostalCode;

    private String secondaryEmployerName;

    private String secondaryWorkPhone;

    private String secondaryWorkEmail;

    private String secondaryWorkAddressLine1;

    private String secondaryWorkAddressLine2;

    private String secondaryWorkCity;

    private String secondaryWorkState;

    private String secondaryWorkPostalCode;

    private String secondaryGender;

    private String secondaryEthnicity;

    private String secondaryCitizenshipStatus;

    private String secondaryCountryOfCitizenship;

    @Column(nullable = false)
    private boolean secondaryVisaRequired;

    private String secondaryVisaType;

    private String secondaryVisaNumber;

    private LocalDate secondaryVisaIssueDate;

    private LocalDate secondaryVisaExpirationDate;

    @Column(nullable = false)
    private boolean secondaryGuardianPortalAccess;

    @Column(nullable = false)
    private boolean primaryGuardianBillingRecipient = true;

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
                         String ethnicity, String citizenshipStatus, String countryOfCitizenship,
                         boolean visaRequired, String visaType, String visaNumber, LocalDate visaIssueDate,
                         LocalDate visaExpirationDate, MaritalStatus maritalStatus, String secondaryGuardianName,
                         String secondaryGuardianEmail, String secondaryGuardianPhone, String secondaryMailingAddressLine1,
                         String secondaryMailingAddressLine2, String secondaryMailingCity, String secondaryMailingState,
                         String secondaryMailingPostalCode, String secondaryEmployerName, String secondaryWorkPhone,
                         String secondaryWorkEmail, String secondaryWorkAddressLine1, String secondaryWorkAddressLine2,
                         String secondaryWorkCity, String secondaryWorkState, String secondaryWorkPostalCode,
                         String secondaryGender, String secondaryEthnicity, String secondaryCitizenshipStatus,
                         String secondaryCountryOfCitizenship, boolean secondaryVisaRequired, String secondaryVisaType,
                         String secondaryVisaNumber, LocalDate secondaryVisaIssueDate,
                         LocalDate secondaryVisaExpirationDate,
                         boolean secondaryGuardianPortalAccess, boolean primaryGuardianBillingRecipient,
                         Campus campus) {
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
        this.citizenshipStatus = citizenshipStatus;
        this.countryOfCitizenship = countryOfCitizenship;
        this.visaRequired = visaRequired;
        this.visaType = visaType;
        this.visaNumber = visaNumber;
        this.visaIssueDate = visaIssueDate;
        this.visaExpirationDate = visaExpirationDate;
        this.maritalStatus = maritalStatus;
        this.secondaryGuardianName = secondaryGuardianName;
        this.secondaryGuardianEmail = secondaryGuardianEmail;
        this.secondaryGuardianPhone = secondaryGuardianPhone;
        this.secondaryMailingAddressLine1 = secondaryMailingAddressLine1;
        this.secondaryMailingAddressLine2 = secondaryMailingAddressLine2;
        this.secondaryMailingCity = secondaryMailingCity;
        this.secondaryMailingState = secondaryMailingState;
        this.secondaryMailingPostalCode = secondaryMailingPostalCode;
        this.secondaryEmployerName = secondaryEmployerName;
        this.secondaryWorkPhone = secondaryWorkPhone;
        this.secondaryWorkEmail = secondaryWorkEmail;
        this.secondaryWorkAddressLine1 = secondaryWorkAddressLine1;
        this.secondaryWorkAddressLine2 = secondaryWorkAddressLine2;
        this.secondaryWorkCity = secondaryWorkCity;
        this.secondaryWorkState = secondaryWorkState;
        this.secondaryWorkPostalCode = secondaryWorkPostalCode;
        this.secondaryGender = secondaryGender;
        this.secondaryEthnicity = secondaryEthnicity;
        this.secondaryCitizenshipStatus = secondaryCitizenshipStatus;
        this.secondaryCountryOfCitizenship = secondaryCountryOfCitizenship;
        this.secondaryVisaRequired = secondaryVisaRequired;
        this.secondaryVisaType = secondaryVisaType;
        this.secondaryVisaNumber = secondaryVisaNumber;
        this.secondaryVisaIssueDate = secondaryVisaIssueDate;
        this.secondaryVisaExpirationDate = secondaryVisaExpirationDate;
        this.secondaryGuardianPortalAccess = secondaryGuardianPortalAccess;
        this.primaryGuardianBillingRecipient = primaryGuardianBillingRecipient;
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

    public String getCitizenshipStatus() {
        return citizenshipStatus;
    }

    public String getCountryOfCitizenship() {
        return countryOfCitizenship;
    }

    public boolean isVisaRequired() {
        return visaRequired;
    }

    public String getVisaType() {
        return visaType;
    }

    public String getVisaNumber() {
        return visaNumber;
    }

    public LocalDate getVisaIssueDate() {
        return visaIssueDate;
    }

    public LocalDate getVisaExpirationDate() {
        return visaExpirationDate;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public String getSecondaryGuardianName() {
        return secondaryGuardianName;
    }

    public String getSecondaryGuardianEmail() {
        return secondaryGuardianEmail;
    }

    public String getSecondaryGuardianPhone() {
        return secondaryGuardianPhone;
    }

    public String getSecondaryMailingAddressLine1() {
        return secondaryMailingAddressLine1;
    }

    public String getSecondaryMailingAddressLine2() {
        return secondaryMailingAddressLine2;
    }

    public String getSecondaryMailingCity() {
        return secondaryMailingCity;
    }

    public String getSecondaryMailingState() {
        return secondaryMailingState;
    }

    public String getSecondaryMailingPostalCode() {
        return secondaryMailingPostalCode;
    }

    public String getSecondaryEmployerName() {
        return secondaryEmployerName;
    }

    public String getSecondaryWorkPhone() {
        return secondaryWorkPhone;
    }

    public String getSecondaryWorkEmail() {
        return secondaryWorkEmail;
    }

    public String getSecondaryWorkAddressLine1() {
        return secondaryWorkAddressLine1;
    }

    public String getSecondaryWorkAddressLine2() {
        return secondaryWorkAddressLine2;
    }

    public String getSecondaryWorkCity() {
        return secondaryWorkCity;
    }

    public String getSecondaryWorkState() {
        return secondaryWorkState;
    }

    public String getSecondaryWorkPostalCode() {
        return secondaryWorkPostalCode;
    }

    public String getSecondaryGender() {
        return secondaryGender;
    }

    public String getSecondaryEthnicity() {
        return secondaryEthnicity;
    }

    public String getSecondaryCitizenshipStatus() {
        return secondaryCitizenshipStatus;
    }

    public String getSecondaryCountryOfCitizenship() {
        return secondaryCountryOfCitizenship;
    }

    public boolean isSecondaryVisaRequired() {
        return secondaryVisaRequired;
    }

    public String getSecondaryVisaType() {
        return secondaryVisaType;
    }

    public String getSecondaryVisaNumber() {
        return secondaryVisaNumber;
    }

    public LocalDate getSecondaryVisaIssueDate() {
        return secondaryVisaIssueDate;
    }

    public LocalDate getSecondaryVisaExpirationDate() {
        return secondaryVisaExpirationDate;
    }

    public boolean isSecondaryGuardianPortalAccess() {
        return secondaryGuardianPortalAccess;
    }

    public boolean isPrimaryGuardianBillingRecipient() {
        return primaryGuardianBillingRecipient;
    }

    public Campus getCampus() {
        return campus;
    }

    public void updateGuardianProfile(String primaryGuardianName, String primaryGuardianEmail, String primaryGuardianPhone,
                                      String mailingAddressLine1, String mailingAddressLine2, String mailingCity,
                                      String mailingState, String mailingPostalCode, String employerName,
                                      String workPhone, String workEmail, String workAddressLine1, String workAddressLine2,
                                      String workCity, String workState, String workPostalCode, String gender,
                                      String ethnicity, String citizenshipStatus, String countryOfCitizenship,
                                      boolean visaRequired, String visaType, String visaNumber,
                                      LocalDate visaIssueDate, LocalDate visaExpirationDate,
                                      MaritalStatus maritalStatus, String secondaryGuardianName,
                                      String secondaryGuardianEmail, String secondaryGuardianPhone,
                                      String secondaryMailingAddressLine1, String secondaryMailingAddressLine2,
                                      String secondaryMailingCity, String secondaryMailingState,
                                      String secondaryMailingPostalCode, String secondaryEmployerName,
                                      String secondaryWorkPhone, String secondaryWorkEmail,
                                      String secondaryWorkAddressLine1, String secondaryWorkAddressLine2,
                                      String secondaryWorkCity, String secondaryWorkState,
                                      String secondaryWorkPostalCode, String secondaryGender, String secondaryEthnicity,
                                      String secondaryCitizenshipStatus, String secondaryCountryOfCitizenship,
                                      boolean secondaryVisaRequired, String secondaryVisaType,
                                      String secondaryVisaNumber, LocalDate secondaryVisaIssueDate,
                                      LocalDate secondaryVisaExpirationDate,
                                      boolean secondaryGuardianPortalAccess, boolean primaryGuardianBillingRecipient) {
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
        this.citizenshipStatus = citizenshipStatus;
        this.countryOfCitizenship = countryOfCitizenship;
        this.visaRequired = visaRequired;
        this.visaType = visaType;
        this.visaNumber = visaNumber;
        this.visaIssueDate = visaIssueDate;
        this.visaExpirationDate = visaExpirationDate;
        this.maritalStatus = maritalStatus;
        this.secondaryGuardianName = secondaryGuardianName;
        this.secondaryGuardianEmail = secondaryGuardianEmail;
        this.secondaryGuardianPhone = secondaryGuardianPhone;
        this.secondaryMailingAddressLine1 = secondaryMailingAddressLine1;
        this.secondaryMailingAddressLine2 = secondaryMailingAddressLine2;
        this.secondaryMailingCity = secondaryMailingCity;
        this.secondaryMailingState = secondaryMailingState;
        this.secondaryMailingPostalCode = secondaryMailingPostalCode;
        this.secondaryEmployerName = secondaryEmployerName;
        this.secondaryWorkPhone = secondaryWorkPhone;
        this.secondaryWorkEmail = secondaryWorkEmail;
        this.secondaryWorkAddressLine1 = secondaryWorkAddressLine1;
        this.secondaryWorkAddressLine2 = secondaryWorkAddressLine2;
        this.secondaryWorkCity = secondaryWorkCity;
        this.secondaryWorkState = secondaryWorkState;
        this.secondaryWorkPostalCode = secondaryWorkPostalCode;
        this.secondaryGender = secondaryGender;
        this.secondaryEthnicity = secondaryEthnicity;
        this.secondaryCitizenshipStatus = secondaryCitizenshipStatus;
        this.secondaryCountryOfCitizenship = secondaryCountryOfCitizenship;
        this.secondaryVisaRequired = secondaryVisaRequired;
        this.secondaryVisaType = secondaryVisaType;
        this.secondaryVisaNumber = secondaryVisaNumber;
        this.secondaryVisaIssueDate = secondaryVisaIssueDate;
        this.secondaryVisaExpirationDate = secondaryVisaExpirationDate;
        this.secondaryGuardianPortalAccess = secondaryGuardianPortalAccess;
        this.primaryGuardianBillingRecipient = primaryGuardianBillingRecipient;
    }
}
