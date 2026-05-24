package org.gca.schoolms.certificates;

import jakarta.annotation.PostConstruct;
import java.sql.Date;
import java.time.LocalDate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CertificateSettingsService {

    private static final int SETTINGS_ROW_ID = 1;

    private final JdbcTemplate jdbcTemplate;

    public CertificateSettingsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    void initializeTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS certificate_settings (
                settings_id INT PRIMARY KEY,
                issue_date DATE NOT NULL,
                issue_location VARCHAR(255) NOT NULL,
                principal_name VARCHAR(255) NOT NULL,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    ON UPDATE CURRENT_TIMESTAMP
            )
            """);

        Integer existing = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM certificate_settings WHERE settings_id = ?",
            Integer.class,
            SETTINGS_ROW_ID
        );

        if (existing != null && existing == 0) {
            jdbcTemplate.update("""
                    INSERT INTO certificate_settings (
                        settings_id,
                        issue_date,
                        issue_location,
                        principal_name
                    ) VALUES (?, ?, ?, ?)
                    """,
                SETTINGS_ROW_ID,
                Date.valueOf(LocalDate.of(2025, 12, 19)),
                "Navy Hill, Saipan, Northern Mariana Islands",
                "Geraldine T.A. Rodgers, MS"
            );
        }
    }

    @Transactional(readOnly = true)
    public CertificateSettingsView getSettings() {
        return jdbcTemplate.queryForObject("""
                SELECT issue_date, issue_location, principal_name
                FROM certificate_settings
                WHERE settings_id = ?
                """,
            (rs, rowNum) -> new CertificateSettingsView(
                rs.getDate("issue_date").toLocalDate(),
                rs.getString("issue_location"),
                rs.getString("principal_name")
            ),
            SETTINGS_ROW_ID
        );
    }
}
