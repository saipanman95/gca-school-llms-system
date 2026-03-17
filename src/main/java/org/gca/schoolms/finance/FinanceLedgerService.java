package org.gca.schoolms.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.gca.schoolms.enrollment.EnrollmentRequest;
import org.gca.schoolms.enrollment.EnrollmentRequestType;
import org.gca.schoolms.records.Student;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.records.StudentRepository;
import org.gca.schoolms.settings.SchoolProfileService;
import org.gca.schoolms.settings.SchoolProfileView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinanceLedgerService {

    private final FeeTypeRepository feeTypeRepository;
    private final StudentFeeRepository studentFeeRepository;
    private final PaymentRepository paymentRepository;
    private final SchoolProjectTypeRepository schoolProjectTypeRepository;
    private final FamilyAccountRepository familyAccountRepository;
    private final StudentRepository studentRepository;
    private final CampusRepository campusRepository;
    private final SchoolProfileService schoolProfileService;

    public FinanceLedgerService(FeeTypeRepository feeTypeRepository, StudentFeeRepository studentFeeRepository,
                                PaymentRepository paymentRepository, SchoolProjectTypeRepository schoolProjectTypeRepository,
                                FamilyAccountRepository familyAccountRepository, StudentRepository studentRepository,
                                CampusRepository campusRepository, SchoolProfileService schoolProfileService) {
        this.feeTypeRepository = feeTypeRepository;
        this.studentFeeRepository = studentFeeRepository;
        this.paymentRepository = paymentRepository;
        this.schoolProjectTypeRepository = schoolProjectTypeRepository;
        this.familyAccountRepository = familyAccountRepository;
        this.studentRepository = studentRepository;
        this.campusRepository = campusRepository;
        this.schoolProfileService = schoolProfileService;
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

    @Transactional(readOnly = true)
    public List<FinanceStudentPaymentOption> paymentStudentOptions() {
        return studentRepository.findAllByOrderByLastNameAscFirstNameAsc().stream()
            .map(student -> new FinanceStudentPaymentOption(
                student.getId(),
                student.getFamilyAccount().getId(),
                student.getLastName() + ", " + student.getFirstName()
                    + " (" + student.getStudentNumber() + " / "
                    + student.getFamilyAccount().getAccountName() + ")"))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<FinanceFamilyLookupOption> paymentFamilyOptions() {
        return familyAccountRepository.findAllByOrderByPrimaryGuardianNameAsc().stream()
            .map(account -> new FinanceFamilyLookupOption(
                account.getId(),
                account.getPrimaryGuardianName() + " (" + account.getAccountName() + ")"))
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
    public Long recordPayment(PaymentPurpose paymentPurpose, Long studentId, Long payerFamilyAccountId,
                              Long schoolProjectTypeId, boolean crossFamilyConfirmed, PaymentMethod paymentMethod,
                              BigDecimal amount, String referenceNumber, String notes, String receivedByUserId) {
        if (payerFamilyAccountId == null) {
            throw new IllegalArgumentException("A guardian / payer must be selected from the lookup list.");
        }
        var payerFamilyAccount = familyAccountRepository.findById(payerFamilyAccountId).orElseThrow();
        Student targetStudent = studentId == null ? null : studentRepository.findById(studentId).orElseThrow();
        SchoolProjectType schoolProjectType = schoolProjectTypeId == null ? null : schoolProjectTypeRepository.findById(schoolProjectTypeId).orElseThrow();
        if (paymentPurpose == PaymentPurpose.STUDENT_ACCOUNT) {
            if (targetStudent == null) {
                throw new IllegalArgumentException("A student must be selected for student payments.");
            }
            boolean sameFamily = targetStudent.getFamilyAccount().getId().equals(payerFamilyAccount.getId());
            if (!sameFamily && !crossFamilyConfirmed) {
                throw new IllegalArgumentException("Cross-family student payments require confirmation.");
            }
            var payment = paymentRepository.save(new Payment(
                payerFamilyAccount,
                paymentPurpose,
                targetStudent,
                null,
                paymentMethod,
                amount,
                LocalDateTime.now(),
                normalizeReceiverId(receivedByUserId),
                referenceNumber == null || referenceNumber.isBlank() ? null : referenceNumber.trim(),
                notes == null || notes.isBlank() ? null : notes.trim()
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
            payerFamilyAccount,
            paymentPurpose,
            targetStudent,
            schoolProjectType,
            paymentMethod,
            amount,
            LocalDateTime.now(),
            normalizeReceiverId(receivedByUserId),
            referenceNumber == null || referenceNumber.isBlank() ? null : referenceNumber.trim(),
            notes == null || notes.isBlank() ? null : notes.trim()
        ));
        return payment.getId();
    }

    @Transactional(readOnly = true)
    public List<FamilyAccount> familyAccounts() {
        return familyAccountRepository.findTop10ByOrderByAccountNameAsc();
    }

    @Transactional(readOnly = true)
    public List<org.gca.schoolms.records.Student> students() {
        return studentRepository.findTop10ByOrderByLastNameAscFirstNameAsc();
    }

    @Transactional(readOnly = true)
    public PaymentReceiptView loadReceipt(Long paymentId) {
        Payment payment = paymentRepository.findWithDetailsById(paymentId).orElseThrow();
        FamilyAccount payer = payment.getFamilyAccount();
        ParsedName payerName = parseName(payer.getPrimaryGuardianName());
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
            payerName.firstName(),
            payerName.middleName(),
            payerName.lastName(),
            payer.getAccountNumber(),
            payer.getAccountName(),
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
            payment.getFamilyAccount().getAccountName(),
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

    private record ParsedName(String firstName, String middleName, String lastName) {
    }
}
