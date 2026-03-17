package org.gca.schoolms.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.gca.schoolms.academics.AttendanceRecord;
import org.gca.schoolms.academics.AttendanceRecordRepository;
import org.gca.schoolms.academics.AttendanceStatus;
import org.gca.schoolms.enrollment.EnrollmentRequest;
import org.gca.schoolms.enrollment.EnrollmentRequestRepository;
import org.gca.schoolms.enrollment.EnrollmentRequestStatus;
import org.gca.schoolms.enrollment.EnrollmentRequestType;
import org.gca.schoolms.academics.Section;
import org.gca.schoolms.academics.SectionRepository;
import org.gca.schoolms.finance.FamilyAccount;
import org.gca.schoolms.finance.FamilyAccountRepository;
import org.gca.schoolms.finance.FeeType;
import org.gca.schoolms.finance.FeeTypeRepository;
import org.gca.schoolms.finance.MaritalStatus;
import org.gca.schoolms.finance.Payment;
import org.gca.schoolms.finance.PaymentAllocation;
import org.gca.schoolms.finance.PaymentMethod;
import org.gca.schoolms.finance.PaymentRepository;
import org.gca.schoolms.finance.StudentFee;
import org.gca.schoolms.finance.StudentFeeRepository;
import org.gca.schoolms.organization.Campus;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.records.GradeLevel;
import org.gca.schoolms.records.Student;
import org.gca.schoolms.records.StudentRepository;
import org.gca.schoolms.records.StudentStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedData(CampusRepository campusRepository, FamilyAccountRepository familyAccountRepository,
                               StudentRepository studentRepository, FeeTypeRepository feeTypeRepository,
                               StudentFeeRepository studentFeeRepository, PaymentRepository paymentRepository,
                               SectionRepository sectionRepository, AttendanceRecordRepository attendanceRecordRepository,
                               EnrollmentRequestRepository enrollmentRequestRepository) {
        return args -> {
            Campus saipan = campusRepository.findByCode("GCA-SAI")
                .orElseGet(() -> campusRepository.save(new Campus("GCA-SAI", "Grace Christian Academy Saipan", "Saipan", true)));
            Campus tinian = campusRepository.findByCode("GCA-TIN")
                .orElseGet(() -> campusRepository.save(new Campus("GCA-TIN", "Grace Christian Academy Tinian", "Tinian", true)));
            Campus rota = campusRepository.findByCode("GCA-ROT")
                .orElseGet(() -> campusRepository.save(new Campus("GCA-ROT", "Grace Christian Academy Rota", "Rota", true)));

            FamilyAccount cruzFamily = null;
            FamilyAccount santosFamily = null;
            FamilyAccount manglonaFamily = null;
            if (familyAccountRepository.count() == 0) {
                cruzFamily = familyAccountRepository.save(new FamilyAccount("FA-1001", "Cruz Family", "Elena Cruz",
                    "elena.cruz@example.org", "670-555-0101",
                    "123 Palm Street", "Unit A", "Saipan", "MP", "96950",
                    "GCA", "670-555-1101", "elena.cruz.work@example.org", "345 School Lane", "",
                    "Saipan", "MP", "96950", "Female", "CHamoru", "US Citizen", "United States",
                    false, "", "", null, null, MaritalStatus.MARRIED,
                    "David Cruz", "david.cruz@example.org", "670-555-0102",
                    "123 Palm Street", "Unit A", "Saipan", "MP", "96950",
                    "Marianas Utilities", "670-555-1199", "david.cruz.work@example.org", "22 Port Ave", "",
                    "Saipan", "MP", "96950", "Male", "CHamoru", "US Citizen", "United States",
                    false, "", "", null, null,
                    true, true, saipan));
                santosFamily = familyAccountRepository.save(new FamilyAccount("FA-1002", "Santos Family", "Marco Santos",
                    "marco.santos@example.org", "670-555-0202",
                    "45 Harbor Road", "", "Tinian", "MP", "96952",
                    "Marianas Telecom", "670-555-2202", "marco.santos.work@example.org", "88 Commerce Ave", "",
                    "Tinian", "MP", "96952", "Male", "Carolinian", "US Citizen", "United States",
                    false, "", "", null, null, MaritalStatus.MARRIED,
                    "Lia Santos", "lia.santos@example.org", "670-555-0203",
                    "45 Harbor Road", "", "Tinian", "MP", "96952",
                    "Tinian Health Center", "670-555-2299", "lia.santos.work@example.org", "9 Clinic Rd", "",
                    "Tinian", "MP", "96952", "Female", "Carolinian", "US Citizen", "United States",
                    false, "", "", null, null,
                    true, false, tinian));
                manglonaFamily = familyAccountRepository.save(new FamilyAccount("FA-1003", "Mangi Family", "Rosa Manglona",
                    "rosa.manglona@example.org", "670-555-0303",
                    "12 Sunset Drive", "", "Rota", "MP", "96951",
                    "Rota Clinic", "670-555-3303", "rosa.manglona.work@example.org", "7 Health Center Rd", "",
                    "Rota", "MP", "96951", "Female", "CHamoru", "US Citizen", "United States",
                    false, "", "", null, null, MaritalStatus.WIDOWED,
                    "", "", "",
                    "", "", "", "", "",
                    "", "", "", "", "",
                    "", "", "", "", "",
                    "", "", false, "", "", null, null,
                    false, true, rota));
            } else {
                cruzFamily = familyAccountRepository.findTop10ByOrderByAccountNameAsc().stream()
                    .filter(account -> "FA-1001".equals(account.getAccountNumber())).findFirst().orElseThrow();
                santosFamily = familyAccountRepository.findTop10ByOrderByAccountNameAsc().stream()
                    .filter(account -> "FA-1002".equals(account.getAccountNumber())).findFirst().orElseThrow();
                manglonaFamily = familyAccountRepository.findTop10ByOrderByAccountNameAsc().stream()
                    .filter(account -> "FA-1003".equals(account.getAccountNumber())).findFirst().orElseThrow();
            }

            Student ava = null;
            Student micah = null;
            Student leah = null;
            if (studentRepository.count() == 0) {
                ava = studentRepository.save(new Student("2026-001", "Ava", "Marie", "Cruz", "", "Ava", LocalDate.of(2010, 5, 12), GradeLevel.GRADE_10, saipan, cruzFamily, StudentStatus.ACTIVE));
                micah = studentRepository.save(new Student("2026-002", "Micah", "", "Santos", "", "Micah", LocalDate.of(2011, 9, 3), GradeLevel.GRADE_9, tinian, santosFamily, StudentStatus.ACTIVE));
                leah = studentRepository.save(new Student("2025-031", "Leah", "", "Palomo", "", "", LocalDate.of(2008, 12, 14), GradeLevel.GRADE_12, rota, manglonaFamily, StudentStatus.GRADUATED));
            } else {
                var students = studentRepository.findTop10ByOrderByLastNameAscFirstNameAsc();
                ava = students.stream().filter(student -> "2026-001".equals(student.getStudentNumber())).findFirst().orElseThrow();
                micah = students.stream().filter(student -> "2026-002".equals(student.getStudentNumber())).findFirst().orElseThrow();
                leah = students.stream().filter(student -> "2025-031".equals(student.getStudentNumber())).findFirst().orElseThrow();
            }
            Section english = null;
            Section algebra = null;
            Section biology = null;
            FeeType enrollmentFeeType = feeTypeRepository.findByCode("ENROLLMENT_FEE")
                .orElseGet(() -> feeTypeRepository.save(new FeeType("ENROLLMENT_FEE", "Enrollment fee", new BigDecimal("300.00"), true)));
            FeeType applicationFeeType = feeTypeRepository.findByCode("APPLICATION_FEE")
                .orElseGet(() -> feeTypeRepository.save(new FeeType("APPLICATION_FEE", "Application fee", new BigDecimal("150.00"), true)));
            FeeType tuitionFeeType = feeTypeRepository.findByCode("TUITION_FEE")
                .orElseGet(() -> feeTypeRepository.save(new FeeType("TUITION_FEE", "Tuition fee", null, true)));
            feeTypeRepository.findByCode("BOOK_FEE")
                .orElseGet(() -> feeTypeRepository.save(new FeeType("BOOK_FEE", "Book fee", null, true)));
            feeTypeRepository.findByCode("COMPUTER_LAB_FEE")
                .orElseGet(() -> feeTypeRepository.save(new FeeType("COMPUTER_LAB_FEE", "Computer lab fee", null, true)));
            feeTypeRepository.findByCode("ELECTRONIC_FEE")
                .orElseGet(() -> feeTypeRepository.save(new FeeType("ELECTRONIC_FEE", "Electronic fee", null, true)));
            feeTypeRepository.findByCode("LUNCH_FEE")
                .orElseGet(() -> feeTypeRepository.save(new FeeType("LUNCH_FEE", "Lunch fee", null, true)));
            feeTypeRepository.findByCode("AFTERSCHOOL_FEE")
                .orElseGet(() -> feeTypeRepository.save(new FeeType("AFTERSCHOOL_FEE", "Afterschool fee", null, true)));
            if (studentFeeRepository.count() == 0) {
                StudentFee avaTuition = studentFeeRepository.save(new StudentFee(
                    ava, cruzFamily, saipan, tuitionFeeType, new BigDecimal("2400.00"),
                    LocalDate.now().minusDays(20).atStartOfDay(), "2025-2026", "Tuition - Semester 2"));
                StudentFee micahEnrollment = studentFeeRepository.save(new StudentFee(
                    micah, santosFamily, tinian, enrollmentFeeType, new BigDecimal("300.00"),
                    LocalDate.now().minusDays(8).atStartOfDay(), "2026-2027", "Enrollment fee"));
                StudentFee micahApplication = studentFeeRepository.save(new StudentFee(
                    micah, santosFamily, tinian, applicationFeeType, new BigDecimal("150.00"),
                    LocalDate.now().minusDays(8).atStartOfDay(), "2026-2027", "Application fee"));
                StudentFee leahTech = studentFeeRepository.save(new StudentFee(
                    leah, manglonaFamily, rota, feeTypeRepository.findByCode("ELECTRONIC_FEE").orElseThrow(),
                    new BigDecimal("200.00"), LocalDate.now().minusDays(12).atStartOfDay(), "2025-2026",
                    "Technology fee"));

                Payment cruzPayment = paymentRepository.save(new Payment(
                    cruzFamily, PaymentMethod.CASH, new BigDecimal("1800.00"),
                    LocalDate.now().minusDays(10).atStartOfDay(), "RCPT-1001", "Partial tuition payment"));
                cruzPayment.getAllocations().add(new PaymentAllocation(
                    cruzPayment, avaTuition, new BigDecimal("1800.00"), LocalDate.now().minusDays(10).atStartOfDay()));
                paymentRepository.save(cruzPayment);

                Payment santosScholarship = paymentRepository.save(new Payment(
                    santosFamily, PaymentMethod.SCHOLARSHIP_APPLIED, new BigDecimal("100.00"),
                    LocalDate.now().minusDays(4).atStartOfDay(), "SCH-2026-1", "Scholarship applied to application fee"));
                santosScholarship.getAllocations().add(new PaymentAllocation(
                    santosScholarship, micahApplication, new BigDecimal("100.00"), LocalDate.now().minusDays(4).atStartOfDay()));
                paymentRepository.save(santosScholarship);

                Payment leahGrant = paymentRepository.save(new Payment(
                    manglonaFamily, PaymentMethod.CCDF_INHOUSE_GRANT, new BigDecimal("75.00"),
                    LocalDate.now().minusDays(5).atStartOfDay(), "CCDF-55", "CCDF in-house grant"));
                leahGrant.getAllocations().add(new PaymentAllocation(
                    leahGrant, leahTech, new BigDecimal("75.00"), LocalDate.now().minusDays(5).atStartOfDay()));
                paymentRepository.save(leahGrant);
            }
            if (sectionRepository.count() == 0) {
                english = sectionRepository.save(new Section("2025-2026 Spring", "ENG-09", "English 9", "M. Perez", saipan));
                algebra = sectionRepository.save(new Section("2025-2026 Spring", "ALG-1", "Algebra I", "D. Flores", tinian));
                biology = sectionRepository.save(new Section("2025-2026 Spring", "BIO-1", "Biology", "R. Tenorio", rota));
            } else {
                var sections = sectionRepository.findTop10ByOrderByTermNameDescCourseCodeAsc();
                english = sections.stream().filter(section -> "ENG-09".equals(section.getCourseCode())).findFirst().orElseThrow();
                algebra = sections.stream().filter(section -> "ALG-1".equals(section.getCourseCode())).findFirst().orElseThrow();
                biology = sections.stream().filter(section -> "BIO-1".equals(section.getCourseCode())).findFirst().orElseThrow();
            }
            if (attendanceRecordRepository.count() == 0) {
                attendanceRecordRepository.save(new AttendanceRecord(ava, english, LocalDate.now(), AttendanceStatus.PRESENT));
                attendanceRecordRepository.save(new AttendanceRecord(micah, algebra, LocalDate.now(), AttendanceStatus.ABSENT));
                attendanceRecordRepository.save(new AttendanceRecord(leah, biology, LocalDate.now().minusDays(1), AttendanceStatus.EXCUSED));
            }
            if (enrollmentRequestRepository.count() == 0) {
                enrollmentRequestRepository.save(new EnrollmentRequest(
                    cruzFamily, ava, saipan, EnrollmentRequestType.REENROLLMENT, EnrollmentRequestStatus.SUBMITTED,
                    "2026-2027", "Ava", "Marie", "Cruz", "", "Ava", LocalDate.of(2010, 5, 12), "Christian", "Grace Christian Academy Chapel", "Chamorro, American", "", true, "Rarely",
                    cruzFamily.getPrimaryGuardianName(), cruzFamily.getPrimaryGuardianEmail(), cruzFamily.getPrimaryGuardianPhone(),
                    cruzFamily.getMailingAddressLine1(), cruzFamily.getMailingAddressLine2(), cruzFamily.getMailingCity(),
                    cruzFamily.getMailingState(), cruzFamily.getMailingPostalCode(), cruzFamily.getEmployerName(),
                    cruzFamily.getWorkPhone(), cruzFamily.getWorkEmail(), cruzFamily.getWorkAddressLine1(),
                    cruzFamily.getWorkAddressLine2(), cruzFamily.getWorkCity(), cruzFamily.getWorkState(),
                    cruzFamily.getWorkPostalCode(), cruzFamily.getGender(), cruzFamily.getEthnicity(),
                    cruzFamily.getCitizenshipStatus(), cruzFamily.getCountryOfCitizenship(),
                    cruzFamily.isVisaRequired(), cruzFamily.getVisaType(), cruzFamily.getVisaNumber(),
                    cruzFamily.getVisaIssueDate(), cruzFamily.getVisaExpirationDate(),
                    cruzFamily.getMaritalStatus(), cruzFamily.getSecondaryGuardianName(),
                    cruzFamily.getSecondaryGuardianEmail(), cruzFamily.getSecondaryGuardianPhone(),
                    cruzFamily.getSecondaryMailingAddressLine1(), cruzFamily.getSecondaryMailingAddressLine2(),
                    cruzFamily.getSecondaryMailingCity(), cruzFamily.getSecondaryMailingState(),
                    cruzFamily.getSecondaryMailingPostalCode(), cruzFamily.getSecondaryEmployerName(),
                    cruzFamily.getSecondaryWorkPhone(), cruzFamily.getSecondaryWorkEmail(),
                    cruzFamily.getSecondaryWorkAddressLine1(), cruzFamily.getSecondaryWorkAddressLine2(),
                    cruzFamily.getSecondaryWorkCity(), cruzFamily.getSecondaryWorkState(),
                    cruzFamily.getSecondaryWorkPostalCode(), cruzFamily.getSecondaryGender(),
                    cruzFamily.getSecondaryEthnicity(), cruzFamily.getSecondaryCitizenshipStatus(),
                    cruzFamily.getSecondaryCountryOfCitizenship(), cruzFamily.isSecondaryVisaRequired(),
                    cruzFamily.getSecondaryVisaType(), cruzFamily.getSecondaryVisaNumber(),
                    cruzFamily.getSecondaryVisaIssueDate(), cruzFamily.getSecondaryVisaExpirationDate(),
                    cruzFamily.isSecondaryGuardianPortalAccess(), cruzFamily.isPrimaryGuardianBillingRecipient(),
                    "US Citizen", "United States", false, "", "", null, null, false, "",
                    "Saipan Community School", "Saipan", "MP", "United States", "Grade 10",
                    GradeLevel.GRADE_11, LocalDate.now().minusDays(3)));
            }
        };
    }
}
