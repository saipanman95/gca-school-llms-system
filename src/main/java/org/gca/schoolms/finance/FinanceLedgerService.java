package org.gca.schoolms.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.gca.schoolms.enrollment.EnrollmentRequest;
import org.gca.schoolms.enrollment.EnrollmentRequestType;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.records.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinanceLedgerService {

    private final FeeTypeRepository feeTypeRepository;
    private final StudentFeeRepository studentFeeRepository;
    private final PaymentRepository paymentRepository;
    private final FamilyAccountRepository familyAccountRepository;
    private final StudentRepository studentRepository;
    private final CampusRepository campusRepository;

    public FinanceLedgerService(FeeTypeRepository feeTypeRepository, StudentFeeRepository studentFeeRepository,
                                PaymentRepository paymentRepository, FamilyAccountRepository familyAccountRepository,
                                StudentRepository studentRepository, CampusRepository campusRepository) {
        this.feeTypeRepository = feeTypeRepository;
        this.studentFeeRepository = studentFeeRepository;
        this.paymentRepository = paymentRepository;
        this.familyAccountRepository = familyAccountRepository;
        this.studentRepository = studentRepository;
        this.campusRepository = campusRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal totalOutstandingBalance() {
        return studentFeeRepository.findAll().stream()
            .map(StudentFee::getOutstandingAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal outstandingBalanceForFamily(FamilyAccount familyAccount) {
        return studentFeeRepository.findByFamilyAccountOrderByAssessedAtDescIdDesc(familyAccount).stream()
            .map(StudentFee::getOutstandingAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
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
    public List<Payment> recentPayments() {
        return paymentRepository.findTop20ByOrderByPaymentDateDescIdDesc();
    }

    @Transactional(readOnly = true)
    public List<StudentFee> feesForFamily(FamilyAccount familyAccount) {
        return studentFeeRepository.findByFamilyAccountOrderByAssessedAtDescIdDesc(familyAccount);
    }

    @Transactional(readOnly = true)
    public List<Payment> paymentsForFamily(FamilyAccount familyAccount) {
        return paymentRepository.findByFamilyAccountOrderByPaymentDateDescIdDesc(familyAccount);
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
    public void recordPayment(Long studentFeeId, PaymentMethod paymentMethod, BigDecimal amount,
                              String referenceNumber, String notes) {
        var studentFee = studentFeeRepository.findById(studentFeeId).orElseThrow();
        var payment = paymentRepository.save(new Payment(
            studentFee.getFamilyAccount(),
            paymentMethod,
            amount,
            LocalDateTime.now(),
            referenceNumber == null || referenceNumber.isBlank() ? null : referenceNumber.trim(),
            notes == null || notes.isBlank() ? null : notes.trim()
        ));
        payment.getAllocations().add(new PaymentAllocation(payment, studentFee, amount, LocalDateTime.now()));
        paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public List<FamilyAccount> familyAccounts() {
        return familyAccountRepository.findTop10ByOrderByAccountNameAsc();
    }

    @Transactional(readOnly = true)
    public List<org.gca.schoolms.records.Student> students() {
        return studentRepository.findTop10ByOrderByLastNameAscFirstNameAsc();
    }
}
