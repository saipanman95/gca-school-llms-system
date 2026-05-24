package org.gca.schoolms.integration.powerschool;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PowerSchoolImportService {

    private static final int PREVIEW_ERROR_LIMIT = 10;
    private static final int BATCH_SIZE = 500;

    private static final Set<String> STUDENT_HEADERS = Set.of(
        "STUDENTS.ID",
        "STUDENTS.dcid",
        "STUDENTS.PSGUID",
        "STUDENTS.First_Name",
        "STUDENTS.Middle_Name",
        "STUDENTS.Last_Name",
        "STUDENTS.LastFirst",
        "STUDENTS.Grade_Level",
        "STUDENTS.ClassOf",
        "STUDENTS.SchoolID",
        "STUDENTS.Enroll_Status",
        "STUDENTS.EntryDate",
        "STUDENTS.ExitDate",
        "STUDENTS.Graduated_SchoolName",
        "STUDENTS.Cumulative_GPA",
        "STUDENTS.Cumulative_Pct",
        "STUDENTS.Simple_GPA",
        "STUDENTS.Exclude_fr_rank",
        "STUDENTS.State_ExcludeFromReporting"
    );

    private static final Set<String> STORED_GRADE_HEADERS = Set.of(
        "STOREDGRADES.dcid",
        "STOREDGRADES.StudentID",
        "STOREDGRADES.Grade_Level",
        "STOREDGRADES.Course_Name",
        "STOREDGRADES.Course_Number",
        "STOREDGRADES.SectionID",
        "STOREDGRADES.StoreCode",
        "STOREDGRADES.Percent",
        "STOREDGRADES.Grade",
        "STOREDGRADES.EarnedCrHrs",
        "STOREDGRADES.GPA_Points",
        "STOREDGRADES.GradeScale_Name",
        "STOREDGRADES.ExcludeFromGPA",
        "STOREDGRADES.ExcludeFromHonorRoll",
        "STOREDGRADES.SchoolID",
        "STOREDGRADES.SchoolName",
        "STOREDGRADES.DateStored"
    );

    private static final Set<String> PERSON_HEADERS = Set.of(
        "PERSON.ID",
        "PERSON.dcid",
        "PERSON.FIRSTNAME",
        "PERSON.LASTNAME",
        "PERSON.MIDDLENAME",
        "PERSON.ISACTIVE"
    );

    private static final Set<String> PERSON_ADDRESS_HEADERS = Set.of(
        "PERSONADDRESS.PersonAddressID",
        "PERSONADDRESS.STREET",
        "PERSONADDRESS.LINETWO",
        "PERSONADDRESS.CITY",
        "PERSONADDRESS.POSTALCODE",
        "PERSONADDRESS.UNIT",
        "PERSONADDRESS.STATESCODESETID"
    );

    private static final Set<String> PERSON_ADDRESS_ASSOC_HEADERS = Set.of(
        "PERSONADDRESSASSOC.PersonAddressAssocID",
        "PERSONADDRESSASSOC.PERSONADDRESSID",
        "PERSONADDRESSASSOC.PERSONID",
        "PERSONADDRESSASSOC.ADDRESSPRIORITYORDER",
        "PERSONADDRESSASSOC.ADDRESSTYPECODESETID",
        "PERSONADDRESSASSOC.STARTDATE",
        "PERSONADDRESSASSOC.ENDDATE"
    );

    private static final Set<String> EMAIL_ADDRESS_HEADERS = Set.of(
        "EMAILADDRESS.EmailAddress",
        "EMAILADDRESS.EmailAddressID"
    );

    private static final Set<String> PHONE_NUMBER_HEADERS = Set.of(
        "PHONENUMBER.ISSMS",
        "PHONENUMBER.PhoneNumber",
        "PHONENUMBER.PhoneNumberEXT",
        "PHONENUMBER.PhoneNumberID"
    );

    private static final Set<String> PERSON_PHONE_NUMBER_ASSOC_HEADERS = Set.of(
        "PERSONPHONENUMBERASSOC.ISPREFERRED",
        "PERSONPHONENUMBERASSOC.PERSONID",
        "PERSONPHONENUMBERASSOC.PersonPhoneNumberAssocID",
        "PERSONPHONENUMBERASSOC.PHONENUMBERASENTERED",
        "PERSONPHONENUMBERASSOC.PHONENUMBERID",
        "PERSONPHONENUMBERASSOC.PHONENUMBERPRIORITYORDER",
        "PERSONPHONENUMBERASSOC.PHONETYPECODESETID"
    );

    private static final Set<String> PERSON_EMAIL_ADDRESS_ASSOC_HEADERS = Set.of(
        "PERSONEMAILADDRESSASSOC.EMAILADDRESSID",
        "PERSONEMAILADDRESSASSOC.EMAILADDRESSPRIORITYORDER",
        "PERSONEMAILADDRESSASSOC.EMAILTYPECODESETID",
        "PERSONEMAILADDRESSASSOC.ISPRIMARYEMAILADDRESS",
        "PERSONEMAILADDRESSASSOC.PersonEmailAddressAssocID",
        "PERSONEMAILADDRESSASSOC.PERSONID"
    );

    private static final Set<String> STUDENT_CONTACT_ASSOC_HEADERS = Set.of(
        "STUDENTCONTACTASSOC.StudentContactAssocID",
        "STUDENTCONTACTASSOC.STUDENTDCID",
        "STUDENTCONTACTASSOC.PERSONID",
        "STUDENTCONTACTASSOC.CONTACTPRIORITYORDER",
        "STUDENTCONTACTASSOC.CURRRELTYPECODESETID"
    );

    private static final Set<String> STUDENT_CONTACT_DETAIL_HEADERS = Set.of(
        "STUDENTCONTACTDETAIL.CONFIDENTIALCOMMFLAG",
        "STUDENTCONTACTDETAIL.ENDDATE",
        "STUDENTCONTACTDETAIL.GENERALCOMMFLAG",
        "STUDENTCONTACTDETAIL.ISACTIVE",
        "STUDENTCONTACTDETAIL.ISCUSTODIAL",
        "STUDENTCONTACTDETAIL.ISEMERGENCY",
        "STUDENTCONTACTDETAIL.LIVESWITHFLG",
        "STUDENTCONTACTDETAIL.RECEIVESMAILFLG",
        "STUDENTCONTACTDETAIL.RELATIONSHIPNOTE",
        "STUDENTCONTACTDETAIL.RELATIONSHIPTYPECODESETID",
        "STUDENTCONTACTDETAIL.SCHOOLPICKUPFLG",
        "STUDENTCONTACTDETAIL.STARTDATE",
        "STUDENTCONTACTDETAIL.STUDENTCONTACTASSOCID",
        "STUDENTCONTACTDETAIL.StudentContactDetailID",
        "STUDENTCONTACTDETAILCOREFIELDS.iscaregiver",
        "STUDENTCONTACTDETAILCOREFIELDS.legalguardian",
        "STUDENTCONTACTDETAILCOREFIELDS.physicaladdresssource",
        "STUDENTCONTACTDETAILCOREFIELDS.primarycontact",
        "STUDENTCONTACTDETAILCOREFIELDS.samehomephonenumber",
        "STUDENTCONTACTDETAILCOREFIELDS.samemailingaddress",
        "STUDENTCONTACTDETAILCOREFIELDS.source"
    );

    private static final Set<String> PG_FINAL_GRADES_HEADERS = Set.of(
        "PGFINALGRADES.ID",
        "PGFINALGRADES.dcid",
        "PGFINALGRADES.Citizenship",
        "PGFINALGRADES.FinalGradeName",
        "PGFINALGRADES.Grade",
        "PGFINALGRADES.Percent",
        "PGFINALGRADES.SectionID",
        "PGFINALGRADES.StartDate",
        "PGFINALGRADES.EndDate",
        "PGFINALGRADES.StudentID"
    );

    private static final Set<String> SECTIONS_HEADERS = Set.of(
        "SECTIONS.ID",
        "SECTIONS.dcid",
        "SECTIONS.Course_Number",
        "SECTIONS.Grade_Level",
        "SECTIONS.GradeScaleID",
        "SECTIONS.Teacher",
        "SECTIONS.TeacherDescr",
        "SECTIONS.SchoolID",
        "SECTIONS.TermID"
    );

    private static final Set<String> SECTION_TEACHER_HEADERS = Set.of(
        "SECTIONTEACHER.ID",
        "SECTIONTEACHER.SECTIONID",
        "SECTIONTEACHER.TeacherID",
        "SECTIONTEACHER.PRIORITYORDER",
        "SECTIONTEACHER.START_DATE",
        "SECTIONTEACHER.END_DATE"
    );

    private static final Set<String> TEACHERS_HEADERS = Set.of(
        "TEACHERS.ID",
        "TEACHERS.dcid",
        "TEACHERS.First_Name",
        "TEACHERS.Middle_Name",
        "TEACHERS.Last_Name",
        "TEACHERS.LastFirst",
        "TEACHERS.Email_Addr",
        "TEACHERS.LoginID",
        "TEACHERS.TeacherLoginID",
        "TEACHERS.TeacherNumber",
        "TEACHERS.Title",
        "TEACHERS.SchoolID",
        "TEACHERS.StaffStatus",
        "TEACHERS.Status"
    );

    private static final Set<String> SCHOOL_STAFF_HEADERS = Set.of(
        "SCHOOLSTAFF.ID",
        "SCHOOLSTAFF.dcid",
        "SCHOOLSTAFF.SchoolID",
        "SCHOOLSTAFF.StaffStatus",
        "SCHOOLSTAFF.Status",
        "SCHOOLSTAFF.TRANSACTION_DATE",
        "SCHOOLSTAFF.Users_DCID"
    );

    private static final Set<String> PSM_TEACHER_HEADERS = Set.of(
        "PSM_TEACHER.ID",
        "PSM_TEACHER.EMAIL",
        "PSM_TEACHER.ETHNICITY",
        "PSM_TEACHER.FIRSTNAME",
        "PSM_TEACHER.ISACCESSIBLE",
        "PSM_TEACHER.LASTNAME",
        "PSM_TEACHER.LDAPENABLED",
        "PSM_TEACHER.TEACHERIDENTIFIER",
        "PSM_TEACHER.USERNAME"
    );

    private static final Set<String> PSM_SECTION_HEADERS = Set.of(
        "PSM_SECTION.ID",
        "PSM_SECTION.DESCRIPTION",
        "PSM_SECTION.GRADEBOOKTYPE",
        "PSM_SECTION.MEETING",
        "PSM_SECTION.ROOMNAME",
        "PSM_SECTION.SCHOOLCOURSEID",
        "PSM_SECTION.SCHOOLID",
        "PSM_SECTION.SECTIONIDENTIFIER",
        "PSM_SECTION.TERMID"
    );

    private static final Set<String> PSM_SECTION_TEACHER_HEADERS = Set.of(
        "PSM_SECTIONTEACHER.ID",
        "PSM_SECTIONTEACHER.ALLOCATION",
        "PSM_SECTIONTEACHER.END_DATE",
        "PSM_SECTIONTEACHER.PRIORITYORDER",
        "PSM_SECTIONTEACHER.ROLEID",
        "PSM_SECTIONTEACHER.SECTIONID",
        "PSM_SECTIONTEACHER.SECTIONNICKNAME",
        "PSM_SECTIONTEACHER.START_DATE",
        "PSM_SECTIONTEACHER.TEACHERID"
    );

    private static final Set<String> PSM_SCHOOL_COURSE_HEADERS = Set.of(
        "PSM_SCHOOLCOURSE.ID",
        "PSM_SCHOOLCOURSE.ABBREVIATION",
        "PSM_SCHOOLCOURSE.COURSECODE",
        "PSM_SCHOOLCOURSE.COURSEID",
        "PSM_SCHOOLCOURSE.DESCRIPTION",
        "PSM_SCHOOLCOURSE.SCHOOLCOURSETITLE",
        "PSM_SCHOOLCOURSE.SCHOOLID"
    );

    private static final Set<String> GUARDIAN_HEADERS = Set.of(
        "GUARDIAN.ACCOUNTIDENTIFIER",
        "GUARDIAN.EMAIL",
        "GUARDIAN.FIRSTNAME",
        "GUARDIAN.GUARDIANID",
        "GUARDIAN.LASTNAME",
        "GUARDIAN.MIDDLENAME",
        "GUARDIAN.PSGUID",
        "GUARDIAN.STATE_GuardianNUMBER"
    );

    private static final Set<String> GUARDIAN_STUDENT_HEADERS = Set.of(
        "GUARDIANSTUDENT.AUTOSEND_ATTENDANCEDETAIL",
        "GUARDIANSTUDENT.AUTOSEND_BALANCEALERT",
        "GUARDIANSTUDENT.AUTOSEND_GRADEDETAIL",
        "GUARDIANSTUDENT.AUTOSEND_HOWOFTEN",
        "GUARDIANSTUDENT.AUTOSEND_SCHOOLANNOUNCEMENTS",
        "GUARDIANSTUDENT.AUTOSEND_SUMMARY",
        "GUARDIANSTUDENT.GUARDIANID",
        "GUARDIANSTUDENT.GUARDIANRELATIONSHIPTYPEID",
        "GUARDIANSTUDENT.GuardianStudentID",
        "GUARDIANSTUDENT.STUDENTSDCID"
    );

    private static final Set<String> GUARDIAN_RELATIONSHIP_TYPE_HEADERS = Set.of(
        "GUARDIANRELATIONSHIPTYPE.DISPLAYORDER",
        "GUARDIANRELATIONSHIPTYPE.GuardianRelationshipTypeID",
        "GUARDIANRELATIONSHIPTYPE.SIFRELATIONTOSTUDENT"
    );

    private static final Set<String> GUARDIAN_PERSON_ASSOC_HEADERS = Set.of(
        "GUARDIANPERSONASSOC.GUARDIANID",
        "GUARDIANPERSONASSOC.GuardianPersonAssocID",
        "GUARDIANPERSONASSOC.PERSONID"
    );

    private static final Set<String> PSM_STUDENT_CONTACT_HEADERS = Set.of(
        "PSM_STUDENTCONTACT.ID",
        "PSM_STUDENTCONTACT.EMAIL",
        "PSM_STUDENTCONTACT.FIRSTNAME",
        "PSM_STUDENTCONTACT.LASTNAME",
        "PSM_STUDENTCONTACT.PHONE",
        "PSM_STUDENTCONTACT.STUDENTCONTACTTYPEID",
        "PSM_STUDENTCONTACT.STUDENTID"
    );

    private static final Set<String> PSM_STUDENT_CONTACT_TYPE_HEADERS = Set.of(
        "PSM_STUDENTCONTACTTYPE.ID",
        "PSM_STUDENTCONTACTTYPE.DESCRIPTION",
        "PSM_STUDENTCONTACTTYPE.NAME"
    );

    private static final Set<String> PSM_GRADE_SCALE_HEADERS = Set.of(
        "PSM_GRADESCALE.ID",
        "PSM_GRADESCALE.CANMODIFY",
        "PSM_GRADESCALE.CONTENTGROUPID",
        "PSM_GRADESCALE.DESCRIPTION",
        "PSM_GRADESCALE.GRADINGSCALE",
        "PSM_GRADESCALE.ISDISTRICTDEFAULT",
        "PSM_GRADESCALE.ISNUMERIC",
        "PSM_GRADESCALE.NAME",
        "PSM_GRADESCALE.NUMERICMAX",
        "PSM_GRADESCALE.NUMERICMIN",
        "PSM_GRADESCALE.NUMERICPRECISION",
        "PSM_GRADESCALE.NUMERICSCALE",
        "PSM_GRADESCALE.PARENTGRADESCALEID",
        "PSM_GRADESCALE.SCHOOLID",
        "PSM_GRADESCALE.TEACHERID"
    );

    private static final Set<String> PSM_GRADE_HEADERS = Set.of(
        "PSM_GRADE.ID",
        "PSM_GRADE.ADMINUSEONLY",
        "PSM_GRADE.CUTOFFPERCENT",
        "PSM_GRADE.DEFAULTZEROCUTOFF",
        "PSM_GRADE.DESCRIPTION",
        "PSM_GRADE.GRADELABEL",
        "PSM_GRADE.GRADESCALEID",
        "PSM_GRADE.ISSPECIAL",
        "PSM_GRADE.PERCENTVALUE",
        "PSM_GRADE.POINTSVALUE",
        "PSM_GRADE.SORTORDER"
    );

    private static final String STUDENT_IMPORT_SQL = """
        INSERT INTO students_local (
            student_id,
            dcid,
            first_name,
            middle_name,
            last_name,
            grade_level,
            class_of,
            school_id,
            enroll_status,
            entry_date,
            exit_date,
            cumulative_gpa,
            exclude_from_rank,
            mailing_street,
            mailing_city,
            mailing_state,
            mailing_zip,
            home_phone,
            guardian_email,
            father_name,
            mother_name,
            father_home_phone,
            father_day_phone,
            mother_home_phone,
            mother_day_phone,
            created_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
        ON DUPLICATE KEY UPDATE
            dcid = VALUES(dcid),
            first_name = VALUES(first_name),
            middle_name = VALUES(middle_name),
            last_name = VALUES(last_name),
            grade_level = VALUES(grade_level),
            class_of = VALUES(class_of),
            school_id = VALUES(school_id),
            enroll_status = VALUES(enroll_status),
            entry_date = VALUES(entry_date),
            exit_date = VALUES(exit_date),
            cumulative_gpa = VALUES(cumulative_gpa),
            exclude_from_rank = VALUES(exclude_from_rank),
            mailing_street = VALUES(mailing_street),
            mailing_city = VALUES(mailing_city),
            mailing_state = VALUES(mailing_state),
            mailing_zip = VALUES(mailing_zip),
            home_phone = VALUES(home_phone),
            guardian_email = VALUES(guardian_email),
            father_name = VALUES(father_name),
            mother_name = VALUES(mother_name),
            father_home_phone = VALUES(father_home_phone),
            father_day_phone = VALUES(father_day_phone),
            mother_home_phone = VALUES(mother_home_phone),
            mother_day_phone = VALUES(mother_day_phone)
        """;

    private static final String STORED_GRADE_IMPORT_SQL = """
        INSERT INTO stored_grades_local (
            dcid,
            student_id,
            grade_level,
            course_name,
            course_department,
            section_id,
            store_code,
            percent,
            letter_grade,
            earned_credit,
            gpa_points,
            grade_scale_name,
            exclude_from_gpa,
            exclude_from_honor_roll,
            school_id,
            school_name,
            date_stored
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            student_id = VALUES(student_id),
            grade_level = VALUES(grade_level),
            course_name = VALUES(course_name),
            course_department = VALUES(course_department),
            section_id = VALUES(section_id),
            store_code = VALUES(store_code),
            percent = VALUES(percent),
            letter_grade = VALUES(letter_grade),
            earned_credit = VALUES(earned_credit),
            gpa_points = VALUES(gpa_points),
            grade_scale_name = VALUES(grade_scale_name),
            exclude_from_gpa = VALUES(exclude_from_gpa),
            exclude_from_honor_roll = VALUES(exclude_from_honor_roll),
            school_id = VALUES(school_id),
            school_name = VALUES(school_name),
            date_stored = VALUES(date_stored)
        """;

    private static final String PERSON_IMPORT_SQL = """
        INSERT INTO person_local (
            person_id,
            dcid,
            first_name,
            middle_name,
            last_name,
            is_active
        ) VALUES (?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            dcid = VALUES(dcid),
            first_name = VALUES(first_name),
            middle_name = VALUES(middle_name),
            last_name = VALUES(last_name),
            is_active = VALUES(is_active)
        """;

    private static final String PERSON_ADDRESS_IMPORT_SQL = """
        INSERT INTO person_address_local (
            person_address_id,
            street,
            line_two,
            city,
            postal_code,
            unit,
            state_code_set_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            street = VALUES(street),
            line_two = VALUES(line_two),
            city = VALUES(city),
            postal_code = VALUES(postal_code),
            unit = VALUES(unit),
            state_code_set_id = VALUES(state_code_set_id)
        """;

    private static final String PERSON_ADDRESS_ASSOC_IMPORT_SQL = """
        INSERT INTO person_address_assoc_local (
            person_address_assoc_id,
            person_address_id,
            person_id,
            address_priority_order,
            address_type_code_set_id,
            start_date,
            end_date
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            person_address_id = VALUES(person_address_id),
            person_id = VALUES(person_id),
            address_priority_order = VALUES(address_priority_order),
            address_type_code_set_id = VALUES(address_type_code_set_id),
            start_date = VALUES(start_date),
            end_date = VALUES(end_date)
        """;

    private static final String EMAIL_ADDRESS_IMPORT_SQL = """
        INSERT INTO email_address_local (
            email_address_id,
            email_address,
            when_created,
            when_modified,
            who_created,
            who_modified
        ) VALUES (?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            email_address = VALUES(email_address),
            when_created = VALUES(when_created),
            when_modified = VALUES(when_modified),
            who_created = VALUES(who_created),
            who_modified = VALUES(who_modified)
        """;

    private static final String PHONE_NUMBER_IMPORT_SQL = """
        INSERT INTO phone_number_local (
            phone_number_id,
            is_sms,
            phone_number,
            phone_number_ext,
            when_created,
            when_modified,
            who_created,
            who_modified,
            is_unlisted
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            is_sms = VALUES(is_sms),
            phone_number = VALUES(phone_number),
            phone_number_ext = VALUES(phone_number_ext),
            when_created = VALUES(when_created),
            when_modified = VALUES(when_modified),
            who_created = VALUES(who_created),
            who_modified = VALUES(who_modified),
            is_unlisted = VALUES(is_unlisted)
        """;

    private static final String PERSON_PHONE_NUMBER_ASSOC_IMPORT_SQL = """
        INSERT INTO person_phone_number_assoc_local (
            person_phone_number_assoc_id,
            person_id,
            phone_number_id,
            phone_number_priority_order,
            phone_type_code_set_id,
            is_preferred,
            phone_number_as_entered,
            when_created,
            when_modified,
            who_created,
            who_modified
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            person_id = VALUES(person_id),
            phone_number_id = VALUES(phone_number_id),
            phone_number_priority_order = VALUES(phone_number_priority_order),
            phone_type_code_set_id = VALUES(phone_type_code_set_id),
            is_preferred = VALUES(is_preferred),
            phone_number_as_entered = VALUES(phone_number_as_entered),
            when_created = VALUES(when_created),
            when_modified = VALUES(when_modified),
            who_created = VALUES(who_created),
            who_modified = VALUES(who_modified)
        """;

    private static final String PERSON_EMAIL_ADDRESS_ASSOC_IMPORT_SQL = """
        INSERT INTO person_email_address_assoc_local (
            person_email_address_assoc_id,
            email_address_id,
            email_address_priority_order,
            email_type_code_set_id,
            is_primary_email_address,
            person_id,
            when_created,
            when_modified,
            who_created,
            who_modified
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            email_address_id = VALUES(email_address_id),
            email_address_priority_order = VALUES(email_address_priority_order),
            email_type_code_set_id = VALUES(email_type_code_set_id),
            is_primary_email_address = VALUES(is_primary_email_address),
            person_id = VALUES(person_id),
            when_created = VALUES(when_created),
            when_modified = VALUES(when_modified),
            who_created = VALUES(who_created),
            who_modified = VALUES(who_modified)
        """;

    private static final String STUDENT_CONTACT_ASSOC_IMPORT_SQL = """
        INSERT INTO student_contact_assoc_local (
            student_contact_assoc_id,
            student_dcid,
            person_id,
            contact_priority_order,
            relationship_type_code_set_id
        ) VALUES (?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            student_dcid = VALUES(student_dcid),
            person_id = VALUES(person_id),
            contact_priority_order = VALUES(contact_priority_order),
            relationship_type_code_set_id = VALUES(relationship_type_code_set_id)
        """;

    private static final String STUDENT_CONTACT_DETAIL_IMPORT_SQL = """
        INSERT INTO student_contact_detail_local (
            student_contact_detail_id,
            student_contact_assoc_id,
            relationship_type_code_set_id,
            confidential_comm_flag,
            general_comm_flag,
            is_active,
            is_custodial,
            is_emergency,
            lives_with_flag,
            receives_mail_flag,
            school_pickup_flag,
            start_date,
            end_date,
            relationship_note,
            is_caregiver,
            legal_guardian,
            physical_address_source,
            primary_contact,
            same_home_phone_number,
            same_mailing_address,
            source
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            student_contact_assoc_id = VALUES(student_contact_assoc_id),
            relationship_type_code_set_id = VALUES(relationship_type_code_set_id),
            confidential_comm_flag = VALUES(confidential_comm_flag),
            general_comm_flag = VALUES(general_comm_flag),
            is_active = VALUES(is_active),
            is_custodial = VALUES(is_custodial),
            is_emergency = VALUES(is_emergency),
            lives_with_flag = VALUES(lives_with_flag),
            receives_mail_flag = VALUES(receives_mail_flag),
            school_pickup_flag = VALUES(school_pickup_flag),
            start_date = VALUES(start_date),
            end_date = VALUES(end_date),
            relationship_note = VALUES(relationship_note),
            is_caregiver = VALUES(is_caregiver),
            legal_guardian = VALUES(legal_guardian),
            physical_address_source = VALUES(physical_address_source),
            primary_contact = VALUES(primary_contact),
            same_home_phone_number = VALUES(same_home_phone_number),
            same_mailing_address = VALUES(same_mailing_address),
            source = VALUES(source)
        """;

    private static final String PG_FINAL_GRADES_IMPORT_SQL = """
        INSERT INTO pg_final_grades_local (
            pg_final_grade_id,
            dcid,
            citizenship,
            final_grade_name,
            grade_value,
            percent,
            section_id,
            start_date,
            end_date,
            student_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            dcid = VALUES(dcid),
            citizenship = VALUES(citizenship),
            final_grade_name = VALUES(final_grade_name),
            grade_value = VALUES(grade_value),
            percent = VALUES(percent),
            section_id = VALUES(section_id),
            start_date = VALUES(start_date),
            end_date = VALUES(end_date),
            student_id = VALUES(student_id)
        """;

    private static final String SECTIONS_IMPORT_SQL = """
        INSERT INTO sections_local (
            section_id,
            dcid,
            course_number,
            grade_level,
            grade_scale_id,
            teacher_id,
            teacher_descr,
            school_id,
            term_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            dcid = VALUES(dcid),
            course_number = VALUES(course_number),
            grade_level = VALUES(grade_level),
            grade_scale_id = VALUES(grade_scale_id),
            teacher_id = VALUES(teacher_id),
            teacher_descr = VALUES(teacher_descr),
            school_id = VALUES(school_id),
            term_id = VALUES(term_id)
        """;

    private static final String SECTION_TEACHER_IMPORT_SQL = """
        INSERT INTO section_teacher_local (
            section_teacher_id,
            section_id,
            teacher_id,
            priority_order,
            start_date,
            end_date
        ) VALUES (?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            section_id = VALUES(section_id),
            teacher_id = VALUES(teacher_id),
            priority_order = VALUES(priority_order),
            start_date = VALUES(start_date),
            end_date = VALUES(end_date)
        """;

    private static final String TEACHERS_IMPORT_SQL = """
        INSERT INTO teacher_local (
            teacher_id,
            dcid,
            first_name,
            middle_name,
            last_name,
            last_first,
            email_addr,
            login_id,
            teacher_login_id,
            teacher_number,
            title,
            school_id,
            staff_status,
            status
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            dcid = VALUES(dcid),
            first_name = VALUES(first_name),
            middle_name = VALUES(middle_name),
            last_name = VALUES(last_name),
            last_first = VALUES(last_first),
            email_addr = VALUES(email_addr),
            login_id = VALUES(login_id),
            teacher_login_id = VALUES(teacher_login_id),
            teacher_number = VALUES(teacher_number),
            title = VALUES(title),
            school_id = VALUES(school_id),
            staff_status = VALUES(staff_status),
            status = VALUES(status)
        """;

    private static final String SCHOOL_STAFF_IMPORT_SQL = """
        INSERT INTO school_staff_local (
            school_staff_id,
            dcid,
            school_id,
            staff_status,
            status,
            transaction_date,
            users_dcid
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            dcid = VALUES(dcid),
            school_id = VALUES(school_id),
            staff_status = VALUES(staff_status),
            status = VALUES(status),
            transaction_date = VALUES(transaction_date),
            users_dcid = VALUES(users_dcid)
        """;

    private static final String PSM_TEACHER_IMPORT_SQL = """
        INSERT INTO psm_teacher_local (
            psm_teacher_id,
            email,
            ethnicity,
            first_name,
            is_accessible,
            last_name,
            ldap_enabled,
            teacher_identifier,
            username
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            email = VALUES(email),
            ethnicity = VALUES(ethnicity),
            first_name = VALUES(first_name),
            is_accessible = VALUES(is_accessible),
            last_name = VALUES(last_name),
            ldap_enabled = VALUES(ldap_enabled),
            teacher_identifier = VALUES(teacher_identifier),
            username = VALUES(username)
        """;

    private static final String PSM_SECTION_IMPORT_SQL = """
        INSERT INTO psm_section_local (
            psm_section_id,
            description,
            gradebook_type,
            meeting,
            room_name,
            school_course_id,
            school_id,
            section_identifier,
            term_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            description = VALUES(description),
            gradebook_type = VALUES(gradebook_type),
            meeting = VALUES(meeting),
            room_name = VALUES(room_name),
            school_course_id = VALUES(school_course_id),
            school_id = VALUES(school_id),
            section_identifier = VALUES(section_identifier),
            term_id = VALUES(term_id)
        """;

    private static final String PSM_SECTION_TEACHER_IMPORT_SQL = """
        INSERT INTO psm_section_teacher_local (
            psm_section_teacher_id,
            allocation,
            end_date,
            priority_order,
            role_id,
            section_id,
            section_nickname,
            start_date,
            teacher_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            allocation = VALUES(allocation),
            end_date = VALUES(end_date),
            priority_order = VALUES(priority_order),
            role_id = VALUES(role_id),
            section_id = VALUES(section_id),
            section_nickname = VALUES(section_nickname),
            start_date = VALUES(start_date),
            teacher_id = VALUES(teacher_id)
        """;

    private static final String PSM_SCHOOL_COURSE_IMPORT_SQL = """
        INSERT INTO psm_school_course_local (
            psm_school_course_id,
            abbreviation,
            course_code,
            course_id,
            description,
            school_course_title,
            school_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            abbreviation = VALUES(abbreviation),
            course_code = VALUES(course_code),
            course_id = VALUES(course_id),
            description = VALUES(description),
            school_course_title = VALUES(school_course_title),
            school_id = VALUES(school_id)
        """;

    private static final String GUARDIAN_IMPORT_SQL = """
        INSERT INTO guardian_local (
            guardian_id,
            account_identifier,
            email,
            first_name,
            last_name,
            middle_name,
            psguid,
            state_guardian_number
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            account_identifier = VALUES(account_identifier),
            email = VALUES(email),
            first_name = VALUES(first_name),
            last_name = VALUES(last_name),
            middle_name = VALUES(middle_name),
            psguid = VALUES(psguid),
            state_guardian_number = VALUES(state_guardian_number)
        """;

    private static final String GUARDIAN_STUDENT_IMPORT_SQL = """
        INSERT INTO guardian_student_local (
            guardian_student_id,
            guardian_id,
            guardian_relationship_type_id,
            student_dcid,
            auto_send_attendance_detail,
            auto_send_balance_alert,
            auto_send_grade_detail,
            auto_send_how_often,
            auto_send_school_announcements,
            auto_send_summary
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            guardian_id = VALUES(guardian_id),
            guardian_relationship_type_id = VALUES(guardian_relationship_type_id),
            student_dcid = VALUES(student_dcid),
            auto_send_attendance_detail = VALUES(auto_send_attendance_detail),
            auto_send_balance_alert = VALUES(auto_send_balance_alert),
            auto_send_grade_detail = VALUES(auto_send_grade_detail),
            auto_send_how_often = VALUES(auto_send_how_often),
            auto_send_school_announcements = VALUES(auto_send_school_announcements),
            auto_send_summary = VALUES(auto_send_summary)
        """;

    private static final String GUARDIAN_RELATIONSHIP_TYPE_IMPORT_SQL = """
        INSERT INTO guardian_relationship_type_local (
            guardian_relationship_type_id,
            display_order,
            sif_relation_to_student
        ) VALUES (?, ?, ?)
        ON DUPLICATE KEY UPDATE
            display_order = VALUES(display_order),
            sif_relation_to_student = VALUES(sif_relation_to_student)
        """;

    private static final String GUARDIAN_PERSON_ASSOC_IMPORT_SQL = """
        INSERT INTO guardian_person_assoc_local (
            guardian_person_assoc_id,
            guardian_id,
            person_id
        ) VALUES (?, ?, ?)
        ON DUPLICATE KEY UPDATE
            guardian_id = VALUES(guardian_id),
            person_id = VALUES(person_id)
        """;

    private static final String PSM_STUDENT_CONTACT_IMPORT_SQL = """
        INSERT INTO psm_student_contact_local (
            psm_student_contact_id,
            email,
            first_name,
            last_name,
            phone,
            student_contact_type_id,
            student_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            email = VALUES(email),
            first_name = VALUES(first_name),
            last_name = VALUES(last_name),
            phone = VALUES(phone),
            student_contact_type_id = VALUES(student_contact_type_id),
            student_id = VALUES(student_id)
        """;

    private static final String PSM_STUDENT_CONTACT_TYPE_IMPORT_SQL = """
        INSERT INTO psm_student_contact_type_local (
            psm_student_contact_type_id,
            description,
            name
        ) VALUES (?, ?, ?)
        ON DUPLICATE KEY UPDATE
            description = VALUES(description),
            name = VALUES(name)
        """;

    private static final String PSM_GRADE_SCALE_IMPORT_SQL = """
        INSERT INTO powerschool_grade_scale_local (
            grade_scale_id,
            can_modify,
            content_group_id,
            description,
            grading_scale,
            is_district_default,
            is_numeric,
            name,
            numeric_max,
            numeric_min,
            numeric_precision,
            numeric_scale,
            parent_grade_scale_id,
            school_id,
            teacher_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            can_modify = VALUES(can_modify),
            content_group_id = VALUES(content_group_id),
            description = VALUES(description),
            grading_scale = VALUES(grading_scale),
            is_district_default = VALUES(is_district_default),
            is_numeric = VALUES(is_numeric),
            name = VALUES(name),
            numeric_max = VALUES(numeric_max),
            numeric_min = VALUES(numeric_min),
            numeric_precision = VALUES(numeric_precision),
            numeric_scale = VALUES(numeric_scale),
            parent_grade_scale_id = VALUES(parent_grade_scale_id),
            school_id = VALUES(school_id),
            teacher_id = VALUES(teacher_id)
        """;

    private static final String PSM_GRADE_IMPORT_SQL = """
        INSERT INTO powerschool_grade_scale_item_local (
            grade_item_id,
            admin_use_only,
            cutoff_percent,
            default_zero_cutoff,
            description,
            grade_label,
            grade_scale_id,
            is_special,
            percent_value,
            points_value,
            sort_order
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            admin_use_only = VALUES(admin_use_only),
            cutoff_percent = VALUES(cutoff_percent),
            default_zero_cutoff = VALUES(default_zero_cutoff),
            description = VALUES(description),
            grade_label = VALUES(grade_label),
            grade_scale_id = VALUES(grade_scale_id),
            is_special = VALUES(is_special),
            percent_value = VALUES(percent_value),
            points_value = VALUES(points_value),
            sort_order = VALUES(sort_order)
        """;

    private final JdbcTemplate jdbcTemplate;

    public PowerSchoolImportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PowerSchoolImportReport validateFile(Path path) throws IOException {
        return parse(path.getFileName().toString(), Files.readAllBytes(path), false);
    }

    public PowerSchoolImportReport validateBytes(String originalFilename, byte[] bytes) {
        return parse(originalFilename, bytes, false);
    }

    @Transactional
    public PowerSchoolImportReport importFile(Path path) throws IOException {
        return parse(path.getFileName().toString(), Files.readAllBytes(path), true);
    }

    @Transactional
    public PowerSchoolImportReport importBytes(String originalFilename, byte[] bytes) {
        return parse(originalFilename, bytes, true);
    }

    private PowerSchoolImportReport parse(String originalFilename, byte[] bytes, boolean importRows) {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                return new PowerSchoolImportReport(false, safeFilename(originalFilename), null, 0,
                    List.of("The uploaded file is empty."));
            }

            String[] headers = parseTsvRow(headerLine);
            Map<String, Integer> index = buildIndex(headers);
            PowerSchoolImportDatasetType datasetType = detectDatasetType(index.keySet());
            if (datasetType == null) {
                return new PowerSchoolImportReport(false, safeFilename(originalFilename), null, 0, List.of(
                    "The TSV header does not match a supported dataset.",
                    "Supported imports: Students, Stored Grades, Person, PersonAddress, PersonAddressAssoc, EmailAddress, PhoneNumber, PersonPhoneNumberAssoc, PersonEmailAddressAssoc, StudentContactAssoc, StudentContactDetail, PGFinalGrades, Sections, SectionTeacher, Teachers, SchoolStaff, Guardian, GuardianStudent, GuardianRelationshipType, GuardianPersonAssoc, PSM_Teacher, PSM_Section, PSM_SectionTeacher, PSM_StudentContact, PSM_StudentContactType, PSM_SchoolCourse, PSM_GradeScale, and PSM_GRADE."
                ));
            }

            List<String> errors = new ArrayList<>();
            List<Object[]> batch = importRows ? new ArrayList<>(BATCH_SIZE) : List.of();
            int rowsProcessed = 0;
            int lineNumber = 1;
            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] row = parseTsvRow(line);
                if (row.length != headers.length) {
                    addError(errors, "Line " + lineNumber + ": expected " + headers.length + " columns but found " + row.length + ".");
                    if (importRows) {
                        return new PowerSchoolImportReport(false, safeFilename(originalFilename), datasetType, rowsProcessed, errors);
                    }
                    if (errors.size() >= PREVIEW_ERROR_LIMIT) {
                        break;
                    }
                    continue;
                }

                try {
                    Object[] params = switch (datasetType) {
                        case STUDENTS -> mapStudentRow(row, index);
                        case STORED_GRADES -> mapStoredGradeRow(row, index);
                        case PERSON -> mapPersonRow(row, index);
                        case PERSON_ADDRESS -> mapPersonAddressRow(row, index);
                        case PERSON_ADDRESS_ASSOC -> mapPersonAddressAssocRow(row, index);
                        case EMAIL_ADDRESS -> mapEmailAddressRow(row, index);
                        case PHONE_NUMBER -> mapPhoneNumberRow(row, index);
                        case PERSON_PHONE_NUMBER_ASSOC -> mapPersonPhoneNumberAssocRow(row, index);
                        case PERSON_EMAIL_ADDRESS_ASSOC -> mapPersonEmailAddressAssocRow(row, index);
                        case STUDENT_CONTACT_ASSOC -> mapStudentContactAssocRow(row, index);
                        case STUDENT_CONTACT_DETAIL -> mapStudentContactDetailRow(row, index);
                        case PG_FINAL_GRADES -> mapPgFinalGradesRow(row, index);
                        case SECTIONS -> mapSectionsRow(row, index);
                        case SECTION_TEACHER -> mapSectionTeacherRow(row, index);
                        case TEACHERS -> mapTeachersRow(row, index);
                        case SCHOOL_STAFF -> mapSchoolStaffRow(row, index);
                        case PSM_TEACHER -> mapPsmTeacherRow(row, index);
                        case PSM_SECTION -> mapPsmSectionRow(row, index);
                        case PSM_SECTION_TEACHER -> mapPsmSectionTeacherRow(row, index);
                        case GUARDIAN -> mapGuardianRow(row, index);
                        case GUARDIAN_STUDENT -> mapGuardianStudentRow(row, index);
                        case GUARDIAN_RELATIONSHIP_TYPE -> mapGuardianRelationshipTypeRow(row, index);
                        case GUARDIAN_PERSON_ASSOC -> mapGuardianPersonAssocRow(row, index);
                        case PSM_STUDENT_CONTACT -> mapPsmStudentContactRow(row, index);
                        case PSM_STUDENT_CONTACT_TYPE -> mapPsmStudentContactTypeRow(row, index);
                        case PSM_SCHOOL_COURSE -> mapPsmSchoolCourseRow(row, index);
                        case PSM_GRADE_SCALE -> mapPowerSchoolGradeScaleRow(row, index);
                        case PSM_GRADE -> mapPowerSchoolGradeRow(row, index);
                    };
                    if (params == null) {
                        continue;
                    }
                    rowsProcessed++;
                    if (importRows) {
                        batch.add(params);
                        if (batch.size() == BATCH_SIZE) {
                            flushBatch(datasetType, batch);
                            batch.clear();
                        }
                    }
                } catch (IllegalArgumentException ex) {
                    addError(errors, "Line " + lineNumber + ": " + ex.getMessage());
                    if (importRows) {
                        return new PowerSchoolImportReport(false, safeFilename(originalFilename), datasetType, rowsProcessed, errors);
                    }
                    if (errors.size() >= PREVIEW_ERROR_LIMIT) {
                        break;
                    }
                }
            }

            if (errors.isEmpty() && importRows && !batch.isEmpty()) {
                flushBatch(datasetType, batch);
            }

            if (!errors.isEmpty()) {
                return new PowerSchoolImportReport(false, safeFilename(originalFilename), datasetType, rowsProcessed, errors);
            }

            return new PowerSchoolImportReport(true, safeFilename(originalFilename), datasetType, rowsProcessed, List.of(
                "Recognized dataset: " + datasetType.getDisplayName(),
                (importRows ? "Rows imported or updated: " : "Rows validated: ") + rowsProcessed
            ));
        } catch (IOException e) {
            return new PowerSchoolImportReport(false, safeFilename(originalFilename), null, 0,
                List.of("Failed to read the TSV content."));
        }
    }

    private void flushBatch(PowerSchoolImportDatasetType datasetType, List<Object[]> batch) {
        switch (datasetType) {
            case STUDENTS -> jdbcTemplate.batchUpdate(STUDENT_IMPORT_SQL, batch);
            case STORED_GRADES -> jdbcTemplate.batchUpdate(STORED_GRADE_IMPORT_SQL, batch);
            case PERSON -> jdbcTemplate.batchUpdate(PERSON_IMPORT_SQL, batch);
            case PERSON_ADDRESS -> jdbcTemplate.batchUpdate(PERSON_ADDRESS_IMPORT_SQL, batch);
            case PERSON_ADDRESS_ASSOC -> jdbcTemplate.batchUpdate(PERSON_ADDRESS_ASSOC_IMPORT_SQL, batch);
            case EMAIL_ADDRESS -> jdbcTemplate.batchUpdate(EMAIL_ADDRESS_IMPORT_SQL, batch);
            case PHONE_NUMBER -> jdbcTemplate.batchUpdate(PHONE_NUMBER_IMPORT_SQL, batch);
            case PERSON_PHONE_NUMBER_ASSOC -> jdbcTemplate.batchUpdate(PERSON_PHONE_NUMBER_ASSOC_IMPORT_SQL, batch);
            case PERSON_EMAIL_ADDRESS_ASSOC -> jdbcTemplate.batchUpdate(PERSON_EMAIL_ADDRESS_ASSOC_IMPORT_SQL, batch);
            case STUDENT_CONTACT_ASSOC -> jdbcTemplate.batchUpdate(STUDENT_CONTACT_ASSOC_IMPORT_SQL, batch);
            case STUDENT_CONTACT_DETAIL -> jdbcTemplate.batchUpdate(STUDENT_CONTACT_DETAIL_IMPORT_SQL, batch);
            case PG_FINAL_GRADES -> jdbcTemplate.batchUpdate(PG_FINAL_GRADES_IMPORT_SQL, batch);
            case SECTIONS -> jdbcTemplate.batchUpdate(SECTIONS_IMPORT_SQL, batch);
            case SECTION_TEACHER -> jdbcTemplate.batchUpdate(SECTION_TEACHER_IMPORT_SQL, batch);
            case TEACHERS -> jdbcTemplate.batchUpdate(TEACHERS_IMPORT_SQL, batch);
            case SCHOOL_STAFF -> jdbcTemplate.batchUpdate(SCHOOL_STAFF_IMPORT_SQL, batch);
            case PSM_TEACHER -> jdbcTemplate.batchUpdate(PSM_TEACHER_IMPORT_SQL, batch);
            case PSM_SECTION -> jdbcTemplate.batchUpdate(PSM_SECTION_IMPORT_SQL, batch);
            case PSM_SECTION_TEACHER -> jdbcTemplate.batchUpdate(PSM_SECTION_TEACHER_IMPORT_SQL, batch);
            case GUARDIAN -> jdbcTemplate.batchUpdate(GUARDIAN_IMPORT_SQL, batch);
            case GUARDIAN_STUDENT -> jdbcTemplate.batchUpdate(GUARDIAN_STUDENT_IMPORT_SQL, batch);
            case GUARDIAN_RELATIONSHIP_TYPE -> jdbcTemplate.batchUpdate(GUARDIAN_RELATIONSHIP_TYPE_IMPORT_SQL, batch);
            case GUARDIAN_PERSON_ASSOC -> jdbcTemplate.batchUpdate(GUARDIAN_PERSON_ASSOC_IMPORT_SQL, batch);
            case PSM_STUDENT_CONTACT -> jdbcTemplate.batchUpdate(PSM_STUDENT_CONTACT_IMPORT_SQL, batch);
            case PSM_STUDENT_CONTACT_TYPE -> jdbcTemplate.batchUpdate(PSM_STUDENT_CONTACT_TYPE_IMPORT_SQL, batch);
            case PSM_SCHOOL_COURSE -> jdbcTemplate.batchUpdate(PSM_SCHOOL_COURSE_IMPORT_SQL, batch);
            case PSM_GRADE_SCALE -> jdbcTemplate.batchUpdate(PSM_GRADE_SCALE_IMPORT_SQL, batch);
            case PSM_GRADE -> jdbcTemplate.batchUpdate(PSM_GRADE_IMPORT_SQL, batch);
        }
    }

    private Object[] mapStudentRow(String[] row, Map<String, Integer> index) {
        Integer studentId = parseInteger(value(row, index, "STUDENTS.ID"), "STUDENTS.ID");
        if (studentId != null && studentId == -100) {
            return null;
        }
        return new Object[]{
            studentId,
            parseInteger(value(row, index, "STUDENTS.dcid"), "STUDENTS.dcid"),
            blankToNull(value(row, index, "STUDENTS.First_Name")),
            blankToNull(value(row, index, "STUDENTS.Middle_Name")),
            blankToNull(value(row, index, "STUDENTS.Last_Name")),
            parseInteger(value(row, index, "STUDENTS.Grade_Level"), "STUDENTS.Grade_Level"),
            parseInteger(value(row, index, "STUDENTS.ClassOf"), "STUDENTS.ClassOf"),
            parseInteger(value(row, index, "STUDENTS.SchoolID"), "STUDENTS.SchoolID"),
            parseInteger(value(row, index, "STUDENTS.Enroll_Status"), "STUDENTS.Enroll_Status"),
            parseDate(value(row, index, "STUDENTS.EntryDate"), "STUDENTS.EntryDate"),
            parseDate(value(row, index, "STUDENTS.ExitDate"), "STUDENTS.ExitDate"),
            parseDecimal(value(row, index, "STUDENTS.Cumulative_GPA"), "STUDENTS.Cumulative_GPA"),
            parseBoolean(value(row, index, "STUDENTS.Exclude_fr_rank")),
            blankToNull(value(row, index, "STUDENTS.Mailing_Street")),
            blankToNull(value(row, index, "STUDENTS.Mailing_City")),
            blankToNull(value(row, index, "STUDENTS.Mailing_State")),
            blankToNull(value(row, index, "STUDENTS.Mailing_Zip")),
            blankToNull(value(row, index, "STUDENTS.Home_Phone")),
            blankToNull(value(row, index, "STUDENTS.GuardianEmail")),
            blankToNull(value(row, index, "STUDENTS.Father")),
            blankToNull(value(row, index, "STUDENTS.Mother")),
            blankToNull(value(row, index, "STUDENTCOREFIELDS.father_home_phone")),
            blankToNull(value(row, index, "STUDENTCOREFIELDS.fatherdayphone")),
            blankToNull(value(row, index, "STUDENTCOREFIELDS.mother_home_phone")),
            blankToNull(value(row, index, "STUDENTCOREFIELDS.motherdayphone"))
        };
    }

    private Object[] mapStoredGradeRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "STOREDGRADES.dcid"), "STOREDGRADES.dcid"),
            parseInteger(value(row, index, "STOREDGRADES.StudentID"), "STOREDGRADES.StudentID"),
            parseInteger(value(row, index, "STOREDGRADES.Grade_Level"), "STOREDGRADES.Grade_Level"),
            blankToNull(value(row, index, "STOREDGRADES.Course_Name")),
            blankToNull(value(row, index, "STOREDGRADES.Course_Number")),
            parseInteger(value(row, index, "STOREDGRADES.SectionID"), "STOREDGRADES.SectionID"),
            blankToNull(value(row, index, "STOREDGRADES.StoreCode")),
            parseDecimal(value(row, index, "STOREDGRADES.Percent"), "STOREDGRADES.Percent"),
            blankToNull(value(row, index, "STOREDGRADES.Grade")),
            parseDecimal(value(row, index, "STOREDGRADES.EarnedCrHrs"), "STOREDGRADES.EarnedCrHrs"),
            parseDecimal(value(row, index, "STOREDGRADES.GPA_Points"), "STOREDGRADES.GPA_Points"),
            blankToNull(value(row, index, "STOREDGRADES.GradeScale_Name")),
            parseBoolean(value(row, index, "STOREDGRADES.ExcludeFromGPA")),
            parseBoolean(value(row, index, "STOREDGRADES.ExcludeFromHonorRoll")),
            parseInteger(value(row, index, "STOREDGRADES.SchoolID"), "STOREDGRADES.SchoolID"),
            blankToNull(value(row, index, "STOREDGRADES.SchoolName")),
            parseDate(value(row, index, "STOREDGRADES.DateStored"), "STOREDGRADES.DateStored")
        };
    }

    private Object[] mapPersonRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PERSON.ID"), "PERSON.ID"),
            parseInteger(value(row, index, "PERSON.dcid"), "PERSON.dcid"),
            blankToNull(value(row, index, "PERSON.FIRSTNAME")),
            blankToNull(value(row, index, "PERSON.MIDDLENAME")),
            blankToNull(value(row, index, "PERSON.LASTNAME")),
            parseBoolean(value(row, index, "PERSON.ISACTIVE"))
        };
    }

    private Object[] mapPersonAddressRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PERSONADDRESS.PersonAddressID"), "PERSONADDRESS.PersonAddressID"),
            blankToNull(value(row, index, "PERSONADDRESS.STREET")),
            blankToNull(value(row, index, "PERSONADDRESS.LINETWO")),
            blankToNull(value(row, index, "PERSONADDRESS.CITY")),
            blankToNull(value(row, index, "PERSONADDRESS.POSTALCODE")),
            blankToNull(value(row, index, "PERSONADDRESS.UNIT")),
            parseInteger(value(row, index, "PERSONADDRESS.STATESCODESETID"), "PERSONADDRESS.STATESCODESETID")
        };
    }

    private Object[] mapPersonAddressAssocRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PERSONADDRESSASSOC.PersonAddressAssocID"), "PERSONADDRESSASSOC.PersonAddressAssocID"),
            parseInteger(value(row, index, "PERSONADDRESSASSOC.PERSONADDRESSID"), "PERSONADDRESSASSOC.PERSONADDRESSID"),
            parseInteger(value(row, index, "PERSONADDRESSASSOC.PERSONID"), "PERSONADDRESSASSOC.PERSONID"),
            parseInteger(value(row, index, "PERSONADDRESSASSOC.ADDRESSPRIORITYORDER"), "PERSONADDRESSASSOC.ADDRESSPRIORITYORDER"),
            parseInteger(value(row, index, "PERSONADDRESSASSOC.ADDRESSTYPECODESETID"), "PERSONADDRESSASSOC.ADDRESSTYPECODESETID"),
            parseDate(value(row, index, "PERSONADDRESSASSOC.STARTDATE"), "PERSONADDRESSASSOC.STARTDATE"),
            parseDate(value(row, index, "PERSONADDRESSASSOC.ENDDATE"), "PERSONADDRESSASSOC.ENDDATE")
        };
    }

    private Object[] mapEmailAddressRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "EMAILADDRESS.EmailAddressID"), "EMAILADDRESS.EmailAddressID"),
            blankToNull(value(row, index, "EMAILADDRESS.EmailAddress")),
            parseDate(value(row, index, "EMAILADDRESS.WHENCREATED"), "EMAILADDRESS.WHENCREATED"),
            parseDate(value(row, index, "EMAILADDRESS.WHENMODIFIED"), "EMAILADDRESS.WHENMODIFIED"),
            blankToNull(value(row, index, "EMAILADDRESS.WHOCREATED")),
            blankToNull(value(row, index, "EMAILADDRESS.WHOMODIFIED"))
        };
    }

    private Object[] mapPhoneNumberRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PHONENUMBER.PhoneNumberID"), "PHONENUMBER.PhoneNumberID"),
            parseBoolean(value(row, index, "PHONENUMBER.ISSMS")),
            blankToNull(value(row, index, "PHONENUMBER.PhoneNumber")),
            blankToNull(value(row, index, "PHONENUMBER.PhoneNumberEXT")),
            parseDate(value(row, index, "PHONENUMBER.WHENCREATED"), "PHONENUMBER.WHENCREATED"),
            parseDate(value(row, index, "PHONENUMBER.WHENMODIFIED"), "PHONENUMBER.WHENMODIFIED"),
            blankToNull(value(row, index, "PHONENUMBER.WHOCREATED")),
            blankToNull(value(row, index, "PHONENUMBER.WHOMODIFIED")),
            parseBoolean(value(row, index, "PHONENUMBERCOREFIELDS.isunlisted"))
        };
    }

    private Object[] mapPersonPhoneNumberAssocRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PERSONPHONENUMBERASSOC.PersonPhoneNumberAssocID"), "PERSONPHONENUMBERASSOC.PersonPhoneNumberAssocID"),
            parseInteger(value(row, index, "PERSONPHONENUMBERASSOC.PERSONID"), "PERSONPHONENUMBERASSOC.PERSONID"),
            parseInteger(value(row, index, "PERSONPHONENUMBERASSOC.PHONENUMBERID"), "PERSONPHONENUMBERASSOC.PHONENUMBERID"),
            parseInteger(value(row, index, "PERSONPHONENUMBERASSOC.PHONENUMBERPRIORITYORDER"), "PERSONPHONENUMBERASSOC.PHONENUMBERPRIORITYORDER"),
            parseInteger(value(row, index, "PERSONPHONENUMBERASSOC.PHONETYPECODESETID"), "PERSONPHONENUMBERASSOC.PHONETYPECODESETID"),
            parseBoolean(value(row, index, "PERSONPHONENUMBERASSOC.ISPREFERRED")),
            blankToNull(value(row, index, "PERSONPHONENUMBERASSOC.PHONENUMBERASENTERED")),
            parseDate(value(row, index, "PERSONPHONENUMBERASSOC.WHENCREATED"), "PERSONPHONENUMBERASSOC.WHENCREATED"),
            parseDate(value(row, index, "PERSONPHONENUMBERASSOC.WHENMODIFIED"), "PERSONPHONENUMBERASSOC.WHENMODIFIED"),
            blankToNull(value(row, index, "PERSONPHONENUMBERASSOC.WHOCREATED")),
            blankToNull(value(row, index, "PERSONPHONENUMBERASSOC.WHOMODIFIED"))
        };
    }

    private Object[] mapPersonEmailAddressAssocRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PERSONEMAILADDRESSASSOC.PersonEmailAddressAssocID"), "PERSONEMAILADDRESSASSOC.PersonEmailAddressAssocID"),
            parseInteger(value(row, index, "PERSONEMAILADDRESSASSOC.EMAILADDRESSID"), "PERSONEMAILADDRESSASSOC.EMAILADDRESSID"),
            parseInteger(value(row, index, "PERSONEMAILADDRESSASSOC.EMAILADDRESSPRIORITYORDER"), "PERSONEMAILADDRESSASSOC.EMAILADDRESSPRIORITYORDER"),
            parseInteger(value(row, index, "PERSONEMAILADDRESSASSOC.EMAILTYPECODESETID"), "PERSONEMAILADDRESSASSOC.EMAILTYPECODESETID"),
            parseBoolean(value(row, index, "PERSONEMAILADDRESSASSOC.ISPRIMARYEMAILADDRESS")),
            parseInteger(value(row, index, "PERSONEMAILADDRESSASSOC.PERSONID"), "PERSONEMAILADDRESSASSOC.PERSONID"),
            parseDate(value(row, index, "PERSONEMAILADDRESSASSOC.WHENCREATED"), "PERSONEMAILADDRESSASSOC.WHENCREATED"),
            parseDate(value(row, index, "PERSONEMAILADDRESSASSOC.WHENMODIFIED"), "PERSONEMAILADDRESSASSOC.WHENMODIFIED"),
            blankToNull(value(row, index, "PERSONEMAILADDRESSASSOC.WHOCREATED")),
            blankToNull(value(row, index, "PERSONEMAILADDRESSASSOC.WHOMODIFIED"))
        };
    }

    private Object[] mapStudentContactAssocRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "STUDENTCONTACTASSOC.StudentContactAssocID"), "STUDENTCONTACTASSOC.StudentContactAssocID"),
            parseInteger(value(row, index, "STUDENTCONTACTASSOC.STUDENTDCID"), "STUDENTCONTACTASSOC.STUDENTDCID"),
            parseInteger(value(row, index, "STUDENTCONTACTASSOC.PERSONID"), "STUDENTCONTACTASSOC.PERSONID"),
            parseInteger(value(row, index, "STUDENTCONTACTASSOC.CONTACTPRIORITYORDER"), "STUDENTCONTACTASSOC.CONTACTPRIORITYORDER"),
            parseInteger(value(row, index, "STUDENTCONTACTASSOC.CURRRELTYPECODESETID"), "STUDENTCONTACTASSOC.CURRRELTYPECODESETID")
        };
    }

    private Object[] mapStudentContactDetailRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "STUDENTCONTACTDETAIL.StudentContactDetailID"), "STUDENTCONTACTDETAIL.StudentContactDetailID"),
            parseInteger(value(row, index, "STUDENTCONTACTDETAIL.STUDENTCONTACTASSOCID"), "STUDENTCONTACTDETAIL.STUDENTCONTACTASSOCID"),
            parseInteger(value(row, index, "STUDENTCONTACTDETAIL.RELATIONSHIPTYPECODESETID"), "STUDENTCONTACTDETAIL.RELATIONSHIPTYPECODESETID"),
            parseBoolean(value(row, index, "STUDENTCONTACTDETAIL.CONFIDENTIALCOMMFLAG")),
            parseBoolean(value(row, index, "STUDENTCONTACTDETAIL.GENERALCOMMFLAG")),
            parseBoolean(value(row, index, "STUDENTCONTACTDETAIL.ISACTIVE")),
            parseBoolean(value(row, index, "STUDENTCONTACTDETAIL.ISCUSTODIAL")),
            parseBoolean(value(row, index, "STUDENTCONTACTDETAIL.ISEMERGENCY")),
            parseBoolean(value(row, index, "STUDENTCONTACTDETAIL.LIVESWITHFLG")),
            parseBoolean(value(row, index, "STUDENTCONTACTDETAIL.RECEIVESMAILFLG")),
            parseBoolean(value(row, index, "STUDENTCONTACTDETAIL.SCHOOLPICKUPFLG")),
            parseDate(value(row, index, "STUDENTCONTACTDETAIL.STARTDATE"), "STUDENTCONTACTDETAIL.STARTDATE"),
            parseDate(value(row, index, "STUDENTCONTACTDETAIL.ENDDATE"), "STUDENTCONTACTDETAIL.ENDDATE"),
            blankToNull(value(row, index, "STUDENTCONTACTDETAIL.RELATIONSHIPNOTE")),
            parseBoolean(value(row, index, "STUDENTCONTACTDETAILCOREFIELDS.iscaregiver")),
            parseBoolean(value(row, index, "STUDENTCONTACTDETAILCOREFIELDS.legalguardian")),
            blankToNull(value(row, index, "STUDENTCONTACTDETAILCOREFIELDS.physicaladdresssource")),
            parseBoolean(value(row, index, "STUDENTCONTACTDETAILCOREFIELDS.primarycontact")),
            parseBoolean(value(row, index, "STUDENTCONTACTDETAILCOREFIELDS.samehomephonenumber")),
            parseBoolean(value(row, index, "STUDENTCONTACTDETAILCOREFIELDS.samemailingaddress")),
            blankToNull(value(row, index, "STUDENTCONTACTDETAILCOREFIELDS.source"))
        };
    }

    private Object[] mapPgFinalGradesRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PGFINALGRADES.ID"), "PGFINALGRADES.ID"),
            parseInteger(value(row, index, "PGFINALGRADES.dcid"), "PGFINALGRADES.dcid"),
            blankToNull(value(row, index, "PGFINALGRADES.Citizenship")),
            blankToNull(value(row, index, "PGFINALGRADES.FinalGradeName")),
            blankToNull(value(row, index, "PGFINALGRADES.Grade")),
            parseLooseDecimal(value(row, index, "PGFINALGRADES.Percent"), "PGFINALGRADES.Percent"),
            parseInteger(value(row, index, "PGFINALGRADES.SectionID"), "PGFINALGRADES.SectionID"),
            parseDate(value(row, index, "PGFINALGRADES.StartDate"), "PGFINALGRADES.StartDate"),
            parseDate(value(row, index, "PGFINALGRADES.EndDate"), "PGFINALGRADES.EndDate"),
            parseInteger(value(row, index, "PGFINALGRADES.StudentID"), "PGFINALGRADES.StudentID")
        };
    }

    private Object[] mapSectionsRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "SECTIONS.ID"), "SECTIONS.ID"),
            parseInteger(value(row, index, "SECTIONS.dcid"), "SECTIONS.dcid"),
            blankToNull(value(row, index, "SECTIONS.Course_Number")),
            blankToNull(value(row, index, "SECTIONS.Grade_Level")),
            parseInteger(value(row, index, "SECTIONS.GradeScaleID"), "SECTIONS.GradeScaleID"),
            parseInteger(value(row, index, "SECTIONS.Teacher"), "SECTIONS.Teacher"),
            blankToNull(value(row, index, "SECTIONS.TeacherDescr")),
            parseInteger(value(row, index, "SECTIONS.SchoolID"), "SECTIONS.SchoolID"),
            parseInteger(value(row, index, "SECTIONS.TermID"), "SECTIONS.TermID")
        };
    }

    private Object[] mapSectionTeacherRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "SECTIONTEACHER.ID"), "SECTIONTEACHER.ID"),
            parseInteger(value(row, index, "SECTIONTEACHER.SECTIONID"), "SECTIONTEACHER.SECTIONID"),
            parseInteger(value(row, index, "SECTIONTEACHER.TeacherID"), "SECTIONTEACHER.TeacherID"),
            parseInteger(value(row, index, "SECTIONTEACHER.PRIORITYORDER"), "SECTIONTEACHER.PRIORITYORDER"),
            parseDate(value(row, index, "SECTIONTEACHER.START_DATE"), "SECTIONTEACHER.START_DATE"),
            parseDate(value(row, index, "SECTIONTEACHER.END_DATE"), "SECTIONTEACHER.END_DATE")
        };
    }

    private Object[] mapTeachersRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "TEACHERS.ID"), "TEACHERS.ID"),
            parseInteger(value(row, index, "TEACHERS.dcid"), "TEACHERS.dcid"),
            blankToNull(value(row, index, "TEACHERS.First_Name")),
            blankToNull(value(row, index, "TEACHERS.Middle_Name")),
            blankToNull(value(row, index, "TEACHERS.Last_Name")),
            blankToNull(value(row, index, "TEACHERS.LastFirst")),
            blankToNull(value(row, index, "TEACHERS.Email_Addr")),
            blankToNull(value(row, index, "TEACHERS.LoginID")),
            blankToNull(value(row, index, "TEACHERS.TeacherLoginID")),
            blankToNull(value(row, index, "TEACHERS.TeacherNumber")),
            blankToNull(value(row, index, "TEACHERS.Title")),
            parseInteger(value(row, index, "TEACHERS.SchoolID"), "TEACHERS.SchoolID"),
            parseInteger(value(row, index, "TEACHERS.StaffStatus"), "TEACHERS.StaffStatus"),
            parseInteger(value(row, index, "TEACHERS.Status"), "TEACHERS.Status")
        };
    }

    private Object[] mapSchoolStaffRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "SCHOOLSTAFF.ID"), "SCHOOLSTAFF.ID"),
            parseInteger(value(row, index, "SCHOOLSTAFF.dcid"), "SCHOOLSTAFF.dcid"),
            parseInteger(value(row, index, "SCHOOLSTAFF.SchoolID"), "SCHOOLSTAFF.SchoolID"),
            parseInteger(value(row, index, "SCHOOLSTAFF.StaffStatus"), "SCHOOLSTAFF.StaffStatus"),
            parseInteger(value(row, index, "SCHOOLSTAFF.Status"), "SCHOOLSTAFF.Status"),
            parseDate(value(row, index, "SCHOOLSTAFF.TRANSACTION_DATE"), "SCHOOLSTAFF.TRANSACTION_DATE"),
            parseInteger(value(row, index, "SCHOOLSTAFF.Users_DCID"), "SCHOOLSTAFF.Users_DCID")
        };
    }

    private Object[] mapPsmTeacherRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PSM_TEACHER.ID"), "PSM_TEACHER.ID"),
            blankToNull(value(row, index, "PSM_TEACHER.EMAIL")),
            blankToNull(value(row, index, "PSM_TEACHER.ETHNICITY")),
            blankToNull(value(row, index, "PSM_TEACHER.FIRSTNAME")),
            parseBoolean(value(row, index, "PSM_TEACHER.ISACCESSIBLE")),
            blankToNull(value(row, index, "PSM_TEACHER.LASTNAME")),
            parseBoolean(value(row, index, "PSM_TEACHER.LDAPENABLED")),
            blankToNull(value(row, index, "PSM_TEACHER.TEACHERIDENTIFIER")),
            blankToNull(value(row, index, "PSM_TEACHER.USERNAME"))
        };
    }

    private Object[] mapPsmSectionRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PSM_SECTION.ID"), "PSM_SECTION.ID"),
            blankToNull(value(row, index, "PSM_SECTION.DESCRIPTION")),
            parseInteger(value(row, index, "PSM_SECTION.GRADEBOOKTYPE"), "PSM_SECTION.GRADEBOOKTYPE"),
            blankToNull(value(row, index, "PSM_SECTION.MEETING")),
            blankToNull(value(row, index, "PSM_SECTION.ROOMNAME")),
            parseInteger(value(row, index, "PSM_SECTION.SCHOOLCOURSEID"), "PSM_SECTION.SCHOOLCOURSEID"),
            parseInteger(value(row, index, "PSM_SECTION.SCHOOLID"), "PSM_SECTION.SCHOOLID"),
            blankToNull(value(row, index, "PSM_SECTION.SECTIONIDENTIFIER")),
            parseInteger(value(row, index, "PSM_SECTION.TERMID"), "PSM_SECTION.TERMID")
        };
    }

    private Object[] mapPsmSectionTeacherRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PSM_SECTIONTEACHER.ID"), "PSM_SECTIONTEACHER.ID"),
            parseLooseDecimal(value(row, index, "PSM_SECTIONTEACHER.ALLOCATION"), "PSM_SECTIONTEACHER.ALLOCATION"),
            parseDate(value(row, index, "PSM_SECTIONTEACHER.END_DATE"), "PSM_SECTIONTEACHER.END_DATE"),
            parseInteger(value(row, index, "PSM_SECTIONTEACHER.PRIORITYORDER"), "PSM_SECTIONTEACHER.PRIORITYORDER"),
            parseInteger(value(row, index, "PSM_SECTIONTEACHER.ROLEID"), "PSM_SECTIONTEACHER.ROLEID"),
            parseInteger(value(row, index, "PSM_SECTIONTEACHER.SECTIONID"), "PSM_SECTIONTEACHER.SECTIONID"),
            blankToNull(value(row, index, "PSM_SECTIONTEACHER.SECTIONNICKNAME")),
            parseDate(value(row, index, "PSM_SECTIONTEACHER.START_DATE"), "PSM_SECTIONTEACHER.START_DATE"),
            parseInteger(value(row, index, "PSM_SECTIONTEACHER.TEACHERID"), "PSM_SECTIONTEACHER.TEACHERID")
        };
    }

    private Object[] mapGuardianRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "GUARDIAN.GUARDIANID"), "GUARDIAN.GUARDIANID"),
            blankToNull(value(row, index, "GUARDIAN.ACCOUNTIDENTIFIER")),
            blankToNull(value(row, index, "GUARDIAN.EMAIL")),
            blankToNull(value(row, index, "GUARDIAN.FIRSTNAME")),
            blankToNull(value(row, index, "GUARDIAN.LASTNAME")),
            blankToNull(value(row, index, "GUARDIAN.MIDDLENAME")),
            blankToNull(value(row, index, "GUARDIAN.PSGUID")),
            blankToNull(value(row, index, "GUARDIAN.STATE_GuardianNUMBER"))
        };
    }

    private Object[] mapGuardianStudentRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "GUARDIANSTUDENT.GuardianStudentID"), "GUARDIANSTUDENT.GuardianStudentID"),
            parseInteger(value(row, index, "GUARDIANSTUDENT.GUARDIANID"), "GUARDIANSTUDENT.GUARDIANID"),
            parseInteger(value(row, index, "GUARDIANSTUDENT.GUARDIANRELATIONSHIPTYPEID"), "GUARDIANSTUDENT.GUARDIANRELATIONSHIPTYPEID"),
            parseInteger(value(row, index, "GUARDIANSTUDENT.STUDENTSDCID"), "GUARDIANSTUDENT.STUDENTSDCID"),
            parseBoolean(value(row, index, "GUARDIANSTUDENT.AUTOSEND_ATTENDANCEDETAIL")),
            parseBoolean(value(row, index, "GUARDIANSTUDENT.AUTOSEND_BALANCEALERT")),
            parseBoolean(value(row, index, "GUARDIANSTUDENT.AUTOSEND_GRADEDETAIL")),
            parseInteger(value(row, index, "GUARDIANSTUDENT.AUTOSEND_HOWOFTEN"), "GUARDIANSTUDENT.AUTOSEND_HOWOFTEN"),
            parseBoolean(value(row, index, "GUARDIANSTUDENT.AUTOSEND_SCHOOLANNOUNCEMENTS")),
            parseBoolean(value(row, index, "GUARDIANSTUDENT.AUTOSEND_SUMMARY"))
        };
    }

    private Object[] mapGuardianRelationshipTypeRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "GUARDIANRELATIONSHIPTYPE.GuardianRelationshipTypeID"), "GUARDIANRELATIONSHIPTYPE.GuardianRelationshipTypeID"),
            parseInteger(value(row, index, "GUARDIANRELATIONSHIPTYPE.DISPLAYORDER"), "GUARDIANRELATIONSHIPTYPE.DISPLAYORDER"),
            parseInteger(value(row, index, "GUARDIANRELATIONSHIPTYPE.SIFRELATIONTOSTUDENT"), "GUARDIANRELATIONSHIPTYPE.SIFRELATIONTOSTUDENT")
        };
    }

    private Object[] mapGuardianPersonAssocRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "GUARDIANPERSONASSOC.GuardianPersonAssocID"), "GUARDIANPERSONASSOC.GuardianPersonAssocID"),
            parseInteger(value(row, index, "GUARDIANPERSONASSOC.GUARDIANID"), "GUARDIANPERSONASSOC.GUARDIANID"),
            parseInteger(value(row, index, "GUARDIANPERSONASSOC.PERSONID"), "GUARDIANPERSONASSOC.PERSONID")
        };
    }

    private Object[] mapPsmStudentContactRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PSM_STUDENTCONTACT.ID"), "PSM_STUDENTCONTACT.ID"),
            blankToNull(value(row, index, "PSM_STUDENTCONTACT.EMAIL")),
            blankToNull(value(row, index, "PSM_STUDENTCONTACT.FIRSTNAME")),
            blankToNull(value(row, index, "PSM_STUDENTCONTACT.LASTNAME")),
            blankToNull(value(row, index, "PSM_STUDENTCONTACT.PHONE")),
            parseInteger(value(row, index, "PSM_STUDENTCONTACT.STUDENTCONTACTTYPEID"), "PSM_STUDENTCONTACT.STUDENTCONTACTTYPEID"),
            parseInteger(value(row, index, "PSM_STUDENTCONTACT.STUDENTID"), "PSM_STUDENTCONTACT.STUDENTID")
        };
    }

    private Object[] mapPsmStudentContactTypeRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PSM_STUDENTCONTACTTYPE.ID"), "PSM_STUDENTCONTACTTYPE.ID"),
            blankToNull(value(row, index, "PSM_STUDENTCONTACTTYPE.DESCRIPTION")),
            blankToNull(value(row, index, "PSM_STUDENTCONTACTTYPE.NAME"))
        };
    }

    private Object[] mapPsmSchoolCourseRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PSM_SCHOOLCOURSE.ID"), "PSM_SCHOOLCOURSE.ID"),
            blankToNull(value(row, index, "PSM_SCHOOLCOURSE.ABBREVIATION")),
            blankToNull(value(row, index, "PSM_SCHOOLCOURSE.COURSECODE")),
            parseInteger(value(row, index, "PSM_SCHOOLCOURSE.COURSEID"), "PSM_SCHOOLCOURSE.COURSEID"),
            blankToNull(value(row, index, "PSM_SCHOOLCOURSE.DESCRIPTION")),
            blankToNull(value(row, index, "PSM_SCHOOLCOURSE.SCHOOLCOURSETITLE")),
            parseInteger(value(row, index, "PSM_SCHOOLCOURSE.SCHOOLID"), "PSM_SCHOOLCOURSE.SCHOOLID")
        };
    }

    private Object[] mapPowerSchoolGradeScaleRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PSM_GRADESCALE.ID"), "PSM_GRADESCALE.ID"),
            parseBoolean(value(row, index, "PSM_GRADESCALE.CANMODIFY")),
            parseInteger(value(row, index, "PSM_GRADESCALE.CONTENTGROUPID"), "PSM_GRADESCALE.CONTENTGROUPID"),
            blankToNull(value(row, index, "PSM_GRADESCALE.DESCRIPTION")),
            blankToNull(value(row, index, "PSM_GRADESCALE.GRADINGSCALE")),
            parseBoolean(value(row, index, "PSM_GRADESCALE.ISDISTRICTDEFAULT")),
            parseBoolean(value(row, index, "PSM_GRADESCALE.ISNUMERIC")),
            blankToNull(value(row, index, "PSM_GRADESCALE.NAME")),
            parseDecimal(value(row, index, "PSM_GRADESCALE.NUMERICMAX"), "PSM_GRADESCALE.NUMERICMAX"),
            parseDecimal(value(row, index, "PSM_GRADESCALE.NUMERICMIN"), "PSM_GRADESCALE.NUMERICMIN"),
            parseInteger(value(row, index, "PSM_GRADESCALE.NUMERICPRECISION"), "PSM_GRADESCALE.NUMERICPRECISION"),
            parseInteger(value(row, index, "PSM_GRADESCALE.NUMERICSCALE"), "PSM_GRADESCALE.NUMERICSCALE"),
            parseInteger(value(row, index, "PSM_GRADESCALE.PARENTGRADESCALEID"), "PSM_GRADESCALE.PARENTGRADESCALEID"),
            parseInteger(value(row, index, "PSM_GRADESCALE.SCHOOLID"), "PSM_GRADESCALE.SCHOOLID"),
            parseInteger(value(row, index, "PSM_GRADESCALE.TEACHERID"), "PSM_GRADESCALE.TEACHERID")
        };
    }

    private Object[] mapPowerSchoolGradeRow(String[] row, Map<String, Integer> index) {
        return new Object[]{
            parseInteger(value(row, index, "PSM_GRADE.ID"), "PSM_GRADE.ID"),
            parseBoolean(value(row, index, "PSM_GRADE.ADMINUSEONLY")),
            parseDecimal(value(row, index, "PSM_GRADE.CUTOFFPERCENT"), "PSM_GRADE.CUTOFFPERCENT"),
            parseBoolean(value(row, index, "PSM_GRADE.DEFAULTZEROCUTOFF")),
            blankToNull(value(row, index, "PSM_GRADE.DESCRIPTION")),
            blankToNull(value(row, index, "PSM_GRADE.GRADELABEL")),
            parseInteger(value(row, index, "PSM_GRADE.GRADESCALEID"), "PSM_GRADE.GRADESCALEID"),
            parseBoolean(value(row, index, "PSM_GRADE.ISSPECIAL")),
            parseDecimal(value(row, index, "PSM_GRADE.PERCENTVALUE"), "PSM_GRADE.PERCENTVALUE"),
            parseDecimal(value(row, index, "PSM_GRADE.POINTSVALUE"), "PSM_GRADE.POINTSVALUE"),
            parseInteger(value(row, index, "PSM_GRADE.SORTORDER"), "PSM_GRADE.SORTORDER")
        };
    }

    private Map<String, Integer> buildIndex(String[] headers) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            String header = normalizeToken(headers[i]);
            if (i == 0 && header != null && !header.isEmpty() && header.charAt(0) == '\uFEFF') {
                header = header.substring(1);
            }
            index.put(header, i);
        }
        return index;
    }

    private PowerSchoolImportDatasetType detectDatasetType(Set<String> headers) {
        Set<String> normalizedHeaders = new LinkedHashSet<>(headers);
        if (normalizedHeaders.containsAll(STUDENT_HEADERS)) {
            return PowerSchoolImportDatasetType.STUDENTS;
        }
        if (normalizedHeaders.containsAll(STORED_GRADE_HEADERS)) {
            return PowerSchoolImportDatasetType.STORED_GRADES;
        }
        if (normalizedHeaders.containsAll(PERSON_HEADERS)) {
            return PowerSchoolImportDatasetType.PERSON;
        }
        if (normalizedHeaders.containsAll(PERSON_ADDRESS_HEADERS)) {
            return PowerSchoolImportDatasetType.PERSON_ADDRESS;
        }
        if (normalizedHeaders.containsAll(PERSON_ADDRESS_ASSOC_HEADERS)) {
            return PowerSchoolImportDatasetType.PERSON_ADDRESS_ASSOC;
        }
        if (normalizedHeaders.containsAll(EMAIL_ADDRESS_HEADERS)) {
            return PowerSchoolImportDatasetType.EMAIL_ADDRESS;
        }
        if (normalizedHeaders.containsAll(PHONE_NUMBER_HEADERS)) {
            return PowerSchoolImportDatasetType.PHONE_NUMBER;
        }
        if (normalizedHeaders.containsAll(PERSON_PHONE_NUMBER_ASSOC_HEADERS)) {
            return PowerSchoolImportDatasetType.PERSON_PHONE_NUMBER_ASSOC;
        }
        if (normalizedHeaders.containsAll(PERSON_EMAIL_ADDRESS_ASSOC_HEADERS)) {
            return PowerSchoolImportDatasetType.PERSON_EMAIL_ADDRESS_ASSOC;
        }
        if (normalizedHeaders.containsAll(STUDENT_CONTACT_ASSOC_HEADERS)) {
            return PowerSchoolImportDatasetType.STUDENT_CONTACT_ASSOC;
        }
        if (normalizedHeaders.containsAll(STUDENT_CONTACT_DETAIL_HEADERS)) {
            return PowerSchoolImportDatasetType.STUDENT_CONTACT_DETAIL;
        }
        if (normalizedHeaders.containsAll(PG_FINAL_GRADES_HEADERS)) {
            return PowerSchoolImportDatasetType.PG_FINAL_GRADES;
        }
        if (normalizedHeaders.containsAll(SECTIONS_HEADERS)) {
            return PowerSchoolImportDatasetType.SECTIONS;
        }
        if (normalizedHeaders.containsAll(SECTION_TEACHER_HEADERS)) {
            return PowerSchoolImportDatasetType.SECTION_TEACHER;
        }
        if (normalizedHeaders.containsAll(TEACHERS_HEADERS)) {
            return PowerSchoolImportDatasetType.TEACHERS;
        }
        if (normalizedHeaders.containsAll(SCHOOL_STAFF_HEADERS)) {
            return PowerSchoolImportDatasetType.SCHOOL_STAFF;
        }
        if (normalizedHeaders.containsAll(PSM_TEACHER_HEADERS)) {
            return PowerSchoolImportDatasetType.PSM_TEACHER;
        }
        if (normalizedHeaders.containsAll(PSM_SECTION_HEADERS)) {
            return PowerSchoolImportDatasetType.PSM_SECTION;
        }
        if (normalizedHeaders.containsAll(PSM_SECTION_TEACHER_HEADERS)) {
            return PowerSchoolImportDatasetType.PSM_SECTION_TEACHER;
        }
        if (normalizedHeaders.containsAll(GUARDIAN_HEADERS)) {
            return PowerSchoolImportDatasetType.GUARDIAN;
        }
        if (normalizedHeaders.containsAll(GUARDIAN_STUDENT_HEADERS)) {
            return PowerSchoolImportDatasetType.GUARDIAN_STUDENT;
        }
        if (normalizedHeaders.containsAll(GUARDIAN_RELATIONSHIP_TYPE_HEADERS)) {
            return PowerSchoolImportDatasetType.GUARDIAN_RELATIONSHIP_TYPE;
        }
        if (normalizedHeaders.containsAll(GUARDIAN_PERSON_ASSOC_HEADERS)) {
            return PowerSchoolImportDatasetType.GUARDIAN_PERSON_ASSOC;
        }
        if (normalizedHeaders.containsAll(PSM_STUDENT_CONTACT_HEADERS)) {
            return PowerSchoolImportDatasetType.PSM_STUDENT_CONTACT;
        }
        if (normalizedHeaders.containsAll(PSM_STUDENT_CONTACT_TYPE_HEADERS)) {
            return PowerSchoolImportDatasetType.PSM_STUDENT_CONTACT_TYPE;
        }
        if (normalizedHeaders.containsAll(PSM_SCHOOL_COURSE_HEADERS)) {
            return PowerSchoolImportDatasetType.PSM_SCHOOL_COURSE;
        }
        if (normalizedHeaders.containsAll(PSM_GRADE_SCALE_HEADERS)) {
            return PowerSchoolImportDatasetType.PSM_GRADE_SCALE;
        }
        if (normalizedHeaders.containsAll(PSM_GRADE_HEADERS)) {
            return PowerSchoolImportDatasetType.PSM_GRADE;
        }
        return null;
    }

    private String value(String[] row, Map<String, Integer> index, String column) {
        Integer location = index.get(column);
        if (location == null || location >= row.length) {
            throw new IllegalArgumentException("Missing required column " + column);
        }
        return normalizeToken(row[location]);
    }

    private Integer parseInteger(String value, String column) {
        String normalized = blankToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            return Integer.valueOf(normalized);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(column + " is not a valid integer: " + normalized);
        }
    }

    private BigDecimal parseDecimal(String value, String column) {
        String normalized = blankToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(column + " is not a valid decimal: " + normalized);
        }
    }

    private BigDecimal parseLooseDecimal(String value, String column) {
        String normalized = blankToNull(value);
        if (normalized == null || "--".equals(normalized)) {
            return null;
        }
        return parseDecimal(normalized, column);
    }

    private Date parseDate(String value, String column) {
        String normalized = blankToNull(value);
        if (normalized == null) {
            return null;
        }
        try {
            return Date.valueOf(LocalDate.parse(normalized));
        } catch (Exception ex) {
            throw new IllegalArgumentException(column + " is not a valid ISO date: " + normalized);
        }
    }

    private Boolean parseBoolean(String value) {
        String normalized = blankToNull(value);
        if (normalized == null) {
            return Boolean.FALSE;
        }
        if ("1".equals(normalized) || "true".equalsIgnoreCase(normalized)) {
            return Boolean.TRUE;
        }
        if ("0".equals(normalized) || "false".equalsIgnoreCase(normalized)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Boolean value is not valid: " + normalized);
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String normalized = normalizeToken(value);
        return normalized.isEmpty() ? null : normalized;
    }

    private String normalizeToken(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.length() >= 2 && normalized.startsWith("\"") && normalized.endsWith("\"")) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }
        return normalized.replace("\"\"", "\"").trim();
    }

    private void addError(List<String> errors, String message) {
        if (errors.size() < PREVIEW_ERROR_LIMIT) {
            errors.add(message);
        }
    }

    private String[] parseTsvRow(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
                continue;
            }
            if (ch == '\t' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(ch);
        }
        values.add(current.toString());
        return values.toArray(String[]::new);
    }

    private String safeFilename(String filename) {
        return filename == null ? "upload.tsv" : filename;
    }
}
