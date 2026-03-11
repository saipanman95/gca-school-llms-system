package org.gca.schoolms.enrollment;

public enum EnrollmentDocumentType {
    MEDICAL_RECORD("medical_record"),
    HEALTH_CERTIFICATE("health_certificate"),
    VACCINATION_CARD("vaccination_card"),
    PREVIOUS_SCHOOL_TRANSCRIPT("previous_school_transcript"),
    GCA_TRANSCRIPT("gca_transcript"),
    STUDENT_VISA("student_visa"),
    STUDENT_PASSPORT("student_passport");

    private final String storageCode;

    EnrollmentDocumentType(String storageCode) {
        this.storageCode = storageCode;
    }

    public String getStorageCode() {
        return storageCode;
    }
}
