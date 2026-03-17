package org.gca.schoolms.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.gca.schoolms.enrollment.EnrollmentRequest;
import org.gca.schoolms.enrollment.EnrollmentRequestType;
import org.gca.schoolms.records.Student;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.records.StudentRepository;
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

    public FinanceLedgerService(FeeTypeRepository feeTypeRepository, StudentFeeRepository studentFeeRepository,
                                PaymentRepository paymentRepository, SchoolProjectTypeRepository schoolProjectTypeRepository,
                                FamilyAccountRepository familyAccountRepository, StudentRepository studentRepository,
                                CampusRepository campusRepository) {
        this.feeTypeRepository = feeTypeRepository;
        this.studentFeeRepository = studentFeeRepository;
        this.paymentRepository = paymentRepository;
        this.schoolProjectTypeRepository = schoolProjectTypeRepository;
        this.familyAccountRepository = familyAccountRepository;
        this.studentRepository = studentRepository;
        this.campusRepository = campusRepository;
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
        createFeeType(code, name, null);
    }

    @Transactional
    public void createFeeType(String code, String name, BigDecimal defaultAmount) {
        feeTypeRepository.save(new FeeType(code.trim().toUpperCase(), name.trim(), defaultAmount, true));
    }

    @Transactional
    public void createSchoolProjectType(String code, String name) {
        schoolProjectTypeRepository.save(new SchoolProjectType(code.trim().toUpperCase(), name.trim(), true));
    }

    @Transactional
    public void assessFee(Long studentId, Long feeTypeId, BigDecimal amount, String schoolYear, String description) {
        var student = studentRepository.findById(studentId).orElseThrow();
        var feeType = feeTypeRepository.findById(feeTypeId).orElseThrow();
        studentFeeRepository.save(new StudentFee(
            student,
            student.getFamilyAccount(),
            campusRepository.findById(student.getCampus().getId()).orElseThrow(),
            feeType,
            amount,
            LocalDateTime.now(),
            schoolYear.trim(),
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

    @Transactional
    public void recordPayment(PaymentPurpose paymentPurpose, Long studentId, Long payerFamilyAccountId,
                              Long schoolProjectTypeId, boolean crossFamilyConfirmed, PaymentMethod paymentMethod,
                              BigDecimal amount, String referenceNumber, String notes) {
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
            return;
        }
        if (paymentPurpose == PaymentPurpose.GIFT_TO_SCHOOL && schoolProjectType == null) {
            throw new IllegalArgumentException("Select a school project type for gifts to the school.");
        }
        paymentRepository.save(new Payment(
            payerFamilyAccount,
            paymentPurpose,
            targetStudent,
            schoolProjectType,
            paymentMethod,
            amount,
            LocalDateTime.now(),
            referenceNumber == null || referenceNumber.isBlank() ? null : referenceNumber.trim(),
            notes == null || notes.isBlank() ? null : notes.trim()
        ));
    }

    @Transactional(readOnly = true)
    public List<FamilyAccount> familyAccounts() {
        return familyAccountRepository.findTop10ByOrderByAccountNameAsc();
    }

    @Transactional(readOnly = true)
    public List<org.gca.schoolms.records.Student> students() {
        return studentRepository.findTop10ByOrderByLastNameAscFirstNameAsc();
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
}
