
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `attendance_record` (
  `attendance_date` date NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `section_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `status` enum('ABSENT','EXCUSED','PRESENT','TARDY') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKp8dv3bv4f18dmvwakdpgbtxpt` (`section_id`),
  KEY `FK9p10lf3n7tcuouo29ery5obq6` (`student_id`),
  CONSTRAINT `FK9p10lf3n7tcuouo29ery5obq6` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
  CONSTRAINT `FKp8dv3bv4f18dmvwakdpgbtxpt` FOREIGN KEY (`section_id`) REFERENCES `section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `award_rule` (
  `cumulative_end_grade` int DEFAULT NULL,
  `cumulative_start_grade` int DEFAULT NULL,
  `evaluated_per_quarter` bit(1) NOT NULL,
  `evaluated_per_school_year` bit(1) NOT NULL,
  `maximum_rank` int DEFAULT NULL,
  `minimum_gpa` decimal(6,2) DEFAULT NULL,
  `minimum_numeric_grade` int DEFAULT NULL,
  `minimum_rank` int DEFAULT NULL,
  `ranking_based` bit(1) NOT NULL,
  `requires_full_residency_across_window` bit(1) NOT NULL,
  `residency_end_grade` int DEFAULT NULL,
  `residency_start_grade` int DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `rule_set_id` bigint NOT NULL,
  `award_category` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `notes` longtext,
  PRIMARY KEY (`id`),
  KEY `FKa65w528h9ohyq5gt5xcfhr7sw` (`rule_set_id`),
  CONSTRAINT `FKa65w528h9ohyq5gt5xcfhr7sw` FOREIGN KEY (`rule_set_id`) REFERENCES `award_rule_set` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `award_rule_set` (
  `active` bit(1) NOT NULL,
  `effective_end_date` date DEFAULT NULL,
  `effective_start_date` date NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKippgudqpvwxfwev14go03ilj9` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `campus` (
  `active` bit(1) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `island` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKlwt9w8c1jhw24upw55r6bmpdi` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `certificate_settings` (
  `settings_id` int NOT NULL,
  `issue_date` date NOT NULL,
  `issue_location` varchar(255) NOT NULL,
  `principal_name` varchar(255) NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`settings_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `enrollment_attestation` (
  `confirmed_true_and_correct` bit(1) NOT NULL,
  `attested_on` datetime(6) NOT NULL,
  `enrollment_request_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_initials` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKx64e60aiwkr2r1ycg8qp18bw` (`enrollment_request_id`),
  CONSTRAINT `FKfoy55bwocn237g6u1yj0hasax` FOREIGN KEY (`enrollment_request_id`) REFERENCES `enrollment_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `enrollment_document` (
  `date_uploaded` datetime(6) NOT NULL,
  `enrollment_request_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint DEFAULT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `record_name` varchar(255) NOT NULL,
  `storage_path` varchar(255) NOT NULL,
  `stored_filename` varchar(255) NOT NULL,
  `record_type` enum('BANK_CERTIFICATE','BIRTH_CERTIFICATE','CHC_BLUE_CARE','GCA_TRANSCRIPT','GUARDIANSHIP_DOCUMENT','HEALTH_CERTIFICATE','HEALTH_PROFILE','MEDICAL_RECORD','OFFICIAL_TRANSCRIPT','PREVIOUS_SCHOOL_I20','PREVIOUS_SCHOOL_TRANSCRIPT','RECENT_PHOTOGRAPH','REPORT_CARD','STUDENT_PASSPORT','STUDENT_VISA','VACCINATION_CARD') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKeqxafdxlh00ftsmfwl4c25rr9` (`enrollment_request_id`),
  KEY `FKdg88h30h7tmhlaxn0x9yo30pq` (`student_id`),
  CONSTRAINT `FKdg88h30h7tmhlaxn0x9yo30pq` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
  CONSTRAINT `FKeqxafdxlh00ftsmfwl4c25rr9` FOREIGN KEY (`enrollment_request_id`) REFERENCES `enrollment_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `enrollment_emergency_contact` (
  `pickup_authorized` bit(1) NOT NULL,
  `enrollment_request_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contact_name` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `primary_phone` varchar(255) NOT NULL,
  `relationship_to_student` varchar(255) DEFAULT NULL,
  `secondary_phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpq4ynbdmyx0imsp0pv8wb45jl` (`enrollment_request_id`),
  CONSTRAINT `FKpq4ynbdmyx0imsp0pv8wb45jl` FOREIGN KEY (`enrollment_request_id`) REFERENCES `enrollment_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `enrollment_finance_authorization` (
  `approved_on` date NOT NULL,
  `enrollment_request_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `notes` varchar(1000) DEFAULT NULL,
  `authorization_type` enum('CCDF_INHOUSE_GRANT','ENROLLMENT_FEE_PAID','OTHER','PAYMENT_AGREEMENT_SIGNED','PAYMENT_RECEIVED','SCHOLARSHIP_GRANTED') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKau9xoii52ewu8it4wjqqfvaeu` (`enrollment_request_id`),
  CONSTRAINT `FKau9xoii52ewu8it4wjqqfvaeu` FOREIGN KEY (`enrollment_request_id`) REFERENCES `enrollment_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `enrollment_request` (
  `allow_aspirin` bit(1) NOT NULL,
  `allow_hydrocortisone` bit(1) NOT NULL,
  `allow_pepto_bismol` bit(1) NOT NULL,
  `allow_robitussin` bit(1) NOT NULL,
  `allow_tums` bit(1) NOT NULL,
  `allow_tylenol` bit(1) NOT NULL,
  `child_potty_trained` bit(1) NOT NULL,
  `emergency_contact_release_consent` bit(1) NOT NULL,
  `emergency_treatment_consent` bit(1) NOT NULL,
  `finance_reviewed_on` date DEFAULT NULL,
  `guardian_visa_expiration_date` date DEFAULT NULL,
  `guardian_visa_issue_date` date DEFAULT NULL,
  `guardian_visa_required` bit(1) NOT NULL,
  `medication_administration_consent` bit(1) NOT NULL,
  `primary_guardian_billing_recipient` bit(1) NOT NULL,
  `registrar_reviewed_on` date DEFAULT NULL,
  `secondary_guardian_portal_access` bit(1) NOT NULL,
  `secondary_visa_expiration_date` date DEFAULT NULL,
  `secondary_visa_issue_date` date DEFAULT NULL,
  `secondary_visa_required` bit(1) NOT NULL,
  `student_date_of_birth` date DEFAULT NULL,
  `student_visa_expiration_date` date DEFAULT NULL,
  `student_visa_issue_date` date DEFAULT NULL,
  `student_visa_required` bit(1) NOT NULL,
  `studentf1required` bit(1) NOT NULL,
  `submitted_on` date NOT NULL,
  `campus_id` bigint NOT NULL,
  `family_account_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint DEFAULT NULL,
  `guardian_email` varchar(255) NOT NULL,
  `guardian_employer_name` varchar(255) DEFAULT NULL,
  `guardian_ethnicity` varchar(255) DEFAULT NULL,
  `guardian_gender` varchar(255) DEFAULT NULL,
  `guardian_mailing_address_line1` varchar(255) NOT NULL,
  `guardian_mailing_address_line2` varchar(255) DEFAULT NULL,
  `guardian_mailing_city` varchar(255) NOT NULL,
  `guardian_mailing_postal_code` varchar(255) NOT NULL,
  `guardian_mailing_state` varchar(255) NOT NULL,
  `guardian_name` varchar(255) NOT NULL,
  `guardian_phone` varchar(255) NOT NULL,
  `guardian_visa_number` varchar(255) DEFAULT NULL,
  `guardian_visa_type` varchar(255) DEFAULT NULL,
  `guardian_work_email` varchar(255) DEFAULT NULL,
  `guardian_work_phone` varchar(255) DEFAULT NULL,
  `school_year` varchar(255) NOT NULL,
  `secondary_employer_name` varchar(255) DEFAULT NULL,
  `secondary_ethnicity` varchar(255) DEFAULT NULL,
  `secondary_gender` varchar(255) DEFAULT NULL,
  `secondary_guardian_email` varchar(255) DEFAULT NULL,
  `secondary_guardian_name` varchar(255) DEFAULT NULL,
  `secondary_guardian_phone` varchar(255) DEFAULT NULL,
  `secondary_visa_number` varchar(255) DEFAULT NULL,
  `secondary_visa_type` varchar(255) DEFAULT NULL,
  `secondary_work_email` varchar(255) DEFAULT NULL,
  `secondary_work_phone` varchar(255) DEFAULT NULL,
  `student_alias` varchar(255) DEFAULT NULL,
  `student_first_name` varchar(255) NOT NULL,
  `student_last_name` varchar(255) NOT NULL,
  `student_middle_name` varchar(255) DEFAULT NULL,
  `student_suffix` varchar(255) DEFAULT NULL,
  `student_visa_number` varchar(255) DEFAULT NULL,
  `student_visa_type` varchar(255) DEFAULT NULL,
  `finance_comment` text,
  `finance_review_status` enum('BLOCKED','CLEARED','CONDITIONALLY_CLEARED','PENDING') NOT NULL,
  `guardian_citizenship_status` text,
  `guardian_country_of_citizenship` text,
  `guardian_work_address_line1` text,
  `guardian_work_address_line2` text,
  `guardian_work_city` text,
  `guardian_work_postal_code` text,
  `guardian_work_state` text,
  `insurance_policy_number` text,
  `insurance_provider` text,
  `marital_status` enum('DIVORCED','MARRIED','SEPARATED','SINGLE','WIDOWED') DEFAULT NULL,
  `other_approved_medications` text,
  `physician_clinic_name` text,
  `physician_phone` text,
  `potty_accident_frequency` text,
  `preferred_hospital` text,
  `previous_school_city` text,
  `previous_school_country` text,
  `previous_school_last_grade_completed` text,
  `previous_school_name` text,
  `previous_school_state` text,
  `primary_physician_name` text,
  `registrar_comment` text,
  `registrar_review_status` enum('COMPLETE','MISSING_DETAILS','PENDING') NOT NULL,
  `request_type` enum('NEW_STUDENT','REENROLLMENT') NOT NULL,
  `requested_grade_level` enum('GRADE_1','GRADE_10','GRADE_11','GRADE_12','GRADE_2','GRADE_3','GRADE_4','GRADE_5','GRADE_6','GRADE_7','GRADE_8','GRADE_9','K4','K5') NOT NULL,
  `secondary_citizenship_status` text,
  `secondary_country_of_citizenship` text,
  `secondary_mailing_address_line1` text,
  `secondary_mailing_address_line2` text,
  `secondary_mailing_city` text,
  `secondary_mailing_postal_code` text,
  `secondary_mailing_state` text,
  `secondary_work_address_line1` text,
  `secondary_work_address_line2` text,
  `secondary_work_city` text,
  `secondary_work_postal_code` text,
  `secondary_work_state` text,
  `status` enum('DRAFT','ENROLLED','FINANCE_HOLD','READY_FOR_FINANCE','READY_TO_ENROLL','SUBMITTED') NOT NULL,
  `student_activity_restrictions` text,
  `student_allergies` text,
  `student_chronic_conditions` text,
  `student_church_attending` text,
  `student_citizenship_status` text,
  `student_country_of_citizenship` text,
  `student_dietary_restrictions` text,
  `student_ethnic_background_other` text,
  `student_ethnic_backgrounds` text,
  `student_medical_notes` text,
  `student_medications` text,
  `student_religious_affiliation` text,
  `studenti20status` text,
  PRIMARY KEY (`id`),
  KEY `FK55kny0xyatic6m909u78n7wpp` (`campus_id`),
  KEY `FKplqcfefii1sxgg9npx1e0xdea` (`family_account_id`),
  KEY `FKabbu2g1u4hbp70xs5tcxgrp5o` (`student_id`),
  CONSTRAINT `FK55kny0xyatic6m909u78n7wpp` FOREIGN KEY (`campus_id`) REFERENCES `campus` (`id`),
  CONSTRAINT `FKabbu2g1u4hbp70xs5tcxgrp5o` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
  CONSTRAINT `FKplqcfefii1sxgg9npx1e0xdea` FOREIGN KEY (`family_account_id`) REFERENCES `family_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `enrollment_request_language` (
  `preference_rank` int DEFAULT NULL,
  `enrollment_request_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `language_name` varchar(255) NOT NULL,
  `proficiency_level` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi4s4oe4vhd612tia6lq395vqm` (`enrollment_request_id`),
  CONSTRAINT `FKi4s4oe4vhd612tia6lq395vqm` FOREIGN KEY (`enrollment_request_id`) REFERENCES `enrollment_request` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `family_account` (
  `primary_guardian_billing_recipient` bit(1) NOT NULL,
  `secondary_guardian_portal_access` bit(1) NOT NULL,
  `secondary_visa_expiration_date` date DEFAULT NULL,
  `secondary_visa_issue_date` date DEFAULT NULL,
  `secondary_visa_required` bit(1) NOT NULL,
  `visa_expiration_date` date DEFAULT NULL,
  `visa_issue_date` date DEFAULT NULL,
  `visa_required` bit(1) NOT NULL,
  `campus_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_name` varchar(255) NOT NULL,
  `account_number` varchar(255) NOT NULL,
  `citizenship_status` varchar(255) DEFAULT NULL,
  `country_of_citizenship` varchar(255) DEFAULT NULL,
  `employer_name` varchar(255) DEFAULT NULL,
  `ethnicity` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `mailing_address_line1` varchar(255) NOT NULL,
  `mailing_address_line2` varchar(255) DEFAULT NULL,
  `mailing_city` varchar(255) NOT NULL,
  `mailing_postal_code` varchar(255) NOT NULL,
  `mailing_state` varchar(255) NOT NULL,
  `primary_guardian_email` varchar(255) NOT NULL,
  `primary_guardian_name` varchar(255) NOT NULL,
  `primary_guardian_phone` varchar(255) NOT NULL,
  `secondary_citizenship_status` varchar(255) DEFAULT NULL,
  `secondary_country_of_citizenship` varchar(255) DEFAULT NULL,
  `secondary_employer_name` varchar(255) DEFAULT NULL,
  `secondary_ethnicity` varchar(255) DEFAULT NULL,
  `secondary_gender` varchar(255) DEFAULT NULL,
  `secondary_guardian_email` varchar(255) DEFAULT NULL,
  `secondary_guardian_name` varchar(255) DEFAULT NULL,
  `secondary_guardian_phone` varchar(255) DEFAULT NULL,
  `secondary_mailing_address_line1` varchar(255) DEFAULT NULL,
  `secondary_mailing_address_line2` varchar(255) DEFAULT NULL,
  `secondary_mailing_city` varchar(255) DEFAULT NULL,
  `secondary_mailing_postal_code` varchar(255) DEFAULT NULL,
  `secondary_mailing_state` varchar(255) DEFAULT NULL,
  `secondary_visa_number` varchar(255) DEFAULT NULL,
  `secondary_visa_type` varchar(255) DEFAULT NULL,
  `secondary_work_address_line1` varchar(255) DEFAULT NULL,
  `secondary_work_address_line2` varchar(255) DEFAULT NULL,
  `secondary_work_city` varchar(255) DEFAULT NULL,
  `secondary_work_email` varchar(255) DEFAULT NULL,
  `secondary_work_phone` varchar(255) DEFAULT NULL,
  `secondary_work_postal_code` varchar(255) DEFAULT NULL,
  `secondary_work_state` varchar(255) DEFAULT NULL,
  `visa_number` varchar(255) DEFAULT NULL,
  `visa_type` varchar(255) DEFAULT NULL,
  `work_address_line1` varchar(255) DEFAULT NULL,
  `work_address_line2` varchar(255) DEFAULT NULL,
  `work_city` varchar(255) DEFAULT NULL,
  `work_email` varchar(255) DEFAULT NULL,
  `work_phone` varchar(255) DEFAULT NULL,
  `work_postal_code` varchar(255) DEFAULT NULL,
  `work_state` varchar(255) DEFAULT NULL,
  `marital_status` enum('DIVORCED','MARRIED','SEPARATED','SINGLE','WIDOWED') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK5my4jdf826cakin5axck0ulf8` (`account_number`),
  KEY `FK74bkdqmri1n77c5v85nwdydvp` (`campus_id`),
  CONSTRAINT `FK74bkdqmri1n77c5v85nwdydvp` FOREIGN KEY (`campus_id`) REFERENCES `campus` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `fee_schedule` (
  `active` bit(1) NOT NULL,
  `campus_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `school_year` varchar(255) NOT NULL,
  `grade_group` enum('ALL_STUDENTS','DAYCARE','ELEMENTARY','HIGH_SCHOOL','JUNIOR_HIGH') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb2buubipv6kaegbm3719xr8b` (`campus_id`),
  CONSTRAINT `FKb2buubipv6kaegbm3719xr8b` FOREIGN KEY (`campus_id`) REFERENCES `campus` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `fee_schedule_item` (
  `amount` decimal(12,2) NOT NULL,
  `sort_order` int NOT NULL,
  `fee_schedule_id` bigint NOT NULL,
  `fee_type_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhmhy2o1lqm6skw5s73jov82h9` (`fee_schedule_id`),
  KEY `FKcwleiq3ewilmlkbfpi07v7y8d` (`fee_type_id`),
  CONSTRAINT `FKcwleiq3ewilmlkbfpi07v7y8d` FOREIGN KEY (`fee_type_id`) REFERENCES `fee_type` (`id`),
  CONSTRAINT `FKhmhy2o1lqm6skw5s73jov82h9` FOREIGN KEY (`fee_schedule_id`) REFERENCES `fee_schedule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `fee_type` (
  `active` bit(1) NOT NULL,
  `billing_month_count` int DEFAULT NULL,
  `default_amount` decimal(12,2) DEFAULT NULL,
  `max_assessments_per_student_per_school_year` int DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `billing_schedule` enum('MONTHLY','ONE_TIME') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3gef756klr7e0e2ua32iogumj` (`code`),
  UNIQUE KEY `UKeo6i18q0uj8b57divliduhl2o` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `grading_scale` (
  `display_order` int NOT NULL,
  `max_percent` decimal(38,2) NOT NULL,
  `min_percent` decimal(38,2) NOT NULL,
  `grade_code` varchar(255) NOT NULL,
  PRIMARY KEY (`grade_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `grading_scale_band` (
  `gpa_points` decimal(38,2) NOT NULL,
  `max_score` decimal(38,2) NOT NULL,
  `min_score` decimal(38,2) NOT NULL,
  `sort_order` int NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `scale_set_id` bigint NOT NULL,
  `letter_grade` varchar(255) NOT NULL,
  `track_code` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKaof9t6avq5730osrcorldlb6x` (`scale_set_id`),
  CONSTRAINT `FKaof9t6avq5730osrcorldlb6x` FOREIGN KEY (`scale_set_id`) REFERENCES `grading_scale_set` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `grading_scale_set` (
  `active` bit(1) NOT NULL,
  `effective_end_date` date DEFAULT NULL,
  `effective_start_date` date NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK50p1tk40r2vnjpldfkka0n1r5` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `grading_special_mark` (
  `counts_for_credit` bit(1) NOT NULL,
  `counts_for_gpa` bit(1) NOT NULL,
  `sort_order` int NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `scale_set_id` bigint NOT NULL,
  `code` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2t8kj3won1mcm7kpvuy79tndp` (`scale_set_id`),
  CONSTRAINT `FK2t8kj3won1mcm7kpvuy79tndp` FOREIGN KEY (`scale_set_id`) REFERENCES `grading_scale_set` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `invoice` (
  `due_date` date NOT NULL,
  `outstanding_amount` decimal(12,2) NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `campus_id` bigint NOT NULL,
  `family_account_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `status` enum('OPEN','PAID','PARTIAL','VOID') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdpmv8ya5i10ia7bxijjd4is5o` (`campus_id`),
  KEY `FK820wy9vn4efq7439qbqigkq6m` (`family_account_id`),
  CONSTRAINT `FK820wy9vn4efq7439qbqigkq6m` FOREIGN KEY (`family_account_id`) REFERENCES `family_account` (`id`),
  CONSTRAINT `FKdpmv8ya5i10ia7bxijjd4is5o` FOREIGN KEY (`campus_id`) REFERENCES `campus` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `payer_profile` (
  `family_account_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `business_name` varchar(255) DEFAULT NULL,
  `email_address` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `mailing_address_line1` varchar(255) DEFAULT NULL,
  `mailing_address_line2` varchar(255) DEFAULT NULL,
  `mailing_city` varchar(255) DEFAULT NULL,
  `mailing_postal_code` varchar(255) DEFAULT NULL,
  `mailing_state` varchar(255) DEFAULT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9ak16kisc537qc6nys890k545` (`family_account_id`),
  CONSTRAINT `FK9ak16kisc537qc6nys890k545` FOREIGN KEY (`family_account_id`) REFERENCES `family_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `payment` (
  `anonymous_to_family` bit(1) NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `family_account_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payer_profile_id` bigint NOT NULL,
  `payment_date` datetime(6) NOT NULL,
  `school_project_type_id` bigint DEFAULT NULL,
  `target_student_id` bigint DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `received_by_user_id` varchar(255) NOT NULL,
  `reference_number` varchar(255) DEFAULT NULL,
  `payment_method` enum('CASH','CCDF_INHOUSE_GRANT','CHECK','CREDIT_CARD','DISCOUNT_APPLIED','SCHOLARSHIP_APPLIED') NOT NULL,
  `payment_purpose` enum('EDUCATION_TAX_CREDIT','GIFT_TO_SCHOOL','STUDENT_ACCOUNT') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKorpbil9kwds41xkyccotclwkf` (`family_account_id`),
  KEY `FKcry94qrtituu13pp7aipt50ol` (`payer_profile_id`),
  KEY `FKsq2q8o3oo3lqon874bk13k5ju` (`school_project_type_id`),
  KEY `FKselq4w5yuv72qbkgwvsrr0ke0` (`target_student_id`),
  CONSTRAINT `FKcry94qrtituu13pp7aipt50ol` FOREIGN KEY (`payer_profile_id`) REFERENCES `payer_profile` (`id`),
  CONSTRAINT `FKorpbil9kwds41xkyccotclwkf` FOREIGN KEY (`family_account_id`) REFERENCES `family_account` (`id`),
  CONSTRAINT `FKselq4w5yuv72qbkgwvsrr0ke0` FOREIGN KEY (`target_student_id`) REFERENCES `student` (`id`),
  CONSTRAINT `FKsq2q8o3oo3lqon874bk13k5ju` FOREIGN KEY (`school_project_type_id`) REFERENCES `school_project_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `payment_allocation` (
  `amount_applied` decimal(12,2) NOT NULL,
  `applied_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_id` bigint NOT NULL,
  `student_fee_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKs5r8ysirc9mfdlrxuejnye3c1` (`payment_id`),
  KEY `FKbsuvc6j8d2e2ot2486oev005q` (`student_fee_id`),
  CONSTRAINT `FKbsuvc6j8d2e2ot2486oev005q` FOREIGN KEY (`student_fee_id`) REFERENCES `student_fee` (`id`),
  CONSTRAINT `FKs5r8ysirc9mfdlrxuejnye3c1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `person_address_assoc_local` (
  `address_priority_order` int DEFAULT NULL,
  `address_type_code_set_id` int DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `person_address_assoc_id` int NOT NULL,
  `person_address_id` int DEFAULT NULL,
  `person_id` int DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  PRIMARY KEY (`person_address_assoc_id`),
  KEY `idx_person_address_assoc_person` (`person_id`),
  KEY `idx_person_address_assoc_address` (`person_address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `person_address_local` (
  `person_address_id` int NOT NULL,
  `state_code_set_id` int DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `line_two` varchar(255) DEFAULT NULL,
  `postal_code` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`person_address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `person_local` (
  `dcid` int DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `person_id` int NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`person_id`),
  UNIQUE KEY `UKdjt84x7oyjtv4oa3fxel564ar` (`dcid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `pg_final_grades_local` (
  `dcid` int DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `percent` decimal(6,2) DEFAULT NULL,
  `pg_final_grade_id` int NOT NULL,
  `section_id` int DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `student_id` int DEFAULT NULL,
  `citizenship` varchar(255) DEFAULT NULL,
  `final_grade_name` varchar(255) DEFAULT NULL,
  `grade_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pg_final_grade_id`),
  KEY `idx_pg_final_grades_student_term` (`student_id`,`final_grade_name`),
  KEY `idx_pg_final_grades_section` (`section_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `powerschool_grade_scale_item_local` (
  `admin_use_only` bit(1) DEFAULT NULL,
  `cutoff_percent` decimal(8,3) DEFAULT NULL,
  `default_zero_cutoff` bit(1) DEFAULT NULL,
  `grade_item_id` int NOT NULL,
  `grade_scale_id` int DEFAULT NULL,
  `is_special` bit(1) DEFAULT NULL,
  `percent_value` decimal(8,3) DEFAULT NULL,
  `points_value` decimal(8,3) DEFAULT NULL,
  `sort_order` int DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `grade_label` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`grade_item_id`),
  KEY `idx_ps_grade_item_scale_cutoff` (`grade_scale_id`,`cutoff_percent`),
  KEY `idx_ps_grade_item_scale_label` (`grade_scale_id`,`grade_label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `powerschool_grade_scale_local` (
  `can_modify` bit(1) DEFAULT NULL,
  `content_group_id` int DEFAULT NULL,
  `grade_scale_id` int NOT NULL,
  `is_district_default` bit(1) DEFAULT NULL,
  `is_numeric` bit(1) DEFAULT NULL,
  `numeric_max` decimal(38,2) DEFAULT NULL,
  `numeric_min` decimal(38,2) DEFAULT NULL,
  `numeric_precision` int DEFAULT NULL,
  `numeric_scale` int DEFAULT NULL,
  `parent_grade_scale_id` int DEFAULT NULL,
  `school_id` int DEFAULT NULL,
  `teacher_id` int DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `grading_scale` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`grade_scale_id`),
  KEY `idx_ps_grade_scale_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `school_profile` (
  `id` bigint NOT NULL,
  `email_address` varchar(255) DEFAULT NULL,
  `mailing_address_line1` varchar(255) NOT NULL,
  `mailing_address_line2` varchar(255) DEFAULT NULL,
  `mailing_city` varchar(255) NOT NULL,
  `mailing_postal_code` varchar(255) NOT NULL,
  `mailing_state` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `school_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `school_project_type` (
  `active` bit(1) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK749enx8x8u91stp7visuw0nwg` (`code`),
  UNIQUE KEY `UKgc7t7emfn7xpqrii68n3if5u7` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `school_year` (
  `end_date` date NOT NULL,
  `first_day_of_classes` date NOT NULL,
  `last_day_of_classes` date NOT NULL,
  `start_date` date NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `label` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKfykll99b74avrgy4eqkrwalx7` (`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `section` (
  `campus_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_code` varchar(255) NOT NULL,
  `course_title` varchar(255) NOT NULL,
  `teacher_name` varchar(255) NOT NULL,
  `term_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlp5y2iy99w1ner2rl567ekuv5` (`campus_id`),
  CONSTRAINT `FKlp5y2iy99w1ner2rl567ekuv5` FOREIGN KEY (`campus_id`) REFERENCES `campus` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `section_teacher_local` (
  `end_date` date DEFAULT NULL,
  `priority_order` int DEFAULT NULL,
  `section_id` int DEFAULT NULL,
  `section_teacher_id` int NOT NULL,
  `start_date` date DEFAULT NULL,
  `teacher_id` int DEFAULT NULL,
  PRIMARY KEY (`section_teacher_id`),
  KEY `idx_section_teacher_section` (`section_id`),
  KEY `idx_section_teacher_teacher` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `sections_local` (
  `dcid` int DEFAULT NULL,
  `grade_scale_id` int DEFAULT NULL,
  `school_id` int DEFAULT NULL,
  `section_id` int NOT NULL,
  `teacher_id` int DEFAULT NULL,
  `term_id` int DEFAULT NULL,
  `course_number` varchar(255) DEFAULT NULL,
  `grade_level` varchar(255) DEFAULT NULL,
  `teacher_descr` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`section_id`),
  KEY `idx_sections_course_number` (`course_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `stored_grades_local` (
  `date_stored` date DEFAULT NULL,
  `dcid` int DEFAULT NULL,
  `earned_credit` decimal(8,2) DEFAULT NULL,
  `exclude_from_gpa` bit(1) DEFAULT NULL,
  `exclude_from_honor_roll` bit(1) DEFAULT NULL,
  `gpa_points` decimal(8,2) DEFAULT NULL,
  `grade_level` int DEFAULT NULL,
  `percent` decimal(6,2) DEFAULT NULL,
  `school_id` int DEFAULT NULL,
  `section_id` int DEFAULT NULL,
  `student_id` int DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_department` varchar(255) DEFAULT NULL,
  `course_name` varchar(255) DEFAULT NULL,
  `grade_scale_name` varchar(255) DEFAULT NULL,
  `letter_grade` varchar(255) DEFAULT NULL,
  `school_name` varchar(255) DEFAULT NULL,
  `store_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1l3quaih20l4riscn5iyr77r4` (`dcid`),
  KEY `idx_stored_grades_student_term` (`student_id`,`store_code`),
  KEY `idx_stored_grades_date` (`date_stored`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `student` (
  `date_of_birth` date DEFAULT NULL,
  `campus_id` bigint NOT NULL,
  `family_account_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `preferred_name` varchar(255) DEFAULT NULL,
  `student_number` varchar(255) NOT NULL,
  `suffix` varchar(255) DEFAULT NULL,
  `grade_level` enum('GRADE_1','GRADE_10','GRADE_11','GRADE_12','GRADE_2','GRADE_3','GRADE_4','GRADE_5','GRADE_6','GRADE_7','GRADE_8','GRADE_9','K4','K5') NOT NULL,
  `status` enum('ACTIVE','GRADUATED','INACTIVE') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7lsgf36yficx2udar96or4fn6` (`campus_id`),
  KEY `FK1b1hftkifanv81shnskwhnhdl` (`family_account_id`),
  CONSTRAINT `FK1b1hftkifanv81shnskwhnhdl` FOREIGN KEY (`family_account_id`) REFERENCES `family_account` (`id`),
  CONSTRAINT `FK7lsgf36yficx2udar96or4fn6` FOREIGN KEY (`campus_id`) REFERENCES `campus` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `student_contact_assoc_local` (
  `contact_priority_order` int DEFAULT NULL,
  `person_id` int DEFAULT NULL,
  `relationship_type_code_set_id` int DEFAULT NULL,
  `student_contact_assoc_id` int NOT NULL,
  `student_dcid` int DEFAULT NULL,
  PRIMARY KEY (`student_contact_assoc_id`),
  KEY `idx_student_contact_assoc_student` (`student_dcid`),
  KEY `idx_student_contact_assoc_person` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `student_fee` (
  `amount` decimal(12,2) NOT NULL,
  `installment_count` int DEFAULT NULL,
  `installment_number` int DEFAULT NULL,
  `assessed_at` datetime(6) NOT NULL,
  `campus_id` bigint NOT NULL,
  `cancelled_at` datetime(6) DEFAULT NULL,
  `enrollment_request_id` bigint DEFAULT NULL,
  `family_account_id` bigint NOT NULL,
  `fee_type_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint DEFAULT NULL,
  `cancellation_reason` varchar(255) DEFAULT NULL,
  `cancelled_by_user_id` varchar(255) DEFAULT NULL,
  `description` varchar(255) NOT NULL,
  `school_year` varchar(255) NOT NULL,
  `status` enum('ACTIVE','CANCELLED') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh9w2ckunpc2hs5c6dghs82dyo` (`campus_id`),
  KEY `FKgq90n36yfwf7lkqad63hgwm6v` (`enrollment_request_id`),
  KEY `FKlfwe3lew7020e69arikm9fhol` (`family_account_id`),
  KEY `FKewjjsdbompdwi1k774omh5gmc` (`fee_type_id`),
  KEY `FKesudo1i3xsjulovy4enioa0kc` (`student_id`),
  CONSTRAINT `FKesudo1i3xsjulovy4enioa0kc` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`),
  CONSTRAINT `FKewjjsdbompdwi1k774omh5gmc` FOREIGN KEY (`fee_type_id`) REFERENCES `fee_type` (`id`),
  CONSTRAINT `FKgq90n36yfwf7lkqad63hgwm6v` FOREIGN KEY (`enrollment_request_id`) REFERENCES `enrollment_request` (`id`),
  CONSTRAINT `FKh9w2ckunpc2hs5c6dghs82dyo` FOREIGN KEY (`campus_id`) REFERENCES `campus` (`id`),
  CONSTRAINT `FKlfwe3lew7020e69arikm9fhol` FOREIGN KEY (`family_account_id`) REFERENCES `family_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `student_name_override` (
  `student_id` int NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `override_id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `legal_first_name` varchar(255) NOT NULL,
  `legal_last_name` varchar(255) NOT NULL,
  `legal_middle_name` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`override_id`),
  UNIQUE KEY `uq_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `students_local` (
  `class_of` int DEFAULT NULL,
  `cumulative_gpa` decimal(6,2) DEFAULT NULL,
  `dcid` int NOT NULL,
  `enroll_status` int DEFAULT NULL,
  `entry_date` date DEFAULT NULL,
  `exclude_from_rank` bit(1) DEFAULT NULL,
  `exit_date` date DEFAULT NULL,
  `grade_level` int DEFAULT NULL,
  `school_id` int DEFAULT NULL,
  `student_id` int NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`student_id`),
  UNIQUE KEY `UKpr5k73orp82kttb6p6boas8se` (`dcid`),
  KEY `idx_students_local_grade` (`grade_level`),
  KEY `idx_students_local_enroll_status` (`enroll_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `teacher_course_stage` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_name` varchar(255) NOT NULL,
  `course_number` varchar(255) NOT NULL,
  `expression` varchar(255) DEFAULT NULL,
  `grade_level` varchar(255) DEFAULT NULL,
  `room` varchar(255) DEFAULT NULL,
  `school_year` varchar(255) NOT NULL,
  `section_number` varchar(255) DEFAULT NULL,
  `teacher_first_name` varchar(255) DEFAULT NULL,
  `teacher_last_name` varchar(255) DEFAULT NULL,
  `teacher_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_teacher_course_stage_course` (`course_name`),
  KEY `idx_teacher_course_stage_school_year` (`school_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

