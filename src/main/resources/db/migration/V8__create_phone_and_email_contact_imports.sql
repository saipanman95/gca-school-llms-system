CREATE TABLE IF NOT EXISTS `phone_number_local` (
  `phone_number_id` int NOT NULL,
  `is_sms` bit(1) DEFAULT NULL,
  `phone_number` varchar(32) DEFAULT NULL,
  `phone_number_ext` varchar(32) DEFAULT NULL,
  `when_created` date DEFAULT NULL,
  `when_modified` date DEFAULT NULL,
  `who_created` varchar(255) DEFAULT NULL,
  `who_modified` varchar(255) DEFAULT NULL,
  `is_unlisted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`phone_number_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `person_phone_number_assoc_local` (
  `person_phone_number_assoc_id` int NOT NULL,
  `person_id` int DEFAULT NULL,
  `phone_number_id` int DEFAULT NULL,
  `phone_number_priority_order` int DEFAULT NULL,
  `phone_type_code_set_id` int DEFAULT NULL,
  `is_preferred` bit(1) DEFAULT NULL,
  `phone_number_as_entered` varchar(64) DEFAULT NULL,
  `when_created` date DEFAULT NULL,
  `when_modified` date DEFAULT NULL,
  `who_created` varchar(255) DEFAULT NULL,
  `who_modified` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`person_phone_number_assoc_id`),
  KEY `idx_person_phone_person` (`person_id`),
  KEY `idx_person_phone_number` (`phone_number_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `person_email_address_assoc_local` (
  `person_email_address_assoc_id` int NOT NULL,
  `email_address_id` int DEFAULT NULL,
  `email_address_priority_order` int DEFAULT NULL,
  `email_type_code_set_id` int DEFAULT NULL,
  `is_primary_email_address` bit(1) DEFAULT NULL,
  `person_id` int DEFAULT NULL,
  `when_created` date DEFAULT NULL,
  `when_modified` date DEFAULT NULL,
  `who_created` varchar(255) DEFAULT NULL,
  `who_modified` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`person_email_address_assoc_id`),
  KEY `idx_person_email_person` (`person_id`),
  KEY `idx_person_email_address` (`email_address_id`)
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
    ranked.guardian_phone_number,
    ranked.guardian_phone_ext,
    ranked.guardian_phone_type_code_set_id,
    ranked.guardian_phone_is_preferred,
    ranked.guardian_phone_is_sms,
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
        COALESCE(NULLIF(TRIM(phone.phone_number_as_entered), ''), NULLIF(TRIM(phone.phone_number), '')) AS guardian_phone_number,
        NULLIF(TRIM(phone.phone_number_ext), '') AS guardian_phone_ext,
        phone.phone_type_code_set_id AS guardian_phone_type_code_set_id,
        CAST(COALESCE(phone.is_preferred, 0) AS UNSIGNED) AS guardian_phone_is_preferred,
        CAST(COALESCE(phone.is_sms, 0) AS UNSIGNED) AS guardian_phone_is_sms,
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
    LEFT JOIN (
        SELECT
            person_id,
            phone_number_as_entered,
            phone_type_code_set_id,
            is_preferred,
            phone_number,
            phone_number_ext,
            is_sms
        FROM (
            SELECT
                ppa.person_id,
                ppa.phone_number_as_entered,
                ppa.phone_type_code_set_id,
                CAST(COALESCE(ppa.is_preferred, b'0') AS UNSIGNED) AS is_preferred,
                pn.phone_number,
                pn.phone_number_ext,
                CAST(COALESCE(pn.is_sms, b'0') AS UNSIGNED) AS is_sms,
                ROW_NUMBER() OVER (
                    PARTITION BY ppa.person_id
                    ORDER BY
                        CAST(COALESCE(ppa.is_preferred, b'0') AS UNSIGNED) DESC,
                        CASE WHEN ppa.phone_number_priority_order IS NULL THEN 1 ELSE 0 END,
                        ppa.phone_number_priority_order,
                        ppa.person_phone_number_assoc_id
                ) AS phone_rank
            FROM person_phone_number_assoc_local ppa
            LEFT JOIN phone_number_local pn
              ON pn.phone_number_id = ppa.phone_number_id
        ) ranked_phone
        WHERE phone_rank = 1
    ) phone
      ON phone.person_id = gpa.person_id
) ranked;
