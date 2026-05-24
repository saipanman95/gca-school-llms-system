CREATE TABLE IF NOT EXISTS `student_contact_detail_local` (
  `student_contact_detail_id` int NOT NULL,
  `student_contact_assoc_id` int DEFAULT NULL,
  `relationship_type_code_set_id` int DEFAULT NULL,
  `confidential_comm_flag` bit(1) DEFAULT NULL,
  `general_comm_flag` bit(1) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_custodial` bit(1) DEFAULT NULL,
  `is_emergency` bit(1) DEFAULT NULL,
  `lives_with_flag` bit(1) DEFAULT NULL,
  `receives_mail_flag` bit(1) DEFAULT NULL,
  `school_pickup_flag` bit(1) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `relationship_note` varchar(255) DEFAULT NULL,
  `is_caregiver` bit(1) DEFAULT NULL,
  `legal_guardian` bit(1) DEFAULT NULL,
  `physical_address_source` varchar(255) DEFAULT NULL,
  `primary_contact` bit(1) DEFAULT NULL,
  `same_home_phone_number` bit(1) DEFAULT NULL,
  `same_mailing_address` bit(1) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`student_contact_detail_id`),
  KEY `idx_student_contact_detail_assoc` (`student_contact_assoc_id`),
  KEY `idx_student_contact_detail_reltype` (`relationship_type_code_set_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE OR REPLACE VIEW `student_guardian_stage` AS
SELECT
    ranked.student_id,
    ranked.student_dcid,
    ranked.student_first_name,
    ranked.student_last_name,
    ranked.grade_level,
    ranked.school_id,
    ranked.guardian_student_id,
    ranked.guardian_id,
    ranked.guardian_person_id,
    ranked.student_contact_assoc_id,
    ranked.student_contact_detail_id,
    ranked.guardian_name,
    ranked.guardian_email,
    ranked.guardian_account_identifier,
    ranked.guardian_relationship_type_id,
    ranked.relationship_display_order,
    ranked.sif_relation_to_student,
    ranked.notification_preference_score,
    ranked.contact_priority_order,
    ranked.contact_preference_score,
    ranked.confidential_comm_flag,
    ranked.general_comm_flag,
    ranked.is_active,
    ranked.is_custodial,
    ranked.is_emergency,
    ranked.lives_with_flag,
    ranked.receives_mail_flag,
    ranked.school_pickup_flag,
    ranked.is_caregiver,
    ranked.legal_guardian,
    ranked.primary_contact,
    ranked.same_home_phone_number,
    ranked.same_mailing_address,
    ranked.physical_address_source,
    ranked.source,
    ranked.auto_send_attendance_detail,
    ranked.auto_send_balance_alert,
    ranked.auto_send_grade_detail,
    ranked.auto_send_how_often,
    ranked.auto_send_school_announcements,
    ranked.auto_send_summary,
    ranked.relationship_rank,
    CASE WHEN ranked.relationship_rank = 1 THEN 1 ELSE 0 END AS is_primary_guardian
FROM (
    SELECT
        s.student_id,
        s.dcid AS student_dcid,
        s.first_name AS student_first_name,
        s.last_name AS student_last_name,
        s.grade_level,
        s.school_id,
        gs.guardian_student_id,
        gs.guardian_id,
        gpa.person_id AS guardian_person_id,
        sca.student_contact_assoc_id,
        scd.student_contact_detail_id,
        NULLIF(TRIM(CONCAT_WS(' ', gl.first_name, gl.middle_name, gl.last_name)), '') AS guardian_name,
        NULLIF(TRIM(gl.email), '') AS guardian_email,
        NULLIF(TRIM(gl.account_identifier), '') AS guardian_account_identifier,
        gs.guardian_relationship_type_id,
        grt.display_order AS relationship_display_order,
        grt.sif_relation_to_student,
        (
            CAST(COALESCE(gs.auto_send_attendance_detail, b'0') AS UNSIGNED)
            + CAST(COALESCE(gs.auto_send_balance_alert, b'0') AS UNSIGNED)
            + CAST(COALESCE(gs.auto_send_grade_detail, b'0') AS UNSIGNED)
            + CAST(COALESCE(gs.auto_send_school_announcements, b'0') AS UNSIGNED)
            + CAST(COALESCE(gs.auto_send_summary, b'0') AS UNSIGNED)
        ) AS notification_preference_score,
        sca.contact_priority_order,
        (
            (CAST(COALESCE(scd.legal_guardian, b'0') AS UNSIGNED) * 32)
            + (CAST(COALESCE(scd.primary_contact, b'0') AS UNSIGNED) * 16)
            + (CAST(COALESCE(scd.receives_mail_flag, b'0') AS UNSIGNED) * 8)
            + (CAST(COALESCE(scd.is_custodial, b'0') AS UNSIGNED) * 4)
            + (CAST(COALESCE(scd.lives_with_flag, b'0') AS UNSIGNED) * 2)
            + CAST(COALESCE(scd.is_caregiver, b'0') AS UNSIGNED)
        ) AS contact_preference_score,
        CAST(COALESCE(scd.confidential_comm_flag, b'0') AS UNSIGNED) AS confidential_comm_flag,
        CAST(COALESCE(scd.general_comm_flag, b'0') AS UNSIGNED) AS general_comm_flag,
        CAST(COALESCE(scd.is_active, b'0') AS UNSIGNED) AS is_active,
        CAST(COALESCE(scd.is_custodial, b'0') AS UNSIGNED) AS is_custodial,
        CAST(COALESCE(scd.is_emergency, b'0') AS UNSIGNED) AS is_emergency,
        CAST(COALESCE(scd.lives_with_flag, b'0') AS UNSIGNED) AS lives_with_flag,
        CAST(COALESCE(scd.receives_mail_flag, b'0') AS UNSIGNED) AS receives_mail_flag,
        CAST(COALESCE(scd.school_pickup_flag, b'0') AS UNSIGNED) AS school_pickup_flag,
        CAST(COALESCE(scd.is_caregiver, b'0') AS UNSIGNED) AS is_caregiver,
        CAST(COALESCE(scd.legal_guardian, b'0') AS UNSIGNED) AS legal_guardian,
        CAST(COALESCE(scd.primary_contact, b'0') AS UNSIGNED) AS primary_contact,
        CAST(COALESCE(scd.same_home_phone_number, b'0') AS UNSIGNED) AS same_home_phone_number,
        CAST(COALESCE(scd.same_mailing_address, b'0') AS UNSIGNED) AS same_mailing_address,
        NULLIF(TRIM(scd.physical_address_source), '') AS physical_address_source,
        NULLIF(TRIM(scd.source), '') AS source,
        CAST(COALESCE(gs.auto_send_attendance_detail, b'0') AS UNSIGNED) AS auto_send_attendance_detail,
        CAST(COALESCE(gs.auto_send_balance_alert, b'0') AS UNSIGNED) AS auto_send_balance_alert,
        CAST(COALESCE(gs.auto_send_grade_detail, b'0') AS UNSIGNED) AS auto_send_grade_detail,
        gs.auto_send_how_often,
        CAST(COALESCE(gs.auto_send_school_announcements, b'0') AS UNSIGNED) AS auto_send_school_announcements,
        CAST(COALESCE(gs.auto_send_summary, b'0') AS UNSIGNED) AS auto_send_summary,
        ROW_NUMBER() OVER (
            PARTITION BY s.student_id
            ORDER BY
                (CAST(COALESCE(scd.legal_guardian, b'0') AS UNSIGNED)) DESC,
                (CAST(COALESCE(scd.primary_contact, b'0') AS UNSIGNED)) DESC,
                (CAST(COALESCE(scd.receives_mail_flag, b'0') AS UNSIGNED)) DESC,
                (CAST(COALESCE(scd.is_custodial, b'0') AS UNSIGNED)) DESC,
                (CAST(COALESCE(scd.lives_with_flag, b'0') AS UNSIGNED)) DESC,
                (CAST(COALESCE(scd.is_caregiver, b'0') AS UNSIGNED)) DESC,
                (
                    CAST(COALESCE(gs.auto_send_attendance_detail, b'0') AS UNSIGNED)
                    + CAST(COALESCE(gs.auto_send_balance_alert, b'0') AS UNSIGNED)
                    + CAST(COALESCE(gs.auto_send_grade_detail, b'0') AS UNSIGNED)
                    + CAST(COALESCE(gs.auto_send_school_announcements, b'0') AS UNSIGNED)
                    + CAST(COALESCE(gs.auto_send_summary, b'0') AS UNSIGNED)
                ) DESC,
                CASE WHEN sca.contact_priority_order IS NULL THEN 1 ELSE 0 END,
                sca.contact_priority_order,
                COALESCE(grt.display_order, 2147483647),
                COALESCE(gl.last_name, ''),
                COALESCE(gl.first_name, ''),
                gs.guardian_id
        ) AS relationship_rank
    FROM guardian_student_local gs
    JOIN students_local s
      ON s.dcid = gs.student_dcid
    LEFT JOIN guardian_local gl
      ON gl.guardian_id = gs.guardian_id
    LEFT JOIN guardian_person_assoc_local gpa
      ON gpa.guardian_id = gs.guardian_id
    LEFT JOIN student_contact_assoc_local sca
      ON sca.student_dcid = gs.student_dcid
     AND sca.person_id = gpa.person_id
    LEFT JOIN student_contact_detail_local scd
      ON scd.student_contact_assoc_id = sca.student_contact_assoc_id
    LEFT JOIN guardian_relationship_type_local grt
      ON grt.guardian_relationship_type_id = gs.guardian_relationship_type_id
) ranked;
