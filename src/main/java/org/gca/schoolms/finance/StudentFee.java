package org.gca.schoolms.finance;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.gca.schoolms.enrollment.EnrollmentRequest;
import org.gca.schoolms.organization.Campus;
import org.gca.schoolms.records.Student;

@Entity
public class StudentFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "family_account_id", nullable = false)
    private FamilyAccount familyAccount;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fee_type_id", nullable = false)
    private FeeType feeType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime assessedAt;

    @Column(nullable = false)
    private String schoolYear;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentFeeStatus status = StudentFeeStatus.ACTIVE;

    private LocalDateTime cancelledAt;

    private String cancelledByUserId;

    private String cancellationReason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "enrollment_request_id")
    private EnrollmentRequest enrollmentRequest;

    @OneToMany(mappedBy = "studentFee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PaymentAllocation> paymentAllocations = new ArrayList<>();

    protected StudentFee() {
    }

    public StudentFee(Student student, FamilyAccount familyAccount, Campus campus, FeeType feeType,
                      BigDecimal amount, LocalDateTime assessedAt, String schoolYear, String description) {
        this(student, familyAccount, campus, feeType, amount, assessedAt, schoolYear, description, null);
    }

    public StudentFee(Student student, FamilyAccount familyAccount, Campus campus, FeeType feeType,
                      BigDecimal amount, LocalDateTime assessedAt, String schoolYear,
                      String description, EnrollmentRequest enrollmentRequest) {
        this.student = student;
        this.familyAccount = familyAccount;
        this.campus = campus;
        this.feeType = feeType;
        this.amount = amount;
        this.assessedAt = assessedAt;
        this.schoolYear = schoolYear;
        this.description = description;
        this.enrollmentRequest = enrollmentRequest;
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public String getStudentDisplayName() {
        if (student != null) {
            return student.getDisplayName();
        }
        if (enrollmentRequest != null) {
            return enrollmentRequest.getStudentDisplayName();
        }
        return "Pending student";
    }

    public FamilyAccount getFamilyAccount() {
        return familyAccount;
    }

    public Campus getCampus() {
        return campus;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getAssessedAt() {
        return assessedAt;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public String getDescription() {
        return description;
    }

    public EnrollmentRequest getEnrollmentRequest() {
        return enrollmentRequest;
    }

    public StudentFeeStatus getStatus() {
        return status;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public String getCancelledByUserId() {
        return cancelledByUserId;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public List<PaymentAllocation> getPaymentAllocations() {
        return paymentAllocations;
    }

    public BigDecimal getAppliedAmount() {
        return paymentAllocations.stream()
            .map(PaymentAllocation::getAmountApplied)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getOutstandingAmount() {
        if (status == StudentFeeStatus.CANCELLED) {
            return BigDecimal.ZERO;
        }
        return amount.subtract(getAppliedAmount());
    }

    public boolean isCancelable() {
        return status == StudentFeeStatus.ACTIVE && getAppliedAmount().compareTo(BigDecimal.ZERO) == 0;
    }

    public void cancel(String cancelledByUserId, String cancellationReason, LocalDateTime cancelledAt) {
        this.status = StudentFeeStatus.CANCELLED;
        this.cancelledByUserId = cancelledByUserId;
        this.cancellationReason = cancellationReason;
        this.cancelledAt = cancelledAt;
    }
}
