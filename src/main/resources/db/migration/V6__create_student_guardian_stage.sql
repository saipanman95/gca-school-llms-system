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
    ranked.guardian_name,
    ranked.guardian_email,
    ranked.guardian_account_identifier,
    ranked.guardian_relationship_type_id,
    ranked.relationship_display_order,
    ranked.sif_relation_to_student,
    ranked.notification_preference_score,
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
        CAST(COALESCE(gs.auto_send_attendance_detail, b'0') AS UNSIGNED) AS auto_send_attendance_detail,
        CAST(COALESCE(gs.auto_send_balance_alert, b'0') AS UNSIGNED) AS auto_send_balance_alert,
        CAST(COALESCE(gs.auto_send_grade_detail, b'0') AS UNSIGNED) AS auto_send_grade_detail,
        gs.auto_send_how_often,
        CAST(COALESCE(gs.auto_send_school_announcements, b'0') AS UNSIGNED) AS auto_send_school_announcements,
        CAST(COALESCE(gs.auto_send_summary, b'0') AS UNSIGNED) AS auto_send_summary,
        ROW_NUMBER() OVER (
            PARTITION BY s.student_id
            ORDER BY
                (
                    CAST(COALESCE(gs.auto_send_attendance_detail, b'0') AS UNSIGNED)
                    + CAST(COALESCE(gs.auto_send_balance_alert, b'0') AS UNSIGNED)
                    + CAST(COALESCE(gs.auto_send_grade_detail, b'0') AS UNSIGNED)
                    + CAST(COALESCE(gs.auto_send_school_announcements, b'0') AS UNSIGNED)
                    + CAST(COALESCE(gs.auto_send_summary, b'0') AS UNSIGNED)
                ) DESC,
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
    LEFT JOIN guardian_relationship_type_local grt
      ON grt.guardian_relationship_type_id = gs.guardian_relationship_type_id
) ranked;
