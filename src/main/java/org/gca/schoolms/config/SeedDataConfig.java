package org.gca.schoolms.config;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
import org.gca.schoolms.finance.FeeBillingSchedule;
import org.gca.schoolms.finance.FeeSchedule;
import org.gca.schoolms.finance.FeeScheduleGradeGroup;
import org.gca.schoolms.finance.FeeScheduleItem;
import org.gca.schoolms.finance.FeeScheduleRepository;
import org.gca.schoolms.finance.FeeTypeRepository;
import org.gca.schoolms.finance.MaritalStatus;
import org.gca.schoolms.finance.Payment;
import org.gca.schoolms.finance.PaymentAllocation;
import org.gca.schoolms.finance.PaymentMethod;
import org.gca.schoolms.finance.PayerProfile;
import org.gca.schoolms.finance.PayerProfileRepository;
import org.gca.schoolms.finance.PaymentRepository;
import org.gca.schoolms.finance.SchoolProjectTypeRepository;
import org.gca.schoolms.finance.StudentFee;
import org.gca.schoolms.finance.StudentFeeRepository;
import org.gca.schoolms.integration.powerschool.TeacherCourseStageRepository;
import org.gca.schoolms.integration.powerschool.TeacherCourseStageSeedService;
import org.gca.schoolms.organization.Campus;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.policy.AwardRule;
import org.gca.schoolms.policy.AwardRuleRepository;
import org.gca.schoolms.policy.AwardRuleSet;
import org.gca.schoolms.policy.AwardRuleSetRepository;
import org.gca.schoolms.policy.GradingScaleBand;
import org.gca.schoolms.policy.GradingScaleBandRepository;
import org.gca.schoolms.policy.GradingScaleSet;
import org.gca.schoolms.policy.GradingScaleSetRepository;
import org.gca.schoolms.policy.GradingSpecialMark;
import org.gca.schoolms.policy.GradingSpecialMarkRepository;
import org.gca.schoolms.policy.LegacyGradingScale;
import org.gca.schoolms.policy.LegacyGradingScaleRepository;
import org.gca.schoolms.records.GradeLevel;
import org.gca.schoolms.records.Student;
import org.gca.schoolms.records.StudentRepository;
import org.gca.schoolms.records.StudentStatus;
import org.gca.schoolms.settings.SchoolProfileService;
import org.gca.schoolms.settings.SchoolYear;
import org.gca.schoolms.settings.SchoolYearRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {

    private static FeeType ensureFeeType(FeeTypeRepository repository, String code, String name,
                                         BigDecimal defaultAmount, Integer maxAssessmentsPerStudentPerSchoolYear) {
        return repository.findByCode(code)
            .map(existing -> {
                existing.update(
                    code,
                    name,
                    defaultAmount,
                    existing.getBillingSchedule(),
                    existing.getBillingMonthCount(),
                    maxAssessmentsPerStudentPerSchoolYear
                );
                return repository.save(existing);
            })
            .orElseGet(() -> repository.save(new FeeType(
                code,
                name,
                defaultAmount,
                maxAssessmentsPerStudentPerSchoolYear,
                true
            )));
    }

    private static void ensureSchoolYear(SchoolYearRepository repository, String label, LocalDate startDate,
                                         LocalDate endDate, LocalDate firstDayOfClasses,
                                         LocalDate lastDayOfClasses) {
        repository.findByLabel(label)
            .orElseGet(() -> repository.save(new SchoolYear(
                label,
                startDate,
                endDate,
                firstDayOfClasses,
                lastDayOfClasses
            )));
    }

    private static PayerProfile ensureBillingPayerProfile(PayerProfileRepository repository, FamilyAccount familyAccount) {
        String billingName = familyAccount.getBillingRecipientName();
        String[] parts = billingName.trim().split("\\s+");
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[parts.length - 1] : parts[0];
        String middleName = parts.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length - 1)) : null;
        return repository.findByFamilyAccountAndFirstNameAndLastName(familyAccount, firstName, lastName)
            .orElseGet(() -> repository.save(new PayerProfile(
                firstName,
                middleName,
                lastName,
                null,
                familyAccount.isPrimaryGuardianBillingRecipient() ? familyAccount.getPrimaryGuardianEmail() : familyAccount.getSecondaryGuardianEmail(),
                familyAccount.isPrimaryGuardianBillingRecipient() ? familyAccount.getPrimaryGuardianPhone() : familyAccount.getSecondaryGuardianPhone(),
                familyAccount.isPrimaryGuardianBillingRecipient() ? familyAccount.getMailingAddressLine1() : familyAccount.getSecondaryMailingAddressLine1(),
                familyAccount.isPrimaryGuardianBillingRecipient() ? familyAccount.getMailingAddressLine2() : familyAccount.getSecondaryMailingAddressLine2(),
                familyAccount.isPrimaryGuardianBillingRecipient() ? familyAccount.getMailingCity() : familyAccount.getSecondaryMailingCity(),
                familyAccount.isPrimaryGuardianBillingRecipient() ? familyAccount.getMailingState() : familyAccount.getSecondaryMailingState(),
                familyAccount.isPrimaryGuardianBillingRecipient() ? familyAccount.getMailingPostalCode() : familyAccount.getSecondaryMailingPostalCode(),
                familyAccount
            )));
    }

    private static Student findStudentByNumber(List<Student> students, String studentNumber) {
        return students.stream()
            .filter(student -> studentNumber.equals(student.getStudentNumber()))
            .findFirst()
            .orElse(null);
    }

    private static boolean hasEnrollmentRequestForStudentAndYear(EnrollmentRequestRepository repository, Student student,
                                                                 String schoolYear) {
        return repository.findTopByStudentOrderBySubmittedOnDesc(student)
            .map(request -> schoolYear.equals(request.getSchoolYear()))
            .orElse(false);
    }

    private static GradingScaleSet ensureGradingScaleSet(GradingScaleSetRepository repository, String code, String name,
                                                         LocalDate effectiveStartDate, LocalDate effectiveEndDate) {
        return repository.findByCode(code)
            .orElseGet(() -> repository.save(new GradingScaleSet(code, name, effectiveStartDate, effectiveEndDate, true)));
    }

    private static AwardRuleSet ensureAwardRuleSet(AwardRuleSetRepository repository, String code, String name,
                                                   LocalDate effectiveStartDate, LocalDate effectiveEndDate) {
        return repository.findByCode(code)
            .orElseGet(() -> repository.save(new AwardRuleSet(code, name, effectiveStartDate, effectiveEndDate, true)));
    }

    private static void seedAcademicPolicyData(GradingScaleSetRepository gradingScaleSetRepository,
                                               GradingScaleBandRepository gradingScaleBandRepository,
                                               GradingSpecialMarkRepository gradingSpecialMarkRepository,
                                               LegacyGradingScaleRepository legacyGradingScaleRepository,
                                               AwardRuleSetRepository awardRuleSetRepository,
                                               AwardRuleRepository awardRuleRepository) {
        GradingScaleSet gradingScaleSet = ensureGradingScaleSet(
            gradingScaleSetRepository,
            "GCA_DEFAULT_2025",
            "GCA Default Grading Scale",
            LocalDate.of(2025, 7, 1),
            null
        );
        if (gradingScaleBandRepository.findAllByScaleSetOrderByTrackCodeAscSortOrderAsc(gradingScaleSet).isEmpty()) {
            gradingScaleBandRepository.saveAll(List.of(
                new GradingScaleBand(gradingScaleSet, "STANDARD", "A+", new BigDecimal("99"), new BigDecimal("100"), new BigDecimal("4.00"), 10),
                new GradingScaleBand(gradingScaleSet, "STANDARD", "A", new BigDecimal("95"), new BigDecimal("98"), new BigDecimal("4.00"), 20),
                new GradingScaleBand(gradingScaleSet, "STANDARD", "A-", new BigDecimal("90"), new BigDecimal("94"), new BigDecimal("4.00"), 30),
                new GradingScaleBand(gradingScaleSet, "STANDARD", "B+", new BigDecimal("87"), new BigDecimal("89"), new BigDecimal("3.50"), 40),
                new GradingScaleBand(gradingScaleSet, "STANDARD", "B", new BigDecimal("83"), new BigDecimal("86"), new BigDecimal("3.25"), 50),
                new GradingScaleBand(gradingScaleSet, "STANDARD", "B-", new BigDecimal("80"), new BigDecimal("82"), new BigDecimal("3.00"), 60),
                new GradingScaleBand(gradingScaleSet, "STANDARD", "C+", new BigDecimal("77"), new BigDecimal("79"), new BigDecimal("2.50"), 70),
                new GradingScaleBand(gradingScaleSet, "STANDARD", "C", new BigDecimal("73"), new BigDecimal("76"), new BigDecimal("2.25"), 80),
                new GradingScaleBand(gradingScaleSet, "STANDARD", "C-", new BigDecimal("70"), new BigDecimal("72"), new BigDecimal("2.00"), 90),
                new GradingScaleBand(gradingScaleSet, "STANDARD", "D+", new BigDecimal("67"), new BigDecimal("69"), new BigDecimal("1.50"), 100),
                new GradingScaleBand(gradingScaleSet, "STANDARD", "D", new BigDecimal("64"), new BigDecimal("66"), new BigDecimal("1.25"), 110),
                new GradingScaleBand(gradingScaleSet, "STANDARD", "D-", new BigDecimal("63"), new BigDecimal("63"), new BigDecimal("1.00"), 120),
                new GradingScaleBand(gradingScaleSet, "STANDARD", "F", new BigDecimal("0"), new BigDecimal("62"), new BigDecimal("0.00"), 130),
                new GradingScaleBand(gradingScaleSet, "HONORS", "A+", new BigDecimal("99"), new BigDecimal("105"), new BigDecimal("4.00"), 10),
                new GradingScaleBand(gradingScaleSet, "HONORS", "A", new BigDecimal("95"), new BigDecimal("98"), new BigDecimal("4.00"), 20),
                new GradingScaleBand(gradingScaleSet, "HONORS", "A-", new BigDecimal("90"), new BigDecimal("94"), new BigDecimal("4.00"), 30),
                new GradingScaleBand(gradingScaleSet, "HONORS", "B+", new BigDecimal("87"), new BigDecimal("89"), new BigDecimal("3.50"), 40),
                new GradingScaleBand(gradingScaleSet, "HONORS", "B", new BigDecimal("83"), new BigDecimal("86"), new BigDecimal("3.25"), 50),
                new GradingScaleBand(gradingScaleSet, "HONORS", "B-", new BigDecimal("80"), new BigDecimal("82"), new BigDecimal("3.00"), 60),
                new GradingScaleBand(gradingScaleSet, "HONORS", "C+", new BigDecimal("77"), new BigDecimal("79"), new BigDecimal("2.50"), 70),
                new GradingScaleBand(gradingScaleSet, "HONORS", "C", new BigDecimal("73"), new BigDecimal("76"), new BigDecimal("2.25"), 80),
                new GradingScaleBand(gradingScaleSet, "HONORS", "C-", new BigDecimal("70"), new BigDecimal("72"), new BigDecimal("2.00"), 90),
                new GradingScaleBand(gradingScaleSet, "HONORS", "D", new BigDecimal("64"), new BigDecimal("66"), new BigDecimal("1.25"), 110),
                new GradingScaleBand(gradingScaleSet, "HONORS", "D-", new BigDecimal("63"), new BigDecimal("63"), new BigDecimal("1.00"), 120),
                new GradingScaleBand(gradingScaleSet, "AP", "A+", new BigDecimal("99"), new BigDecimal("110"), new BigDecimal("5.00"), 10),
                new GradingScaleBand(gradingScaleSet, "AP", "A", new BigDecimal("95"), new BigDecimal("98"), new BigDecimal("5.00"), 20),
                new GradingScaleBand(gradingScaleSet, "AP", "A-", new BigDecimal("90"), new BigDecimal("94"), new BigDecimal("5.00"), 30),
                new GradingScaleBand(gradingScaleSet, "AP", "B+", new BigDecimal("87"), new BigDecimal("89"), new BigDecimal("4.50"), 40),
                new GradingScaleBand(gradingScaleSet, "AP", "B", new BigDecimal("83"), new BigDecimal("86"), new BigDecimal("4.25"), 50),
                new GradingScaleBand(gradingScaleSet, "AP", "B-", new BigDecimal("80"), new BigDecimal("82"), new BigDecimal("4.00"), 60),
                new GradingScaleBand(gradingScaleSet, "AP", "C+", new BigDecimal("77"), new BigDecimal("79"), new BigDecimal("3.50"), 70),
                new GradingScaleBand(gradingScaleSet, "AP", "C", new BigDecimal("73"), new BigDecimal("76"), new BigDecimal("3.25"), 80),
                new GradingScaleBand(gradingScaleSet, "AP", "C-", new BigDecimal("70"), new BigDecimal("72"), new BigDecimal("3.00"), 90),
                new GradingScaleBand(gradingScaleSet, "AP", "D+", new BigDecimal("67"), new BigDecimal("69"), new BigDecimal("1.50"), 100),
                new GradingScaleBand(gradingScaleSet, "AP", "D", new BigDecimal("64"), new BigDecimal("66"), new BigDecimal("1.25"), 110),
                new GradingScaleBand(gradingScaleSet, "AP", "D-", new BigDecimal("63"), new BigDecimal("63"), new BigDecimal("1.00"), 120)
            ));
        }
        if (gradingSpecialMarkRepository.findAllByScaleSetOrderBySortOrderAsc(gradingScaleSet).isEmpty()) {
            gradingSpecialMarkRepository.saveAll(List.of(
                new GradingSpecialMark(gradingScaleSet, "P", "Pass", false, true, 10),
                new GradingSpecialMark(gradingScaleSet, "I", "Incomplete", false, false, 20),
                new GradingSpecialMark(gradingScaleSet, "NC", "No Credit", false, false, 30)
            ));
        }
        if (legacyGradingScaleRepository.count() == 0) {
            legacyGradingScaleRepository.saveAll(List.of(
                new LegacyGradingScale("A+", new BigDecimal("99"), new BigDecimal("100"), 10),
                new LegacyGradingScale("A", new BigDecimal("95"), new BigDecimal("98"), 20),
                new LegacyGradingScale("A-", new BigDecimal("90"), new BigDecimal("94"), 30),
                new LegacyGradingScale("B+", new BigDecimal("87"), new BigDecimal("89"), 40),
                new LegacyGradingScale("B", new BigDecimal("83"), new BigDecimal("86"), 50),
                new LegacyGradingScale("B-", new BigDecimal("80"), new BigDecimal("82"), 60),
                new LegacyGradingScale("C+", new BigDecimal("77"), new BigDecimal("79"), 70),
                new LegacyGradingScale("C", new BigDecimal("73"), new BigDecimal("76"), 80),
                new LegacyGradingScale("C-", new BigDecimal("70"), new BigDecimal("72"), 90),
                new LegacyGradingScale("D+", new BigDecimal("67"), new BigDecimal("69"), 100),
                new LegacyGradingScale("D", new BigDecimal("64"), new BigDecimal("66"), 110),
                new LegacyGradingScale("D-", new BigDecimal("63"), new BigDecimal("63"), 120),
                new LegacyGradingScale("F", new BigDecimal("0"), new BigDecimal("62"), 130)
            ));
        }

        AwardRuleSet awardRuleSet = ensureAwardRuleSet(
            awardRuleSetRepository,
            "GCA_ACADEMIC_AWARDS_2025",
            "GCA Academic Awards",
            LocalDate.of(2025, 7, 1),
            null
        );
        if (awardRuleRepository.findAllByRuleSetOrderByAwardCategoryAscNameAsc(awardRuleSet).isEmpty()) {
            awardRuleRepository.saveAll(List.of(
                new AwardRule(awardRuleSet, "VALEDICTORIAN", "Valedictorian", "GRADUATION_RANKING",
                    9, 12, 9, 12, 83, null, 1, 1, true, true, true, true,
                    "Highest cumulative numeric average for grades 9-12 or 3-5 with GCA attendance across the required years."),
                new AwardRule(awardRuleSet, "SALUTATORIAN", "Salutatorian", "GRADUATION_RANKING",
                    9, 12, 9, 12, 83, null, 2, 2, true, true, true, true,
                    "Second highest cumulative numeric average for grades 9-12 or 3-5 with GCA attendance across the required years."),
                new AwardRule(awardRuleSet, "GRADUATING_WITH_HONORS_3", "3rd Graduating With Honors", "GRADUATION_RANKING",
                    9, 12, 10, 12, null, null, 3, 3, true, false, false, true,
                    "Third highest numeric average. Elementary note in source document says attended GCA from 3rd through 5th grade."),
                new AwardRule(awardRuleSet, "GRADUATING_WITH_HONORS_4", "4th Graduating With Honors", "GRADUATION_RANKING",
                    9, 12, 10, 12, null, null, 4, 4, true, false, false, true,
                    "Fourth highest numeric average. Elementary note in source document says attended GCA from 3rd through 5th grade."),
                new AwardRule(awardRuleSet, "GRADUATING_WITH_HONORS_5", "5th Graduating With Honors", "GRADUATION_RANKING",
                    9, 12, 10, 12, null, null, 5, 5, true, false, false, true,
                    "Fifth highest numeric average. Elementary note in source document says attended GCA from 3rd through 5th grade."),
                new AwardRule(awardRuleSet, "CONSISTENT_PRINCIPALS_LIST", "Consistent Principal's List", "CONSISTENCY",
                    null, null, null, null, 95, null, null, null, false, false, true, true,
                    "A average in all subjects with no numeric grade below 95 per quarter per year."),
                new AwardRule(awardRuleSet, "CONSISTENT_HONOR_ROLL", "Consistent Honor Roll", "CONSISTENCY",
                    null, null, null, null, 80, null, null, null, false, false, true, true,
                    "A or B average in all subjects with no numeric grade below 80 per quarter per school year."),
                new AwardRule(awardRuleSet, "CONSISTENT_HIGH_GPA_GRADE_5", "Consistent High GPA Grade 5", "CONSISTENCY",
                    3, 5, 4, 5, null, new BigDecimal("3.50"), null, null, false, false, false, true,
                    "5th grade students maintaining GPA 3.5 for the present year and two previous years, with GCA attendance for grades 4 and 5."),
                new AwardRule(awardRuleSet, "CONSISTENT_HIGH_GPA_GRADE_12", "Consistent High GPA Grade 12", "CONSISTENCY",
                    9, 12, 11, 12, null, new BigDecimal("3.50"), null, null, false, false, false, true,
                    "12th grade students maintaining GPA 3.5 for grades 9-12, with GCA attendance for grades 11 and 12."),
                new AwardRule(awardRuleSet, "A_HONOR_ROLL", "A Honor Roll", "QUARTERLY_CERTIFICATE",
                    null, null, null, null, 90, null, null, null, false, false, true, false,
                    "Quarter certificate rule: no grade below 90."),
                new AwardRule(awardRuleSet, "B_HONOR_ROLL", "B Honor Roll", "QUARTERLY_CERTIFICATE",
                    null, null, null, null, 80, null, null, null, false, false, true, false,
                    "Quarter certificate rule: no grade below 80.")
            ));
        }
    }

    private static void seedTeacherCourseStage(TeacherCourseStageRepository repository,
                                               TeacherCourseStageSeedService seedService) {
        if (repository.count() > 0) {
            return;
        }
        try {
            repository.saveAll(seedService.loadSeedRows());
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to seed teacher course stage data", exception);
        }
    }

    @Bean
    CommandLineRunner seedData(CampusRepository campusRepository, FamilyAccountRepository familyAccountRepository,
                               StudentRepository studentRepository, FeeTypeRepository feeTypeRepository,
                               FeeScheduleRepository feeScheduleRepository,
                               StudentFeeRepository studentFeeRepository, PaymentRepository paymentRepository,
                               PayerProfileRepository payerProfileRepository,
                               SchoolProjectTypeRepository schoolProjectTypeRepository,
                               SchoolYearRepository schoolYearRepository,
                               SchoolProfileService schoolProfileService,
                               SectionRepository sectionRepository, AttendanceRecordRepository attendanceRecordRepository,
                               EnrollmentRequestRepository enrollmentRequestRepository,
                               GradingScaleSetRepository gradingScaleSetRepository,
                               GradingScaleBandRepository gradingScaleBandRepository,
                               GradingSpecialMarkRepository gradingSpecialMarkRepository,
                               LegacyGradingScaleRepository legacyGradingScaleRepository,
                               AwardRuleSetRepository awardRuleSetRepository,
                               AwardRuleRepository awardRuleRepository,
                               TeacherCourseStageRepository teacherCourseStageRepository,
                               TeacherCourseStageSeedService teacherCourseStageSeedService,
                               @Value("${app.seed-demo-data:true}") boolean seedDemoData) {
        return args -> {
            schoolProfileService.ensureDefaultProfile();
            seedAcademicPolicyData(
                gradingScaleSetRepository,
                gradingScaleBandRepository,
                gradingSpecialMarkRepository,
                legacyGradingScaleRepository,
                awardRuleSetRepository,
                awardRuleRepository
            );
            seedTeacherCourseStage(teacherCourseStageRepository, teacherCourseStageSeedService);
            if (!seedDemoData) {
                return;
            }
            for (int startYear = 2018; startYear <= 2027; startYear++) {
                String label = startYear + "-" + (startYear + 1);
                ensureSchoolYear(
                    schoolYearRepository,
                    label,
                    LocalDate.of(startYear, 7, 1),
                    LocalDate.of(startYear + 1, 6, 30),
                    LocalDate.of(startYear, 8, 15),
                    LocalDate.of(startYear + 1, 5, 31)
                );
            }
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
            PayerProfile cruzPayer = ensureBillingPayerProfile(payerProfileRepository, cruzFamily);
            PayerProfile santosPayer = ensureBillingPayerProfile(payerProfileRepository, santosFamily);
            PayerProfile manglonaPayer = ensureBillingPayerProfile(payerProfileRepository, manglonaFamily);

            Student ava = null;
            Student isaac = null;
            Student noelle = null;
            Student micah = null;
            Student leah = null;
            if (studentRepository.count() == 0) {
                ava = studentRepository.save(new Student("2026-001", "Ava", "Marie", "Cruz", "", "Ava", LocalDate.of(2010, 5, 12), GradeLevel.GRADE_10, saipan, cruzFamily, StudentStatus.ACTIVE));
                isaac = studentRepository.save(new Student("2026-003", "Isaac", "Daniel", "Cruz", "", "Isaac", LocalDate.of(2015, 8, 21), GradeLevel.GRADE_5, saipan, cruzFamily, StudentStatus.ACTIVE));
                noelle = studentRepository.save(new Student("2026-004", "Noelle", "Grace", "Cruz", "", "Ellie", LocalDate.of(2018, 2, 9), GradeLevel.GRADE_2, saipan, cruzFamily, StudentStatus.ACTIVE));
                micah = studentRepository.save(new Student("2026-002", "Micah", "", "Santos", "", "Micah", LocalDate.of(2011, 9, 3), GradeLevel.GRADE_9, tinian, santosFamily, StudentStatus.ACTIVE));
                leah = studentRepository.save(new Student("2025-031", "Leah", "", "Palomo", "", "", LocalDate.of(2008, 12, 14), GradeLevel.GRADE_12, rota, manglonaFamily, StudentStatus.GRADUATED));
            } else {
                var students = studentRepository.findAll();
                ava = findStudentByNumber(students, "2026-001");
                if (ava == null) {
                    ava = studentRepository.save(new Student("2026-001", "Ava", "Marie", "Cruz", "", "Ava", LocalDate.of(2010, 5, 12), GradeLevel.GRADE_10, saipan, cruzFamily, StudentStatus.ACTIVE));
                }
                isaac = findStudentByNumber(students, "2026-003");
                if (isaac == null) {
                    isaac = studentRepository.save(new Student("2026-003", "Isaac", "Daniel", "Cruz", "", "Isaac", LocalDate.of(2015, 8, 21), GradeLevel.GRADE_5, saipan, cruzFamily, StudentStatus.ACTIVE));
                }
                noelle = findStudentByNumber(students, "2026-004");
                if (noelle == null) {
                    noelle = studentRepository.save(new Student("2026-004", "Noelle", "Grace", "Cruz", "", "Ellie", LocalDate.of(2018, 2, 9), GradeLevel.GRADE_2, saipan, cruzFamily, StudentStatus.ACTIVE));
                }
                micah = findStudentByNumber(students, "2026-002");
                if (micah == null) {
                    micah = studentRepository.save(new Student("2026-002", "Micah", "", "Santos", "", "Micah", LocalDate.of(2011, 9, 3), GradeLevel.GRADE_9, tinian, santosFamily, StudentStatus.ACTIVE));
                }
                leah = findStudentByNumber(students, "2025-031");
                if (leah == null) {
                    leah = studentRepository.save(new Student("2025-031", "Leah", "", "Palomo", "", "", LocalDate.of(2008, 12, 14), GradeLevel.GRADE_12, rota, manglonaFamily, StudentStatus.GRADUATED));
                }
            }
            Section english = null;
            Section algebra = null;
            Section biology = null;
            FeeType enrollmentFeeType = ensureFeeType(
                feeTypeRepository, "ENROLLMENT_FEE", "Enrollment fee", new BigDecimal("300.00"), 1);
            FeeType applicationFeeType = ensureFeeType(
                feeTypeRepository, "APPLICATION_FEE", "Application fee", new BigDecimal("150.00"), 1);
            FeeType tuitionFeeType = ensureFeeType(
                feeTypeRepository, "TUITION_FEE", "Tuition fee", null, 1);
            tuitionFeeType.update(
                tuitionFeeType.getCode(),
                tuitionFeeType.getName(),
                tuitionFeeType.getDefaultAmount(),
                FeeBillingSchedule.MONTHLY,
                10,
                tuitionFeeType.getMaxAssessmentsPerStudentPerSchoolYear()
            );
            feeTypeRepository.save(tuitionFeeType);
            ensureFeeType(feeTypeRepository, "BOOK_FEE", "Book fee", null, 1);
            ensureFeeType(feeTypeRepository, "UNIFORM_FEE", "Uniform fee", null, 1);
            ensureFeeType(feeTypeRepository, "COMPUTER_LAB_FEE", "Computer lab fee", null, 1);
            ensureFeeType(feeTypeRepository, "ELECTRONIC_FEE", "Electronic fee", null, 1);
            ensureFeeType(feeTypeRepository, "LUNCH_FEE", "Lunch fee", null, null);
            ensureFeeType(feeTypeRepository, "AFTERSCHOOL_FEE", "Afterschool fee", null, null);
            if (feeScheduleRepository.count() == 0) {
                FeeType tuition = feeTypeRepository.findByCode("TUITION_FEE").orElseThrow();
                FeeType books = feeTypeRepository.findByCode("BOOK_FEE").orElseThrow();
                FeeType electronic = feeTypeRepository.findByCode("ELECTRONIC_FEE").orElseThrow();

                FeeSchedule elementarySchedule = feeScheduleRepository.save(
                    new FeeSchedule("Standard package", "2026-2027", FeeScheduleGradeGroup.ELEMENTARY, saipan, true)
                );
                elementarySchedule.getItems().add(new FeeScheduleItem(
                    elementarySchedule, tuition, new BigDecimal("355.00"), "Elementary monthly tuition", 1
                ));
                elementarySchedule.getItems().add(new FeeScheduleItem(
                    elementarySchedule, books, new BigDecimal("125.00"), "Elementary book fee", 2
                ));
                feeScheduleRepository.save(elementarySchedule);

                FeeSchedule juniorHighSchedule = feeScheduleRepository.save(
                    new FeeSchedule("Standard package", "2026-2027", FeeScheduleGradeGroup.JUNIOR_HIGH, saipan, true)
                );
                juniorHighSchedule.getItems().add(new FeeScheduleItem(
                    juniorHighSchedule, tuition, new BigDecimal("425.00"), "Junior High monthly tuition", 1
                ));
                juniorHighSchedule.getItems().add(new FeeScheduleItem(
                    juniorHighSchedule, electronic, new BigDecimal("200.00"), "Junior High technology fee", 2
                ));
                feeScheduleRepository.save(juniorHighSchedule);

                FeeSchedule highSchoolSchedule = feeScheduleRepository.save(
                    new FeeSchedule("Standard package", "2026-2027", FeeScheduleGradeGroup.HIGH_SCHOOL, saipan, true)
                );
                highSchoolSchedule.getItems().add(new FeeScheduleItem(
                    highSchoolSchedule, tuition, new BigDecimal("455.00"), "High School monthly tuition", 1
                ));
                highSchoolSchedule.getItems().add(new FeeScheduleItem(
                    highSchoolSchedule, electronic, new BigDecimal("250.00"), "High School technology fee", 2
                ));
                feeScheduleRepository.save(highSchoolSchedule);
            }
            schoolProjectTypeRepository.findByCode("GENERAL")
                .orElseGet(() -> schoolProjectTypeRepository.save(new org.gca.schoolms.finance.SchoolProjectType("GENERAL", "General", true)));
            schoolProjectTypeRepository.findByCode("BUILDING")
                .orElseGet(() -> schoolProjectTypeRepository.save(new org.gca.schoolms.finance.SchoolProjectType("BUILDING", "Building", true)));
            schoolProjectTypeRepository.findByCode("SENIOR_TRIP")
                .orElseGet(() -> schoolProjectTypeRepository.save(new org.gca.schoolms.finance.SchoolProjectType("SENIOR_TRIP", "Senior Trip", true)));
            schoolProjectTypeRepository.findByCode("TEACHER_LUNCHEONS")
                .orElseGet(() -> schoolProjectTypeRepository.save(new org.gca.schoolms.finance.SchoolProjectType("TEACHER_LUNCHEONS", "Teacher Luncheons", true)));
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
                    cruzPayer,
                    cruzFamily, PaymentMethod.CASH, new BigDecimal("1800.00"),
                    LocalDate.now().minusDays(10).atStartOfDay(), "SYSTEM", "RCPT-1001", "Partial tuition payment"));
                cruzPayment.getAllocations().add(new PaymentAllocation(
                    cruzPayment, avaTuition, new BigDecimal("1800.00"), LocalDate.now().minusDays(10).atStartOfDay()));
                paymentRepository.save(cruzPayment);

                Payment santosScholarship = paymentRepository.save(new Payment(
                    santosPayer,
                    santosFamily, PaymentMethod.SCHOLARSHIP_APPLIED, new BigDecimal("100.00"),
                    LocalDate.now().minusDays(4).atStartOfDay(), "SYSTEM", "SCH-2026-1", "Scholarship applied to application fee"));
                santosScholarship.getAllocations().add(new PaymentAllocation(
                    santosScholarship, micahApplication, new BigDecimal("100.00"), LocalDate.now().minusDays(4).atStartOfDay()));
                paymentRepository.save(santosScholarship);

                Payment leahGrant = paymentRepository.save(new Payment(
                    manglonaPayer,
                    manglonaFamily, PaymentMethod.CCDF_INHOUSE_GRANT, new BigDecimal("75.00"),
                    LocalDate.now().minusDays(5).atStartOfDay(), "SYSTEM", "CCDF-55", "CCDF in-house grant"));
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
            if (!hasEnrollmentRequestForStudentAndYear(enrollmentRequestRepository, isaac, "2026-2027")) {
                enrollmentRequestRepository.save(new EnrollmentRequest(
                    cruzFamily, isaac, saipan, EnrollmentRequestType.REENROLLMENT, EnrollmentRequestStatus.READY_FOR_FINANCE,
                    "2026-2027", "Isaac", "Daniel", "Cruz", "", "Isaac", LocalDate.of(2015, 8, 21), "Christian", "Grace Christian Academy Chapel", "Chamorro, American", "", false, "",
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
                    "", "", "", "", "",
                    GradeLevel.GRADE_6, LocalDate.now().minusDays(2)));
            }
            if (enrollmentRequestRepository.findByFamilyAccountOrderBySubmittedOnDesc(cruzFamily).stream()
                .noneMatch(request -> request.getStudent() == null
                    && request.getFamilyAccount().getAccountNumber().equals("FA-1001")
                    && "Caleb".equals(request.getStudentFirstName())
                    && "2026-2027".equals(request.getSchoolYear()))) {
                enrollmentRequestRepository.save(new EnrollmentRequest(
                    cruzFamily, null, saipan, EnrollmentRequestType.NEW_STUDENT, EnrollmentRequestStatus.DRAFT,
                    "2026-2027", "Caleb", "Joseph", "Cruz", "", "Caleb", LocalDate.of(2021, 11, 4), "Christian", "Grace Christian Academy Chapel", "Chamorro", "", true, "Occasional",
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
                    "Bright Start Preschool", "Saipan", "MP", "United States", "Pre-K",
                    GradeLevel.K5, LocalDate.now().minusDays(1)));
            }
        };
    }
}
