CREATE TABLE IF NOT EXISTS `teacher_local` (
  `teacher_id` int NOT NULL,
  `dcid` int DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `last_first` varchar(255) DEFAULT NULL,
  `email_addr` varchar(255) DEFAULT NULL,
  `login_id` varchar(255) DEFAULT NULL,
  `teacher_login_id` varchar(255) DEFAULT NULL,
  `teacher_number` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `school_id` int DEFAULT NULL,
  `staff_status` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`teacher_id`),
  UNIQUE KEY `uk_teacher_local_dcid` (`dcid`),
  KEY `idx_teacher_local_teacher_number` (`teacher_number`),
  KEY `idx_teacher_local_teacher_login` (`teacher_login_id`),
  KEY `idx_teacher_local_last_first` (`last_first`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `school_staff_local` (
  `school_staff_id` int NOT NULL,
  `dcid` int DEFAULT NULL,
  `school_id` int DEFAULT NULL,
  `staff_status` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `transaction_date` date DEFAULT NULL,
  `users_dcid` int DEFAULT NULL,
  PRIMARY KEY (`school_staff_id`),
  UNIQUE KEY `uk_school_staff_local_dcid` (`dcid`),
  KEY `idx_school_staff_local_school` (`school_id`),
  KEY `idx_school_staff_local_users_dcid` (`users_dcid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `psm_teacher_local` (
  `psm_teacher_id` int NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `ethnicity` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `is_accessible` bit(1) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `ldap_enabled` bit(1) DEFAULT NULL,
  `teacher_identifier` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`psm_teacher_id`),
  KEY `idx_psm_teacher_identifier` (`teacher_identifier`),
  KEY `idx_psm_teacher_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `psm_section_local` (
  `psm_section_id` int NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `gradebook_type` int DEFAULT NULL,
  `meeting` varchar(255) DEFAULT NULL,
  `room_name` varchar(255) DEFAULT NULL,
  `school_course_id` int DEFAULT NULL,
  `school_id` int DEFAULT NULL,
  `section_identifier` varchar(255) DEFAULT NULL,
  `term_id` int DEFAULT NULL,
  PRIMARY KEY (`psm_section_id`),
  KEY `idx_psm_section_school_course` (`school_course_id`),
  KEY `idx_psm_section_school` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `psm_section_teacher_local` (
  `psm_section_teacher_id` int NOT NULL,
  `allocation` decimal(8,2) DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `priority_order` int DEFAULT NULL,
  `role_id` int DEFAULT NULL,
  `section_id` int DEFAULT NULL,
  `section_nickname` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `teacher_id` int DEFAULT NULL,
  PRIMARY KEY (`psm_section_teacher_id`),
  KEY `idx_psm_section_teacher_section` (`section_id`),
  KEY `idx_psm_section_teacher_teacher` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `psm_school_course_local` (
  `psm_school_course_id` int NOT NULL,
  `abbreviation` varchar(255) DEFAULT NULL,
  `course_code` varchar(255) DEFAULT NULL,
  `course_id` int DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `school_course_title` varchar(255) DEFAULT NULL,
  `school_id` int DEFAULT NULL,
  PRIMARY KEY (`psm_school_course_id`),
  KEY `idx_psm_school_course_code` (`course_code`),
  KEY `idx_psm_school_course_school` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
