package org.gca.schoolms.portal;

public class EmergencyContactFormRow {

    private String contactName;

    private String relationshipToStudent;

    private String primaryPhone;

    private String secondaryPhone;

    private String email;

    private boolean pickupAuthorized;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getRelationshipToStudent() {
        return relationshipToStudent;
    }

    public void setRelationshipToStudent(String relationshipToStudent) {
        this.relationshipToStudent = relationshipToStudent;
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPickupAuthorized() {
        return pickupAuthorized;
    }

    public void setPickupAuthorized(boolean pickupAuthorized) {
        this.pickupAuthorized = pickupAuthorized;
    }
}
