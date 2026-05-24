CREATE TABLE IF NOT EXISTS `guardian_local` (
  `guardian_id` int NOT NULL,
  `account_identifier` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `psguid` varchar(255) DEFAULT NULL,
  `state_guardian_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`guardian_id`),
  KEY `idx_guardian_local_email` (`email`),
  KEY `idx_guardian_local_last_first` (`last_name`,`first_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `guardian_student_local` (
  `guardian_student_id` int NOT NULL,
  `guardian_id` int DEFAULT NULL,
  `guardian_relationship_type_id` int DEFAULT NULL,
  `student_dcid` int DEFAULT NULL,
  `auto_send_attendance_detail` bit(1) DEFAULT NULL,
  `auto_send_balance_alert` bit(1) DEFAULT NULL,
  `auto_send_grade_detail` bit(1) DEFAULT NULL,
  `auto_send_how_often` int DEFAULT NULL,
  `auto_send_school_announcements` bit(1) DEFAULT NULL,
  `auto_send_summary` bit(1) DEFAULT NULL,
  PRIMARY KEY (`guardian_student_id`),
  KEY `idx_guardian_student_guardian` (`guardian_id`),
  KEY `idx_guardian_student_student_dcid` (`student_dcid`),
  KEY `idx_guardian_student_relationship` (`guardian_relationship_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `guardian_relationship_type_local` (
  `guardian_relationship_type_id` int NOT NULL,
  `display_order` int DEFAULT NULL,
  `sif_relation_to_student` int DEFAULT NULL,
  PRIMARY KEY (`guardian_relationship_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `guardian_person_assoc_local` (
  `guardian_person_assoc_id` int NOT NULL,
  `guardian_id` int DEFAULT NULL,
  `person_id` int DEFAULT NULL,
  PRIMARY KEY (`guardian_person_assoc_id`),
  KEY `idx_guardian_person_assoc_guardian` (`guardian_id`),
  KEY `idx_guardian_person_assoc_person` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `psm_student_contact_type_local` (
  `psm_student_contact_type_id` int NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`psm_student_contact_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `psm_student_contact_local` (
  `psm_student_contact_id` int NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `student_contact_type_id` int DEFAULT NULL,
  `student_id` int DEFAULT NULL,
  PRIMARY KEY (`psm_student_contact_id`),
  KEY `idx_psm_student_contact_type` (`student_contact_type_id`),
  KEY `idx_psm_student_contact_student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
