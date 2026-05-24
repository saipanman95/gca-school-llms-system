ALTER TABLE `students_local`
    ADD COLUMN `mailing_street` varchar(255) DEFAULT NULL,
    ADD COLUMN `mailing_city` varchar(255) DEFAULT NULL,
    ADD COLUMN `mailing_state` varchar(255) DEFAULT NULL,
    ADD COLUMN `mailing_zip` varchar(255) DEFAULT NULL,
    ADD COLUMN `home_phone` varchar(255) DEFAULT NULL,
    ADD COLUMN `guardian_email` varchar(255) DEFAULT NULL,
    ADD COLUMN `father_name` varchar(255) DEFAULT NULL,
    ADD COLUMN `mother_name` varchar(255) DEFAULT NULL,
    ADD COLUMN `father_home_phone` varchar(255) DEFAULT NULL,
    ADD COLUMN `father_day_phone` varchar(255) DEFAULT NULL,
    ADD COLUMN `mother_home_phone` varchar(255) DEFAULT NULL,
    ADD COLUMN `mother_day_phone` varchar(255) DEFAULT NULL;

CREATE OR REPLACE VIEW `student_contact_legacy_stage` AS
SELECT
    sl.student_id,
    sl.dcid AS student_dcid,
    sl.first_name AS student_first_name,
    sl.last_name AS student_last_name,
    NULLIF(TRIM(
        COALESCE(
            NULLIF(TRIM(primary_guardian.guardian_name), ''),
            NULLIF(TRIM(sl.mother_name), ''),
            NULLIF(TRIM(sl.father_name), '')
        )
    ), '') AS primary_contact_name,
    NULLIF(TRIM(primary_guardian.guardian_email), '') AS guardian_stage_email,
    NULLIF(TRIM(sl.guardian_email), '') AS legacy_guardian_email,
    COALESCE(
        NULLIF(TRIM(primary_guardian.guardian_email), ''),
        NULLIF(TRIM(sl.guardian_email), '')
    ) AS primary_contact_email,
    COALESCE(
        NULLIF(TRIM(primary_guardian.guardian_phone_number), ''),
        NULLIF(TRIM(sl.mother_day_phone), ''),
        NULLIF(TRIM(sl.mother_home_phone), ''),
        NULLIF(TRIM(sl.father_day_phone), ''),
        NULLIF(TRIM(sl.father_home_phone), ''),
        NULLIF(TRIM(sl.home_phone), '')
    ) AS primary_contact_phone,
    NULLIF(TRIM(sl.mailing_street), '') AS mailing_street,
    NULLIF(TRIM(sl.mailing_city), '') AS mailing_city,
    NULLIF(TRIM(sl.mailing_state), '') AS mailing_state,
    NULLIF(TRIM(sl.mailing_zip), '') AS mailing_zip
FROM students_local sl
LEFT JOIN (
    SELECT
        student_id,
        guardian_name,
        guardian_email,
        guardian_phone_number
    FROM student_guardian_stage
    WHERE is_primary_guardian = 1
) primary_guardian
  ON primary_guardian.student_id = sl.student_id;
