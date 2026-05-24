package org.gca.schoolms.integration.powerschool;

public enum PowerSchoolImportDatasetType {
    STUDENTS("Students"),
    STORED_GRADES("Stored grades"),
    PERSON("Person"),
    PERSON_ADDRESS("Person address"),
    PERSON_ADDRESS_ASSOC("Person address association"),
    EMAIL_ADDRESS("Email address"),
    PHONE_NUMBER("Phone number"),
    PERSON_PHONE_NUMBER_ASSOC("Person phone number association"),
    PERSON_EMAIL_ADDRESS_ASSOC("Person email address association"),
    STUDENT_CONTACT_ASSOC("Student contact association"),
    STUDENT_CONTACT_DETAIL("Student contact detail"),
    PG_FINAL_GRADES("PG final grades"),
    SECTIONS("Sections"),
    SECTION_TEACHER("Section teacher"),
    TEACHERS("Teachers"),
    SCHOOL_STAFF("School staff"),
    PSM_TEACHER("PSM teacher"),
    PSM_SECTION("PSM section"),
    PSM_SECTION_TEACHER("PSM section teacher"),
    PSM_SCHOOL_COURSE("PSM school course"),
    GUARDIAN("Guardian"),
    GUARDIAN_STUDENT("Guardian student"),
    GUARDIAN_RELATIONSHIP_TYPE("Guardian relationship type"),
    GUARDIAN_PERSON_ASSOC("Guardian person association"),
    PSM_STUDENT_CONTACT("PSM student contact"),
    PSM_STUDENT_CONTACT_TYPE("PSM student contact type"),
    PSM_GRADE_SCALE("PowerSchool grade scales"),
    PSM_GRADE("PowerSchool grade scale items");

    private final String displayName;

    PowerSchoolImportDatasetType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
