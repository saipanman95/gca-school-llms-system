package org.gca.schoolms.records;

public record StudentGuardianStageRow(
    Long studentId,
    Integer studentDcid,
    String studentFirstName,
    String studentLastName,
    Integer gradeLevel,
    Integer schoolId,
    Integer guardianStudentId,
    Integer guardianId,
    Integer guardianPersonId,
    Integer studentContactAssocId,
    Integer studentContactDetailId,
    String guardianName,
    String guardianEmail,
    String guardianAccountIdentifier,
    String guardianPhoneNumber,
    String guardianPhoneExt,
    Integer guardianPhoneTypeCodeSetId,
    boolean guardianPhonePreferred,
    boolean guardianPhoneSms,
    Integer guardianRelationshipTypeId,
    Integer relationshipDisplayOrder,
    Integer sifRelationToStudent,
    Integer notificationPreferenceScore,
    Integer contactPriorityOrder,
    Integer contactPreferenceScore,
    boolean isActive,
    boolean isCustodial,
    boolean isEmergency,
    boolean livesWith,
    boolean receivesMail,
    boolean isCaregiver,
    boolean legalGuardian,
    boolean primaryContactFlag,
    boolean sameMailingAddress,
    String physicalAddressSource,
    boolean autoSendAttendanceDetail,
    boolean autoSendBalanceAlert,
    boolean autoSendGradeDetail,
    Integer autoSendHowOften,
    boolean autoSendSchoolAnnouncements,
    boolean autoSendSummary,
    Integer relationshipRank,
    boolean primaryGuardian
) {

    public String studentDisplayName() {
        return ((studentFirstName == null ? "" : studentFirstName.trim()) + " "
            + (studentLastName == null ? "" : studentLastName.trim())).trim();
    }

    public String relationshipSummary() {
        StringBuilder summary = new StringBuilder();
        if (guardianRelationshipTypeId != null) {
            summary.append("Type ").append(guardianRelationshipTypeId);
        }
        if (sifRelationToStudent != null) {
            if (!summary.isEmpty()) {
                summary.append(" / ");
            }
            summary.append("SIF ").append(sifRelationToStudent);
        }
        if (relationshipDisplayOrder != null) {
            if (!summary.isEmpty()) {
                summary.append(" / ");
            }
            summary.append("Order ").append(relationshipDisplayOrder);
        }
        return summary.isEmpty() ? "Unclassified relationship" : summary.toString();
    }

    public String guardianPhoneSummary() {
        if (guardianPhoneNumber == null || guardianPhoneNumber.isBlank()) {
            return "No phone on import";
        }
        StringBuilder summary = new StringBuilder(guardianPhoneNumber.trim());
        if (guardianPhoneExt != null && !guardianPhoneExt.isBlank()) {
            summary.append(" ext ").append(guardianPhoneExt.trim());
        }
        if (guardianPhonePreferred) {
            summary.append(" / preferred");
        }
        if (guardianPhoneSms) {
            summary.append(" / SMS");
        }
        return summary.toString();
    }

    public String contactRankingSummary() {
        StringBuilder summary = new StringBuilder();
        appendFlag(summary, legalGuardian, "legal guardian");
        appendFlag(summary, primaryContactFlag, "primary contact");
        appendFlag(summary, receivesMail, "receives mail");
        appendFlag(summary, isCustodial, "custodial");
        appendFlag(summary, livesWith, "lives with");
        appendFlag(summary, isCaregiver, "caregiver");
        appendFlag(summary, isEmergency, "emergency");
        if (summary.isEmpty()) {
            summary.append("No contact detail flags");
        }
        if (contactPriorityOrder != null) {
            summary.append(" / priority ").append(contactPriorityOrder);
        }
        if (contactPreferenceScore != null) {
            summary.append(" / score ").append(contactPreferenceScore);
        }
        return summary.toString();
    }

    public String notificationSummary() {
        StringBuilder summary = new StringBuilder();
        appendFlag(summary, autoSendSummary, "summary");
        appendFlag(summary, autoSendGradeDetail, "grades");
        appendFlag(summary, autoSendAttendanceDetail, "attendance");
        appendFlag(summary, autoSendBalanceAlert, "balance");
        appendFlag(summary, autoSendSchoolAnnouncements, "announcements");
        if (summary.isEmpty()) {
            return "No notification flags";
        }
        if (autoSendHowOften != null && autoSendHowOften > 0) {
            summary.append(" / every ").append(autoSendHowOften).append(" day");
            if (autoSendHowOften != 1) {
                summary.append('s');
            }
        }
        return summary.toString();
    }

    private static void appendFlag(StringBuilder summary, boolean enabled, String label) {
        if (!enabled) {
            return;
        }
        if (!summary.isEmpty()) {
            summary.append(", ");
        }
        summary.append(label);
    }
}
