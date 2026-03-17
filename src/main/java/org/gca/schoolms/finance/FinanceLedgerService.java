package org.gca.schoolms.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.gca.schoolms.common.MoneyFormatter;
import org.gca.schoolms.enrollment.EnrollmentRequest;
import org.gca.schoolms.enrollment.EnrollmentRequestType;
import org.gca.schoolms.records.Student;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.records.StudentRepository;
import org.gca.schoolms.settings.SchoolProfileService;
import org.gca.schoolms.settings.SchoolProfileView;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinanceLedgerService {

    private final FeeTypeRepository feeTypeRepository;
    private final StudentFeeRepository studentFeeRepository;
    private final PaymentRepository paymentRepository;
    private final PayerProfileRepository payerProfileRepository;
    private final SchoolProjectTypeRepository schoolProjectTypeRepository;
    private final FamilyAccountRepository familyAccountRepository;
    private final StudentRepository studentRepository;
    private final CampusRepository campusRepository;
    private final SchoolProfileService schoolProfileService;
    private final JavaMailSender mailSender;
    private final MoneyFormatter moneyFormatter;

    public FinanceLedgerService(FeeTypeRepository feeTypeRepository, StudentFeeRepository studentFeeRepository,
                                PaymentRepository paymentRepository, PayerProfileRepository payerProfileRepository,
                                SchoolProjectTypeRepository schoolProjectTypeRepository,
                                FamilyAccountRepository familyAccountRepository, StudentRepository studentRepository,
                                CampusRepository campusRepository, SchoolProfileService schoolProfileService,
                                ObjectProvider<JavaMailSender> mailSenderProvider, MoneyFormatter moneyFormatter) {
        this.feeTypeRepository = feeTypeRepository;
        this.studentFeeRepository = studentFeeRepository;
        this.paymentRepository = paymentRepository;
        this.payerProfileRepository = payerProfileRepository;
        this.schoolProjectTypeRepository = schoolProjectTypeRepository;
        this.familyAccountRepository = familyAccountRepository;
        this.studentRepository = studentRepository;
        this.campusRepository = campusRepository;
        this.schoolProfileService = schoolProfileService;
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.moneyFormatter = moneyFormatter;
    }

    @Transactional(readOnly = true)
    public BigDecimal totalOutstandingBalance() {
        BigDecimal feeOutstanding = studentFeeRepository.findAll().stream()
            .map(StudentFee::getOutstandingAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal unappliedCredits = paymentRepository.findAll().stream()
            .map(Payment::getUnappliedAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return feeOutstanding.subtract(unappliedCredits);
    }

    @Transactional(readOnly = true)
    public BigDecimal outstandingBalanceForFamily(FamilyAccount familyAccount) {
        BigDecimal feeOutstanding = studentFeeRepository.findByFamilyAccountOrderByAssessedAtDescIdDesc(familyAccount).stream()
            .map(StudentFee::getOutstandingAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal unappliedCredits = paymentRepository.findByFamilyAccountOrderByPaymentDateDescIdDesc(familyAccount).stream()
            .map(Payment::getUnappliedAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return feeOutstanding.subtract(unappliedCredits);
    }

    @Transactional(readOnly = true)
    public long openChargeCount() {
        return studentFeeRepository.findAll().stream()
            .filter(fee -> fee.getOutstandingAmount().compareTo(BigDecimal.ZERO) > 0)
            .count();
    }

    @Transactional(readOnly = true)
    public List<FeeType> feeTypes() {
        return feeTypeRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public FeeTypeForm buildFeeTypeForm(Long feeTypeId) {
        FeeTypeForm form = new FeeTypeForm();
        if (feeTypeId == null) {
            return form;
        }
        FeeType feeType = feeTypeRepository.findById(feeTypeId).orElseThrow();
        form.setId(feeType.getId());
        form.setCode(feeType.getCode());
        form.setName(feeType.getName());
        form.setDefaultAmount(feeType.getDefaultAmount());
        form.setMaxAssessmentsPerStudentPerSchoolYear(feeType.getMaxAssessmentsPerStudentPerSchoolYear());
        form.setActive(feeType.isActive());
        return form;
    }

    @Transactional(readOnly = true)
    public List<StudentFee> recentFees() {
        return studentFeeRepository.findTop20ByOrderByAssessedAtDescIdDesc();
    }

    @Transactional(readOnly = true)
    public List<SchoolProjectType> schoolProjectTypes() {
        return schoolProjectTypeRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Payment> recentPayments() {
        return paymentRepository.findTop20ByOrderByPaymentDateDescIdDesc();
    }

    @Transactional(readOnly = true)
    public List<PaymentRowView> recentPaymentRows() {
        return paymentRepository.findTop20ByOrderByPaymentDateDescIdDesc().stream()
            .map(this::toPaymentRowView)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentFee> openFeesForPaymentPosting() {
        return studentFeeRepository.findAllByOrderByAssessedAtAscIdAsc().stream()
            .filter(fee -> fee.getOutstandingAmount().compareTo(BigDecimal.ZERO) > 0)
            .toList();
    }

    @Transactional
    public List<FinanceStudentPaymentOption> paymentStudentOptions() {
        return studentRepository.findAllByOrderByLastNameAscFirstNameAsc().stream()
            .map(student -> {
                PayerProfile billingPayer = ensureBillingPayerProfile(student.getFamilyAccount());
                return new FinanceStudentPaymentOption(
                    student.getId(),
                    student.getFamilyAccount().getId(),
                    billingPayer.getId(),
                    billingPayer.getLookupLabel(),
                    student.getLastName() + ", " + student.getFirstName()
                        + " (" + student.getStudentNumber() + " / "
                        + student.getFamilyAccount().getAccountName() + ")"
                );
            })
            .toList();
    }

    @Transactional
    public List<PayerLookupOption> payerOptions() {
        familyAccountRepository.findAllByOrderByPrimaryGuardianNameAsc()
            .forEach(this::ensureBillingPayerProfile);
        return payerProfileRepository.findAllByOrderByLastNameAscFirstNameAsc().stream()
            .map(profile -> new PayerLookupOption(
                profile.getId(),
                profile.getFamilyAccount() != null ? profile.getFamilyAccount().getId() : null,
                profile.getLookupLabel()
            ))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentFee> feesForFamily(FamilyAccount familyAccount) {
        return studentFeeRepository.findByFamilyAccountOrderByAssessedAtDescIdDesc(familyAccount);
    }

    @Transactional(readOnly = true)
    public List<Payment> paymentsForFamily(FamilyAccount familyAccount) {
        return paymentRepository.findByFamilyAccountOrderByPaymentDateDescIdDesc(familyAccount);
    }

    @Transactional(readOnly = true)
    public List<PaymentRowView> paymentRowsForFamily(FamilyAccount familyAccount) {
        return paymentRepository.findByFamilyAccountOrderByPaymentDateDescIdDesc(familyAccount).stream()
            .map(this::toPaymentRowView)
            .toList();
    }

    @Transactional
    public void createFeeType(String code, String name) {
        createFeeType(code, name, null, null);
    }

    @Transactional
    public void createFeeType(String code, String name, BigDecimal defaultAmount) {
        createFeeType(code, name, defaultAmount, null);
    }

    @Transactional
    public void createFeeType(String code, String name, BigDecimal defaultAmount,
                              Integer maxAssessmentsPerStudentPerSchoolYear) {
        feeTypeRepository.save(new FeeType(
            code.trim().toUpperCase(Locale.ROOT),
            name.trim(),
            defaultAmount,
            normalizeAssessmentLimit(maxAssessmentsPerStudentPerSchoolYear),
            true
        ));
    }

    @Transactional
    public void updateFeeType(Long id, String code, String name, BigDecimal defaultAmount,
                              Integer maxAssessmentsPerStudentPerSchoolYear, boolean active) {
        FeeType feeType = feeTypeRepository.findById(id).orElseThrow();
        String normalizedCode = code.trim().toUpperCase(Locale.ROOT);
        if (!feeType.getCode().equals(normalizedCode) && studentFeeRepository.countByFeeType(feeType) > 0) {
            throw new IllegalArgumentException(
                "Fee codes cannot be changed after the fee type has been used. Deactivate it and create a new fee type instead."
            );
        }
        feeType.update(
            normalizedCode,
            name.trim(),
            defaultAmount,
            normalizeAssessmentLimit(maxAssessmentsPerStudentPerSchoolYear)
        );
        feeType.setActive(active);
        feeTypeRepository.save(feeType);
    }

    @Transactional
    public void createSchoolProjectType(String code, String name) {
        schoolProjectTypeRepository.save(new SchoolProjectType(code.trim().toUpperCase(), name.trim(), true));
    }

    @Transactional
    public void assessFee(Long studentId, Long feeTypeId, BigDecimal amount, String schoolYear, String description) {
        var student = studentRepository.findById(studentId).orElseThrow();
        var feeType = feeTypeRepository.findById(feeTypeId).orElseThrow();
        if (!feeType.isActive()) {
            throw new IllegalArgumentException("Inactive fee types cannot be assessed. Reactivate the fee type or choose another one.");
        }
        String normalizedSchoolYear = schoolYear.trim();
        enforceAssessmentLimit(student, feeType, normalizedSchoolYear);
        studentFeeRepository.save(new StudentFee(
            student,
            student.getFamilyAccount(),
            campusRepository.findById(student.getCampus().getId()).orElseThrow(),
            feeType,
            amount,
            LocalDateTime.now(),
            normalizedSchoolYear,
            description == null || description.isBlank() ? feeType.getName() : description.trim()
        ));
    }

    @Transactional
    public void ensureEnrollmentRequestFee(EnrollmentRequest enrollmentRequest) {
        if (studentFeeRepository.findByEnrollmentRequest(enrollmentRequest).isPresent()) {
            return;
        }
        FeeType feeType = switch (enrollmentRequest.getRequestType()) {
            case NEW_STUDENT -> feeTypeRepository.findByCode("APPLICATION_FEE").orElseThrow();
            case REENROLLMENT -> feeTypeRepository.findByCode("ENROLLMENT_FEE").orElseThrow();
        };
        BigDecimal amount = feeType.getDefaultAmount() == null ? BigDecimal.ZERO : feeType.getDefaultAmount();
        String description = switch (enrollmentRequest.getRequestType()) {
            case NEW_STUDENT -> "Application fee for " + enrollmentRequest.getSchoolYear();
            case REENROLLMENT -> "Enrollment fee for " + enrollmentRequest.getSchoolYear();
        };
        studentFeeRepository.save(new StudentFee(
            enrollmentRequest.getStudent(),
            enrollmentRequest.getFamilyAccount(),
            campusRepository.findById(enrollmentRequest.getCampus().getId()).orElseThrow(),
            feeType,
            amount,
            LocalDateTime.now(),
            enrollmentRequest.getSchoolYear(),
            description,
            enrollmentRequest
        ));
    }

    private Integer normalizeAssessmentLimit(Integer maxAssessmentsPerStudentPerSchoolYear) {
        if (maxAssessmentsPerStudentPerSchoolYear == null || maxAssessmentsPerStudentPerSchoolYear < 1) {
            return null;
        }
        return maxAssessmentsPerStudentPerSchoolYear;
    }

    private void enforceAssessmentLimit(Student student, FeeType feeType, String schoolYear) {
        Integer assessmentLimit = feeType.getMaxAssessmentsPerStudentPerSchoolYear();
        if (assessmentLimit == null) {
            return;
        }
        long existingCount = studentFeeRepository.countByStudentAndFeeTypeAndSchoolYearAndStatus(
            student, feeType, schoolYear, StudentFeeStatus.ACTIVE);
        if (existingCount >= assessmentLimit) {
            throw new IllegalArgumentException(
                feeType.getName() + " can only be assessed " + assessmentLimit
                    + " time(s) for " + student.getDisplayName() + " in " + schoolYear + "."
            );
        }
    }

    @Transactional
    public void cancelFee(Long studentFeeId, String cancellationReason, String cancelledByUserId) {
        StudentFee studentFee = studentFeeRepository.findById(studentFeeId).orElseThrow();
        if (!studentFee.isCancelable()) {
            throw new IllegalArgumentException("Only active fees with no payments applied can be cancelled.");
        }
        if (cancellationReason == null || cancellationReason.isBlank()) {
            throw new IllegalArgumentException("A cancellation reason is required.");
        }
        studentFee.cancel(normalizeReceiverId(cancelledByUserId), cancellationReason.trim(), LocalDateTime.now());
        studentFeeRepository.save(studentFee);
    }

    @Transactional
    public Long recordPayment(PaymentPurpose paymentPurpose, Long studentId, Long payerProfileId,
                              Long schoolProjectTypeId, boolean crossFamilyConfirmed, PaymentMethod paymentMethod,
                              BigDecimal amount, String referenceNumber, String notes, boolean anonymousToFamily,
                              String receivedByUserId) {
        if (payerProfileId == null) {
            throw new IllegalArgumentException("A payer must be selected from the lookup list.");
        }
        PayerProfile payerProfile = payerProfileRepository.findById(payerProfileId).orElseThrow();
        FamilyAccount payerFamilyAccount = payerProfile.getFamilyAccount();
        Student targetStudent = studentId == null ? null : studentRepository.findById(studentId).orElseThrow();
        SchoolProjectType schoolProjectType = schoolProjectTypeId == null ? null : schoolProjectTypeRepository.findById(schoolProjectTypeId).orElseThrow();
        if (paymentPurpose == PaymentPurpose.STUDENT_ACCOUNT) {
            if (targetStudent == null) {
                throw new IllegalArgumentException("A student must be selected for student payments.");
            }
            boolean sameFamily = payerFamilyAccount != null && targetStudent.getFamilyAccount().getId().equals(payerFamilyAccount.getId());
            if (!sameFamily && !crossFamilyConfirmed) {
                throw new IllegalArgumentException("Cross-family student payments require confirmation.");
            }
            FamilyAccount creditedFamilyAccount = targetStudent.getFamilyAccount();
            var payment = paymentRepository.save(new Payment(
                payerProfile,
                creditedFamilyAccount,
                paymentPurpose,
                targetStudent,
                null,
                paymentMethod,
                amount,
                LocalDateTime.now(),
                normalizeReceiverId(receivedByUserId),
                referenceNumber == null || referenceNumber.isBlank() ? null : referenceNumber.trim(),
                notes == null || notes.isBlank() ? null : notes.trim(),
                anonymousToFamily
            ));
            BigDecimal remainingAmount = amount;
            for (StudentFee studentFee : studentFeeRepository.findByStudentOrderByAssessedAtAscIdAsc(targetStudent)) {
                if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
                BigDecimal outstandingAmount = studentFee.getOutstandingAmount();
                if (outstandingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                BigDecimal amountApplied = remainingAmount.min(outstandingAmount);
                payment.getAllocations().add(new PaymentAllocation(payment, studentFee, amountApplied, LocalDateTime.now()));
                remainingAmount = remainingAmount.subtract(amountApplied);
            }
            paymentRepository.save(payment);
            return payment.getId();
        }
        if (paymentPurpose == PaymentPurpose.GIFT_TO_SCHOOL && schoolProjectType == null) {
            throw new IllegalArgumentException("Select a school project type for gifts to the school.");
        }
        Payment payment = paymentRepository.save(new Payment(
            payerProfile,
            payerFamilyAccount,
            paymentPurpose,
            targetStudent,
            schoolProjectType,
            paymentMethod,
            amount,
            LocalDateTime.now(),
            normalizeReceiverId(receivedByUserId),
            referenceNumber == null || referenceNumber.isBlank() ? null : referenceNumber.trim(),
            notes == null || notes.isBlank() ? null : notes.trim(),
            anonymousToFamily
        ));
        return payment.getId();
    }

    @Transactional
    public Long createPayerProfile(PayerProfileForm form) {
        PayerProfile payerProfile = payerProfileRepository.save(new PayerProfile(
            form.getFirstName().trim(),
            blankToNull(form.getMiddleName()),
            form.getLastName().trim(),
            blankToNull(form.getBusinessName()),
            blankToNull(form.getEmailAddress()),
            blankToNull(form.getPhoneNumber()),
            blankToNull(form.getMailingAddressLine1()),
            blankToNull(form.getMailingAddressLine2()),
            blankToNull(form.getMailingCity()),
            blankToNull(form.getMailingState()),
            blankToNull(form.getMailingPostalCode()),
            null
        ));
        return payerProfile.getId();
    }

    @Transactional(readOnly = true)
    public PayerLookupOption loadPayerOption(Long payerProfileId) {
        return payerProfileRepository.findById(payerProfileId)
            .map(profile -> new PayerLookupOption(
                profile.getId(),
                profile.getFamilyAccount() != null ? profile.getFamilyAccount().getId() : null,
                profile.getLookupLabel()
            ))
            .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<FamilyAccount> familyAccounts() {
        return familyAccountRepository.findTop10ByOrderByAccountNameAsc();
    }

    @Transactional(readOnly = true)
    public FinanceHomeView loadFinanceHome(long pendingClearanceCount) {
        long openChargeCount = openChargeCount();
        long outstandingFamilyCount = familyAccountRepository.findAllByOrderByPrimaryGuardianNameAsc().stream()
            .filter(account -> outstandingBalanceForFamily(account).compareTo(BigDecimal.ZERO) > 0)
            .count();
        return new FinanceHomeView(
            totalOutstandingBalance(),
            openChargeCount,
            paymentRepository.count(),
            pendingClearanceCount,
            outstandingFamilyCount,
            feeTypeRepository.count()
        );
    }

    @Transactional(readOnly = true)
    public List<FinanceOutstandingAccountRow> outstandingAccounts() {
        return familyAccountRepository.findAllByOrderByPrimaryGuardianNameAsc().stream()
            .map(account -> {
                BigDecimal outstandingBalance = outstandingBalanceForFamily(account);
                long openChargeCount = feesForFamily(account).stream()
                    .filter(fee -> fee.getOutstandingAmount().compareTo(BigDecimal.ZERO) > 0)
                    .count();
                return new FinanceOutstandingAccountRow(
                    account.getId(),
                    account.getAccountNumber(),
                    account.getAccountName(),
                    account.getPrimaryGuardianName(),
                    account.getPrimaryGuardianPhone(),
                    account.getPrimaryGuardianEmail(),
                    account.getCampus().getCode(),
                    outstandingBalance,
                    openChargeCount
                );
            })
            .filter(account -> account.outstandingBalance().compareTo(BigDecimal.ZERO) > 0)
            .toList();
    }

    @Transactional(readOnly = true)
    public BillingStatementView loadBillingStatement(Long familyAccountId) {
        FamilyAccount familyAccount = familyAccountRepository.findById(familyAccountId).orElseThrow();
        SchoolProfileView schoolProfile = schoolProfileService.loadView();
        return new BillingStatementView(
            LocalDateTime.now(),
            schoolProfile.schoolName(),
            schoolProfile.phoneNumber(),
            schoolProfile.emailAddress(),
            schoolProfile.mailingAddress(),
            familyAccount.getAccountNumber(),
            familyAccount.getAccountName(),
            familyAccount.getBillingRecipientName(),
            familyAccount.getBillingRecipientEmail(),
            familyAccount.getBillingRecipientPhone(),
            familyAccount.getMailingAddress(),
            outstandingBalanceForFamily(familyAccount),
            feesForFamily(familyAccount),
            paymentsForFamily(familyAccount).stream()
                .map(payment -> new BillingStatementPaymentRow(
                    payment.getPaymentDate(),
                    payment.isAnonymousToFamily() ? "Anonymous donor" : payment.getPayerProfile().getLookupLabel(),
                    payment.getPaymentPurpose().getLabel(),
                    payment.getTargetDisplayName(),
                    payment.getPaymentMethod().getLabel(),
                    payment.getReferenceNumber(),
                    payment.getTotalAmount(),
                    payment.getUnappliedAmount(),
                    payment.getNotes()
                ))
                .toList()
        );
    }

    @Transactional(readOnly = true)
    public void emailBillingStatement(Long familyAccountId) {
        BillingStatementView statement = loadBillingStatement(familyAccountId);
        if (statement.billingRecipientEmail() == null || statement.billingRecipientEmail().isBlank()) {
            throw new IllegalArgumentException("Billing recipient email is not available for this family.");
        }
        if (mailSender == null) {
            throw new IllegalStateException("Email delivery is not configured. Add spring.mail.* settings and try again.");
        }
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, false);
            helper.setTo(statement.billingRecipientEmail());
            helper.setSubject(statement.schoolName() + " billing statement for " + statement.familyAccountName());
            if (statement.schoolEmailAddress() != null && !statement.schoolEmailAddress().isBlank()) {
                helper.setFrom(statement.schoolEmailAddress());
            }
            helper.setText(buildBillingStatementEmailBody(statement), false);
            mailSender.send(message);
        } catch (MailException | jakarta.mail.MessagingException ex) {
            throw new IllegalStateException("Email delivery is not configured or failed. Configure spring.mail.* settings and try again.", ex);
        }
    }

    @Transactional(readOnly = true)
    public List<org.gca.schoolms.records.Student> students() {
        return studentRepository.findTop10ByOrderByLastNameAscFirstNameAsc();
    }

    @Transactional(readOnly = true)
    public PaymentReceiptView loadReceipt(Long paymentId) {
        Payment payment = paymentRepository.findWithDetailsById(paymentId).orElseThrow();
        PayerProfile payer = payment.getPayerProfile();
        SchoolProfileView schoolProfile = schoolProfileService.loadView();
        BigDecimal remainingBalance = payment.getPaymentPurpose() == PaymentPurpose.STUDENT_ACCOUNT
            ? outstandingBalanceForFamily(payment.getTargetStudent().getFamilyAccount())
            : BigDecimal.ZERO;
        return new PaymentReceiptView(
            payment.getId(),
            "RCT-%06d".formatted(payment.getId()),
            payment.getPaymentDate(),
            schoolProfile.schoolName(),
            schoolProfile.emailAddress(),
            schoolProfile.phoneNumber(),
            schoolProfile.mailingAddress(),
            payment.getReceivedByUserId(),
            payer.getFirstName(),
            payer.getMiddleName(),
            payer.getLastName(),
            payer.getBusinessName(),
            "PYR-%06d".formatted(payer.getId()),
            payer.getEmailAddress(),
            payer.getPhoneNumber(),
            payment.getFamilyAccount() != null ? payment.getFamilyAccount().getAccountName() : null,
            payment.isAnonymousToFamily(),
            payment.getPaymentPurpose().getLabel(),
            payment.getTargetDisplayName(),
            payment.getSchoolProjectType() != null ? payment.getSchoolProjectType().getName() : null,
            payment.getPaymentMethod().getLabel(),
            payment.getReferenceNumber(),
            payment.getTotalAmount(),
            payment.getUnappliedAmount(),
            remainingBalance,
            payment.getNotes(),
            payment.getAllocations().stream()
                .map(allocation -> new PaymentReceiptAllocationView(
                    allocation.getStudentFee().getFeeType().getName(),
                    allocation.getStudentFee().getDescription(),
                    allocation.getAmountApplied(),
                    allocation.getStudentFee().getOutstandingAmount()))
                .toList()
        );
    }

    private PaymentRowView toPaymentRowView(Payment payment) {
        return new PaymentRowView(
            payment.getPayerProfile().getDisplayName(),
            payment.getFamilyAccount() != null ? payment.getFamilyAccount().getAccountName() : null,
            payment.getPaymentPurpose().getLabel(),
            payment.getTargetDisplayName(),
            payment.getSchoolProjectType() != null ? payment.getSchoolProjectType().getName() : null,
            payment.getPaymentMethod().getLabel(),
            payment.getPaymentDate(),
            payment.getReferenceNumber(),
            payment.getTotalAmount(),
            payment.getUnappliedAmount(),
            payment.getNotes()
        );
    }

    private String normalizeReceiverId(String receivedByUserId) {
        if (receivedByUserId == null || receivedByUserId.isBlank()) {
            return "SYSTEM";
        }
        return receivedByUserId.trim().toUpperCase(Locale.ROOT);
    }

    private ParsedName parseName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new ParsedName("", "", "");
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return new ParsedName(parts[0], "", "");
        }
        if (parts.length == 2) {
            return new ParsedName(parts[0], "", parts[1]);
        }
        String first = parts[0];
        String last = parts[parts.length - 1];
        String middle = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length - 1));
        return new ParsedName(first, middle, last);
    }

    private PayerProfile ensureBillingPayerProfile(FamilyAccount familyAccount) {
        String billingName = familyAccount.getBillingRecipientName();
        ParsedName parsedName = parseName(billingName);
        return payerProfileRepository.findByFamilyAccountAndFirstNameAndLastName(familyAccount, parsedName.firstName(), parsedName.lastName())
            .orElseGet(() -> payerProfileRepository.save(new PayerProfile(
                parsedName.firstName(),
                blankToNull(parsedName.middleName()),
                parsedName.lastName(),
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

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String buildBillingStatementEmailBody(BillingStatementView statement) {
        StringBuilder body = new StringBuilder();
        body.append(statement.schoolName()).append('\n');
        body.append("Billing Statement").append('\n');
        body.append("Generated: ").append(statement.generatedOn()).append("\n\n");
        body.append("Account: ").append(statement.familyAccountName()).append(" (").append(statement.familyAccountNumber()).append(")\n");
        body.append("Billing recipient: ").append(statement.billingRecipientName()).append('\n');
        body.append("Outstanding balance: ").append(moneyFormatter.format(statement.outstandingBalance())).append("\n\n");
        body.append("Assessed fees:\n");
        for (StudentFee fee : statement.fees()) {
            body.append("- ")
                .append(fee.getStudentDisplayName())
                .append(" | ")
                .append(fee.getFeeType().getName())
                .append(" | ")
                .append(moneyFormatter.format(fee.getAmount()))
                .append(" | outstanding ")
                .append(moneyFormatter.format(fee.getOutstandingAmount()))
                .append('\n');
        }
        body.append("\nPayments and credits:\n");
        for (BillingStatementPaymentRow payment : statement.payments()) {
            body.append("- ")
                .append(payment.paymentPurposeLabel())
                .append(" | ")
                .append(payment.targetDisplayName())
                .append(" | ")
                .append(moneyFormatter.format(payment.totalAmount()))
                .append(" | ")
                .append(payment.payerDisplayName())
                .append('\n');
        }
        body.append("\nFor questions, contact ").append(statement.schoolName());
        if (statement.schoolPhoneNumber() != null && !statement.schoolPhoneNumber().isBlank()) {
            body.append(" at ").append(statement.schoolPhoneNumber());
        }
        if (statement.schoolEmailAddress() != null && !statement.schoolEmailAddress().isBlank()) {
            body.append(" or ").append(statement.schoolEmailAddress());
        }
        return body.toString();
    }

    private record ParsedName(String firstName, String middleName, String lastName) {
    }
}
