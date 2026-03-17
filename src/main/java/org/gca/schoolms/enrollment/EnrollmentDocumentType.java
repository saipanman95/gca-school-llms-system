package org.gca.schoolms.enrollment;

public enum EnrollmentDocumentType {
    BIRTH_CERTIFICATE("birth_certificate"),
    MEDICAL_RECORD("medical_record"),
    HEALTH_CERTIFICATE("health_certificate"),
    CHC_BLUE_CARE("chc_blue_care"),
    HEALTH_PROFILE("health_profile"),
    VACCINATION_CARD("vaccination_card"),
    RECENT_PHOTOGRAPH("recent_photograph"),
    REPORT_CARD("report_card"),
    OFFICIAL_TRANSCRIPT("official_transcript"),
    PREVIOUS_SCHOOL_TRANSCRIPT("previous_school_transcript"),
    GCA_TRANSCRIPT("gca_transcript"),
    STUDENT_VISA("student_visa"),
    STUDENT_PASSPORT("student_passport"),
    BANK_CERTIFICATE("bank_certificate"),
    GUARDIANSHIP_DOCUMENT("guardianship_document"),
    PREVIOUS_SCHOOL_I20("previous_school_i20");

    private final String storageCode;

    EnrollmentDocumentType(String storageCode) {
        this.storageCode = storageCode;
    }

    public String getStorageCode() {
        return storageCode;
    }
}
