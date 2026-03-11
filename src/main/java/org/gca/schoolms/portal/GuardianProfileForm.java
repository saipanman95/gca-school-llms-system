package org.gca.schoolms.portal;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import org.gca.schoolms.finance.MaritalStatus;

public class GuardianProfileForm {

    @NotBlank
    private String guardianName;

    @NotBlank
    private String guardianEmail;

    @NotBlank
    private String guardianPhone;

    @NotBlank
    private String guardianMailingAddressLine1;

    private String guardianMailingAddressLine2;

    @NotBlank
    private String guardianMailingCity;

    @NotBlank
    private String guardianMailingState;

    @NotBlank
    private String guardianMailingPostalCode;

    private String guardianEmployerName;
    private String guardianWorkPhone;
    private String guardianWorkEmail;
    private String guardianWorkAddressLine1;
    private String guardianWorkAddressLine2;
    private String guardianWorkCity;
    private String guardianWorkState;
    private String guardianWorkPostalCode;
    private String guardianGender;
    private String guardianEthnicity;
    private String guardianCitizenshipStatus;
    private String guardianCountryOfCitizenship;
    private boolean guardianVisaRequired;
    private String guardianVisaType;
    private String guardianVisaNumber;
    private LocalDate guardianVisaIssueDate;
    private LocalDate guardianVisaExpirationDate;
    private MaritalStatus maritalStatus = MaritalStatus.MARRIED;
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
    private boolean secondaryVisaRequired;
    private String secondaryVisaType;
    private String secondaryVisaNumber;
    private LocalDate secondaryVisaIssueDate;
    private LocalDate secondaryVisaExpirationDate;
    private boolean secondaryGuardianPortalAccess = true;
    private boolean primaryGuardianBillingRecipient = true;

    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }
    public String getGuardianEmail() { return guardianEmail; }
    public void setGuardianEmail(String guardianEmail) { this.guardianEmail = guardianEmail; }
    public String getGuardianPhone() { return guardianPhone; }
    public void setGuardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; }
    public String getGuardianMailingAddressLine1() { return guardianMailingAddressLine1; }
    public void setGuardianMailingAddressLine1(String guardianMailingAddressLine1) { this.guardianMailingAddressLine1 = guardianMailingAddressLine1; }
    public String getGuardianMailingAddressLine2() { return guardianMailingAddressLine2; }
    public void setGuardianMailingAddressLine2(String guardianMailingAddressLine2) { this.guardianMailingAddressLine2 = guardianMailingAddressLine2; }
    public String getGuardianMailingCity() { return guardianMailingCity; }
    public void setGuardianMailingCity(String guardianMailingCity) { this.guardianMailingCity = guardianMailingCity; }
    public String getGuardianMailingState() { return guardianMailingState; }
    public void setGuardianMailingState(String guardianMailingState) { this.guardianMailingState = guardianMailingState; }
    public String getGuardianMailingPostalCode() { return guardianMailingPostalCode; }
    public void setGuardianMailingPostalCode(String guardianMailingPostalCode) { this.guardianMailingPostalCode = guardianMailingPostalCode; }
    public String getGuardianEmployerName() { return guardianEmployerName; }
    public void setGuardianEmployerName(String guardianEmployerName) { this.guardianEmployerName = guardianEmployerName; }
    public String getGuardianWorkPhone() { return guardianWorkPhone; }
    public void setGuardianWorkPhone(String guardianWorkPhone) { this.guardianWorkPhone = guardianWorkPhone; }
    public String getGuardianWorkEmail() { return guardianWorkEmail; }
    public void setGuardianWorkEmail(String guardianWorkEmail) { this.guardianWorkEmail = guardianWorkEmail; }
    public String getGuardianWorkAddressLine1() { return guardianWorkAddressLine1; }
    public void setGuardianWorkAddressLine1(String guardianWorkAddressLine1) { this.guardianWorkAddressLine1 = guardianWorkAddressLine1; }
    public String getGuardianWorkAddressLine2() { return guardianWorkAddressLine2; }
    public void setGuardianWorkAddressLine2(String guardianWorkAddressLine2) { this.guardianWorkAddressLine2 = guardianWorkAddressLine2; }
    public String getGuardianWorkCity() { return guardianWorkCity; }
    public void setGuardianWorkCity(String guardianWorkCity) { this.guardianWorkCity = guardianWorkCity; }
    public String getGuardianWorkState() { return guardianWorkState; }
    public void setGuardianWorkState(String guardianWorkState) { this.guardianWorkState = guardianWorkState; }
    public String getGuardianWorkPostalCode() { return guardianWorkPostalCode; }
    public void setGuardianWorkPostalCode(String guardianWorkPostalCode) { this.guardianWorkPostalCode = guardianWorkPostalCode; }
    public String getGuardianGender() { return guardianGender; }
    public void setGuardianGender(String guardianGender) { this.guardianGender = guardianGender; }
    public String getGuardianEthnicity() { return guardianEthnicity; }
    public void setGuardianEthnicity(String guardianEthnicity) { this.guardianEthnicity = guardianEthnicity; }
    public String getGuardianCitizenshipStatus() { return guardianCitizenshipStatus; }
    public void setGuardianCitizenshipStatus(String guardianCitizenshipStatus) { this.guardianCitizenshipStatus = guardianCitizenshipStatus; }
    public String getGuardianCountryOfCitizenship() { return guardianCountryOfCitizenship; }
    public void setGuardianCountryOfCitizenship(String guardianCountryOfCitizenship) { this.guardianCountryOfCitizenship = guardianCountryOfCitizenship; }
    public boolean isGuardianVisaRequired() { return guardianVisaRequired; }
    public void setGuardianVisaRequired(boolean guardianVisaRequired) { this.guardianVisaRequired = guardianVisaRequired; }
    public String getGuardianVisaType() { return guardianVisaType; }
    public void setGuardianVisaType(String guardianVisaType) { this.guardianVisaType = guardianVisaType; }
    public String getGuardianVisaNumber() { return guardianVisaNumber; }
    public void setGuardianVisaNumber(String guardianVisaNumber) { this.guardianVisaNumber = guardianVisaNumber; }
    public LocalDate getGuardianVisaIssueDate() { return guardianVisaIssueDate; }
    public void setGuardianVisaIssueDate(LocalDate guardianVisaIssueDate) { this.guardianVisaIssueDate = guardianVisaIssueDate; }
    public LocalDate getGuardianVisaExpirationDate() { return guardianVisaExpirationDate; }
    public void setGuardianVisaExpirationDate(LocalDate guardianVisaExpirationDate) { this.guardianVisaExpirationDate = guardianVisaExpirationDate; }
    public MaritalStatus getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(MaritalStatus maritalStatus) { this.maritalStatus = maritalStatus; }
    public String getSecondaryGuardianName() { return secondaryGuardianName; }
    public void setSecondaryGuardianName(String secondaryGuardianName) { this.secondaryGuardianName = secondaryGuardianName; }
    public String getSecondaryGuardianEmail() { return secondaryGuardianEmail; }
    public void setSecondaryGuardianEmail(String secondaryGuardianEmail) { this.secondaryGuardianEmail = secondaryGuardianEmail; }
    public String getSecondaryGuardianPhone() { return secondaryGuardianPhone; }
    public void setSecondaryGuardianPhone(String secondaryGuardianPhone) { this.secondaryGuardianPhone = secondaryGuardianPhone; }
    public String getSecondaryMailingAddressLine1() { return secondaryMailingAddressLine1; }
    public void setSecondaryMailingAddressLine1(String secondaryMailingAddressLine1) { this.secondaryMailingAddressLine1 = secondaryMailingAddressLine1; }
    public String getSecondaryMailingAddressLine2() { return secondaryMailingAddressLine2; }
    public void setSecondaryMailingAddressLine2(String secondaryMailingAddressLine2) { this.secondaryMailingAddressLine2 = secondaryMailingAddressLine2; }
    public String getSecondaryMailingCity() { return secondaryMailingCity; }
    public void setSecondaryMailingCity(String secondaryMailingCity) { this.secondaryMailingCity = secondaryMailingCity; }
    public String getSecondaryMailingState() { return secondaryMailingState; }
    public void setSecondaryMailingState(String secondaryMailingState) { this.secondaryMailingState = secondaryMailingState; }
    public String getSecondaryMailingPostalCode() { return secondaryMailingPostalCode; }
    public void setSecondaryMailingPostalCode(String secondaryMailingPostalCode) { this.secondaryMailingPostalCode = secondaryMailingPostalCode; }
    public String getSecondaryEmployerName() { return secondaryEmployerName; }
    public void setSecondaryEmployerName(String secondaryEmployerName) { this.secondaryEmployerName = secondaryEmployerName; }
    public String getSecondaryWorkPhone() { return secondaryWorkPhone; }
    public void setSecondaryWorkPhone(String secondaryWorkPhone) { this.secondaryWorkPhone = secondaryWorkPhone; }
    public String getSecondaryWorkEmail() { return secondaryWorkEmail; }
    public void setSecondaryWorkEmail(String secondaryWorkEmail) { this.secondaryWorkEmail = secondaryWorkEmail; }
    public String getSecondaryWorkAddressLine1() { return secondaryWorkAddressLine1; }
    public void setSecondaryWorkAddressLine1(String secondaryWorkAddressLine1) { this.secondaryWorkAddressLine1 = secondaryWorkAddressLine1; }
    public String getSecondaryWorkAddressLine2() { return secondaryWorkAddressLine2; }
    public void setSecondaryWorkAddressLine2(String secondaryWorkAddressLine2) { this.secondaryWorkAddressLine2 = secondaryWorkAddressLine2; }
    public String getSecondaryWorkCity() { return secondaryWorkCity; }
    public void setSecondaryWorkCity(String secondaryWorkCity) { this.secondaryWorkCity = secondaryWorkCity; }
    public String getSecondaryWorkState() { return secondaryWorkState; }
    public void setSecondaryWorkState(String secondaryWorkState) { this.secondaryWorkState = secondaryWorkState; }
    public String getSecondaryWorkPostalCode() { return secondaryWorkPostalCode; }
    public void setSecondaryWorkPostalCode(String secondaryWorkPostalCode) { this.secondaryWorkPostalCode = secondaryWorkPostalCode; }
    public String getSecondaryGender() { return secondaryGender; }
    public void setSecondaryGender(String secondaryGender) { this.secondaryGender = secondaryGender; }
    public String getSecondaryEthnicity() { return secondaryEthnicity; }
    public void setSecondaryEthnicity(String secondaryEthnicity) { this.secondaryEthnicity = secondaryEthnicity; }
    public String getSecondaryCitizenshipStatus() { return secondaryCitizenshipStatus; }
    public void setSecondaryCitizenshipStatus(String secondaryCitizenshipStatus) { this.secondaryCitizenshipStatus = secondaryCitizenshipStatus; }
    public String getSecondaryCountryOfCitizenship() { return secondaryCountryOfCitizenship; }
    public void setSecondaryCountryOfCitizenship(String secondaryCountryOfCitizenship) { this.secondaryCountryOfCitizenship = secondaryCountryOfCitizenship; }
    public boolean isSecondaryVisaRequired() { return secondaryVisaRequired; }
    public void setSecondaryVisaRequired(boolean secondaryVisaRequired) { this.secondaryVisaRequired = secondaryVisaRequired; }
    public String getSecondaryVisaType() { return secondaryVisaType; }
    public void setSecondaryVisaType(String secondaryVisaType) { this.secondaryVisaType = secondaryVisaType; }
    public String getSecondaryVisaNumber() { return secondaryVisaNumber; }
    public void setSecondaryVisaNumber(String secondaryVisaNumber) { this.secondaryVisaNumber = secondaryVisaNumber; }
    public LocalDate getSecondaryVisaIssueDate() { return secondaryVisaIssueDate; }
    public void setSecondaryVisaIssueDate(LocalDate secondaryVisaIssueDate) { this.secondaryVisaIssueDate = secondaryVisaIssueDate; }
    public LocalDate getSecondaryVisaExpirationDate() { return secondaryVisaExpirationDate; }
    public void setSecondaryVisaExpirationDate(LocalDate secondaryVisaExpirationDate) { this.secondaryVisaExpirationDate = secondaryVisaExpirationDate; }
    public boolean isSecondaryGuardianPortalAccess() { return secondaryGuardianPortalAccess; }
    public void setSecondaryGuardianPortalAccess(boolean secondaryGuardianPortalAccess) { this.secondaryGuardianPortalAccess = secondaryGuardianPortalAccess; }
    public boolean isPrimaryGuardianBillingRecipient() { return primaryGuardianBillingRecipient; }
    public void setPrimaryGuardianBillingRecipient(boolean primaryGuardianBillingRecipient) { this.primaryGuardianBillingRecipient = primaryGuardianBillingRecipient; }
}
