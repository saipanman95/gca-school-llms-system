package org.gca.schoolms.reports;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class StudentLegacyContactStageService {

    private final JdbcTemplate jdbcTemplate;

    public StudentLegacyContactStageService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public StudentLegacyContactRow findByStudentId(Long studentId) {
        String sql = """
            SELECT
                student_id,
                student_dcid,
                student_first_name,
                student_last_name,
                primary_contact_name,
                guardian_stage_email,
                legacy_guardian_email,
                primary_contact_email,
                primary_contact_phone,
                mailing_street,
                mailing_city,
                mailing_state,
                mailing_zip
            FROM student_contact_legacy_stage
            WHERE student_id = ?
            """;
        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                return null;
            }
            return new StudentLegacyContactRow(
                rs.getLong("student_id"),
                getNullableInt(rs, "student_dcid"),
                rs.getString("student_first_name"),
                rs.getString("student_last_name"),
                rs.getString("primary_contact_name"),
                rs.getString("guardian_stage_email"),
                rs.getString("legacy_guardian_email"),
                rs.getString("primary_contact_email"),
                rs.getString("primary_contact_phone"),
                rs.getString("mailing_street"),
                rs.getString("mailing_city"),
                rs.getString("mailing_state"),
                rs.getString("mailing_zip")
            );
        }, studentId);
    }

    private static Integer getNullableInt(java.sql.ResultSet rs, String column) throws java.sql.SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
}
