package org.gca.schoolms.finance;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.gca.schoolms.records.Student;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "family_account_id", nullable = false)
    private FamilyAccount familyAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentPurpose paymentPurpose;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_student_id")
    private Student targetStudent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "school_project_type_id")
    private SchoolProjectType schoolProjectType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    private String referenceNumber;

    private String notes;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentAllocation> allocations = new ArrayList<>();

    protected Payment() {
    }

    public Payment(FamilyAccount familyAccount, PaymentMethod paymentMethod, BigDecimal totalAmount,
                   LocalDateTime paymentDate, String referenceNumber, String notes) {
        this(familyAccount, PaymentPurpose.STUDENT_ACCOUNT, null, null, paymentMethod, totalAmount, paymentDate, referenceNumber, notes);
    }

    public Payment(FamilyAccount familyAccount, PaymentPurpose paymentPurpose, Student targetStudent,
                   SchoolProjectType schoolProjectType,
                   PaymentMethod paymentMethod, BigDecimal totalAmount,
                   LocalDateTime paymentDate, String referenceNumber, String notes) {
        this.familyAccount = familyAccount;
        this.paymentPurpose = paymentPurpose;
        this.targetStudent = targetStudent;
        this.schoolProjectType = schoolProjectType;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.paymentDate = paymentDate;
        this.referenceNumber = referenceNumber;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public FamilyAccount getFamilyAccount() {
        return familyAccount;
    }

    public PaymentPurpose getPaymentPurpose() {
        return paymentPurpose;
    }

    public Student getTargetStudent() {
        return targetStudent;
    }

    public SchoolProjectType getSchoolProjectType() {
        return schoolProjectType;
    }

    public String getTargetDisplayName() {
        if (targetStudent != null) {
            return targetStudent.getDisplayName();
        }
        return allocations.stream()
            .map(PaymentAllocation::getStudentFee)
            .map(StudentFee::getStudentDisplayName)
            .findFirst()
            .orElse("School-wide");
    }

    public BigDecimal getAllocatedAmount() {
        return allocations.stream()
            .map(PaymentAllocation::getAmountApplied)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getUnappliedAmount() {
        return totalAmount.subtract(getAllocatedAmount());
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getNotes() {
        return notes;
    }

    public List<PaymentAllocation> getAllocations() {
        return allocations;
    }
}
