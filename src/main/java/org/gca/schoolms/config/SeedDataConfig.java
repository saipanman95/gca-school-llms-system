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
import org.gca.schoolms.finance.Invoice;
import org.gca.schoolms.finance.InvoiceRepository;
import org.gca.schoolms.finance.InvoiceStatus;
import org.gca.schoolms.finance.MaritalStatus;
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
                               StudentRepository studentRepository, InvoiceRepository invoiceRepository,
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
                    "Saipan", "MP", "96950", "Female", "CHamoru", MaritalStatus.MARRIED,
                    "David Cruz", "david.cruz@example.org", "670-555-0102", true, true, saipan));
                santosFamily = familyAccountRepository.save(new FamilyAccount("FA-1002", "Santos Family", "Marco Santos",
                    "marco.santos@example.org", "670-555-0202",
                    "45 Harbor Road", "", "Tinian", "MP", "96952",
                    "Marianas Telecom", "670-555-2202", "marco.santos.work@example.org", "88 Commerce Ave", "",
                    "Tinian", "MP", "96952", "Male", "Carolinian", MaritalStatus.MARRIED,
                    "Lia Santos", "lia.santos@example.org", "670-555-0203", true, false, tinian));
                manglonaFamily = familyAccountRepository.save(new FamilyAccount("FA-1003", "Mangi Family", "Rosa Manglona",
                    "rosa.manglona@example.org", "670-555-0303",
                    "12 Sunset Drive", "", "Rota", "MP", "96951",
                    "Rota Clinic", "670-555-3303", "rosa.manglona.work@example.org", "7 Health Center Rd", "",
                    "Rota", "MP", "96951", "Female", "CHamoru", MaritalStatus.WIDOWED,
                    "", "", "", false, true, rota));
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
                ava = studentRepository.save(new Student("2026-001", "Ava", "Cruz", LocalDate.of(2010, 5, 12), GradeLevel.GRADE_10, saipan, cruzFamily, StudentStatus.ACTIVE));
                micah = studentRepository.save(new Student("2026-002", "Micah", "Santos", LocalDate.of(2011, 9, 3), GradeLevel.GRADE_9, tinian, santosFamily, StudentStatus.ACTIVE));
                leah = studentRepository.save(new Student("2025-031", "Leah", "Palomo", LocalDate.of(2008, 12, 14), GradeLevel.GRADE_12, rota, manglonaFamily, StudentStatus.GRADUATED));
            } else {
                var students = studentRepository.findTop10ByOrderByLastNameAscFirstNameAsc();
                ava = students.stream().filter(student -> "2026-001".equals(student.getStudentNumber())).findFirst().orElseThrow();
                micah = students.stream().filter(student -> "2026-002".equals(student.getStudentNumber())).findFirst().orElseThrow();
                leah = students.stream().filter(student -> "2025-031".equals(student.getStudentNumber())).findFirst().orElseThrow();
            }
            Section english = null;
            Section algebra = null;
            Section biology = null;
            if (invoiceRepository.count() == 0) {
                invoiceRepository.save(new Invoice(cruzFamily, "Tuition - Semester 2", new BigDecimal("2400.00"),
                    new BigDecimal("600.00"), InvoiceStatus.PARTIAL, saipan, LocalDate.now().plusDays(10)));
                invoiceRepository.save(new Invoice(santosFamily, "Enrollment and activity fees", new BigDecimal("450.00"),
                    new BigDecimal("450.00"), InvoiceStatus.OPEN, tinian, LocalDate.now().plusDays(5)));
                invoiceRepository.save(new Invoice(manglonaFamily, "Technology fee", new BigDecimal("200.00"),
                    new BigDecimal("200.00"), InvoiceStatus.OPEN, rota, LocalDate.now().plusDays(14)));
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
                    "2026-2027", "Ava", "Cruz",
                    cruzFamily.getPrimaryGuardianName(), cruzFamily.getPrimaryGuardianEmail(), cruzFamily.getPrimaryGuardianPhone(),
                    cruzFamily.getMailingAddressLine1(), cruzFamily.getMailingAddressLine2(), cruzFamily.getMailingCity(),
                    cruzFamily.getMailingState(), cruzFamily.getMailingPostalCode(), cruzFamily.getEmployerName(),
                    cruzFamily.getWorkPhone(), cruzFamily.getWorkEmail(), cruzFamily.getWorkAddressLine1(),
                    cruzFamily.getWorkAddressLine2(), cruzFamily.getWorkCity(), cruzFamily.getWorkState(),
                    cruzFamily.getWorkPostalCode(), cruzFamily.getGender(), cruzFamily.getEthnicity(),
                    cruzFamily.getMaritalStatus(), cruzFamily.getSecondaryGuardianName(),
                    cruzFamily.getSecondaryGuardianEmail(), cruzFamily.getSecondaryGuardianPhone(),
                    cruzFamily.isSecondaryGuardianPortalAccess(), cruzFamily.isPrimaryGuardianBillingRecipient(),
                    GradeLevel.GRADE_11, LocalDate.now().minusDays(3)));
            }
        };
    }
}
