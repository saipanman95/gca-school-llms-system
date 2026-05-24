package org.gca.schoolms.records;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class StudentGuardianStageService {

    private final JdbcTemplate jdbcTemplate;

    public StudentGuardianStageService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<Long, List<StudentGuardianStageRow>> findByStudentIds(List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return Collections.emptyMap();
        }
        String placeholders = String.join(", ", Collections.nCopies(studentIds.size(), "?"));
        String sql = """
            SELECT
                student_id,
                student_dcid,
                student_first_name,
                student_last_name,
                grade_level,
                school_id,
                guardian_student_id,
                guardian_id,
                guardian_person_id,
                student_contact_assoc_id,
                student_contact_detail_id,
                guardian_name,
                guardian_email,
                guardian_account_identifier,
                guardian_phone_number,
                guardian_phone_ext,
                guardian_phone_type_code_set_id,
                guardian_phone_is_preferred,
                guardian_phone_is_sms,
                guardian_relationship_type_id,
                relationship_display_order,
                sif_relation_to_student,
                notification_preference_score,
                contact_priority_order,
                contact_preference_score,
                is_active,
                is_custodial,
                is_emergency,
                lives_with_flag,
                receives_mail_flag,
                is_caregiver,
                legal_guardian,
                primary_contact,
                same_mailing_address,
                physical_address_source,
                auto_send_attendance_detail,
                auto_send_balance_alert,
                auto_send_grade_detail,
                auto_send_how_often,
                auto_send_school_announcements,
                auto_send_summary,
                relationship_rank,
                is_primary_guardian
            FROM student_guardian_stage
            WHERE student_id IN (%s)
            ORDER BY student_last_name, student_first_name, relationship_rank, guardian_id
            """.formatted(placeholders);

        Map<Long, List<StudentGuardianStageRow>> rowsByStudent = new LinkedHashMap<>();
        jdbcTemplate.query(sql, studentIds.toArray(), rs -> {
            StudentGuardianStageRow row = mapRow(rs);
            rowsByStudent.computeIfAbsent(row.studentId(), ignored -> new ArrayList<>()).add(row);
        });
        return rowsByStudent;
    }

    public List<StudentGuardianStageRow> findPreviewRows(int limit) {
        String sql = """
            SELECT
                student_id,
                student_dcid,
                student_first_name,
                student_last_name,
                grade_level,
                school_id,
                guardian_student_id,
                guardian_id,
                guardian_person_id,
                student_contact_assoc_id,
                student_contact_detail_id,
                guardian_name,
                guardian_email,
                guardian_account_identifier,
                guardian_phone_number,
                guardian_phone_ext,
                guardian_phone_type_code_set_id,
                guardian_phone_is_preferred,
                guardian_phone_is_sms,
                guardian_relationship_type_id,
                relationship_display_order,
                sif_relation_to_student,
                notification_preference_score,
                contact_priority_order,
                contact_preference_score,
                is_active,
                is_custodial,
                is_emergency,
                lives_with_flag,
                receives_mail_flag,
                is_caregiver,
                legal_guardian,
                primary_contact,
                same_mailing_address,
                physical_address_source,
                auto_send_attendance_detail,
                auto_send_balance_alert,
                auto_send_grade_detail,
                auto_send_how_often,
                auto_send_school_announcements,
                auto_send_summary,
                relationship_rank,
                is_primary_guardian
            FROM student_guardian_stage
            ORDER BY student_last_name, student_first_name, relationship_rank, guardian_id
            LIMIT ?
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), limit);
    }

    public String summarizeForStudent(List<StudentGuardianStageRow> guardians) {
        if (guardians == null || guardians.isEmpty()) {
            return null;
        }
        List<String> primaryNames = guardians.stream()
            .filter(StudentGuardianStageRow::primaryGuardian)
            .map(StudentGuardianStageRow::guardianName)
            .filter(name -> name != null && !name.isBlank())
            .distinct()
            .toList();
        if (!primaryNames.isEmpty()) {
            return String.join(" / ", primaryNames);
        }
        List<String> names = guardians.stream()
            .map(StudentGuardianStageRow::guardianName)
            .filter(name -> name != null && !name.isBlank())
            .distinct()
            .limit(2)
            .toList();
        if (!names.isEmpty()) {
            return String.join(" / ", names);
        }
        return guardians.size() + " guardian link" + (guardians.size() == 1 ? "" : "s");
    }

    private StudentGuardianStageRow mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new StudentGuardianStageRow(
            rs.getLong("student_id"),
            getNullableInt(rs, "student_dcid"),
            rs.getString("student_first_name"),
            rs.getString("student_last_name"),
            getNullableInt(rs, "grade_level"),
            getNullableInt(rs, "school_id"),
            getNullableInt(rs, "guardian_student_id"),
            getNullableInt(rs, "guardian_id"),
            getNullableInt(rs, "guardian_person_id"),
            getNullableInt(rs, "student_contact_assoc_id"),
            getNullableInt(rs, "student_contact_detail_id"),
            rs.getString("guardian_name"),
            rs.getString("guardian_email"),
            rs.getString("guardian_account_identifier"),
            rs.getString("guardian_phone_number"),
            rs.getString("guardian_phone_ext"),
            getNullableInt(rs, "guardian_phone_type_code_set_id"),
            rs.getInt("guardian_phone_is_preferred") == 1,
            rs.getInt("guardian_phone_is_sms") == 1,
            getNullableInt(rs, "guardian_relationship_type_id"),
            getNullableInt(rs, "relationship_display_order"),
            getNullableInt(rs, "sif_relation_to_student"),
            getNullableInt(rs, "notification_preference_score"),
            getNullableInt(rs, "contact_priority_order"),
            getNullableInt(rs, "contact_preference_score"),
            rs.getInt("is_active") == 1,
            rs.getInt("is_custodial") == 1,
            rs.getInt("is_emergency") == 1,
            rs.getInt("lives_with_flag") == 1,
            rs.getInt("receives_mail_flag") == 1,
            rs.getInt("is_caregiver") == 1,
            rs.getInt("legal_guardian") == 1,
            rs.getInt("primary_contact") == 1,
            rs.getInt("same_mailing_address") == 1,
            rs.getString("physical_address_source"),
            rs.getInt("auto_send_attendance_detail") == 1,
            rs.getInt("auto_send_balance_alert") == 1,
            rs.getInt("auto_send_grade_detail") == 1,
            getNullableInt(rs, "auto_send_how_often"),
            rs.getInt("auto_send_school_announcements") == 1,
            rs.getInt("auto_send_summary") == 1,
            getNullableInt(rs, "relationship_rank"),
            rs.getInt("is_primary_guardian") == 1
        );
    }

    private static Integer getNullableInt(java.sql.ResultSet rs, String column) throws java.sql.SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
}
