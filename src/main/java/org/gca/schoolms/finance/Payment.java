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
    @JoinColumn(name = "payer_profile_id", nullable = false)
    private PayerProfile payerProfile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "family_account_id")
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

    @Column(nullable = false)
    private String receivedByUserId;

    private String referenceNumber;

    private String notes;

    @Column(nullable = false)
    private boolean anonymousToFamily = false;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentAllocation> allocations = new ArrayList<>();

    protected Payment() {
    }

    public Payment(PayerProfile payerProfile, FamilyAccount familyAccount, PaymentMethod paymentMethod, BigDecimal totalAmount,
                   LocalDateTime paymentDate, String receivedByUserId, String referenceNumber, String notes) {
        this(payerProfile, familyAccount, PaymentPurpose.STUDENT_ACCOUNT, null, null, paymentMethod, totalAmount, paymentDate, receivedByUserId, referenceNumber, notes, false);
    }

    public Payment(FamilyAccount familyAccount, PaymentMethod paymentMethod, BigDecimal totalAmount,
                   LocalDateTime paymentDate, String referenceNumber, String notes) {
        this(null, familyAccount, PaymentPurpose.STUDENT_ACCOUNT, null, null, paymentMethod, totalAmount, paymentDate, "system", referenceNumber, notes, false);
    }

    public Payment(FamilyAccount familyAccount, PaymentMethod paymentMethod, BigDecimal totalAmount,
                   LocalDateTime paymentDate, String receivedByUserId, String referenceNumber, String notes) {
        this(null, familyAccount, PaymentPurpose.STUDENT_ACCOUNT, null, null, paymentMethod, totalAmount, paymentDate, receivedByUserId, referenceNumber, notes, false);
    }

    public Payment(PayerProfile payerProfile, FamilyAccount familyAccount, PaymentPurpose paymentPurpose, Student targetStudent,
                   SchoolProjectType schoolProjectType,
                   PaymentMethod paymentMethod, BigDecimal totalAmount,
                   LocalDateTime paymentDate, String receivedByUserId, String referenceNumber, String notes,
                   boolean anonymousToFamily) {
        this.payerProfile = payerProfile;
        this.familyAccount = familyAccount;
        this.paymentPurpose = paymentPurpose;
        this.targetStudent = targetStudent;
        this.schoolProjectType = schoolProjectType;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.paymentDate = paymentDate;
        this.receivedByUserId = receivedByUserId;
        this.referenceNumber = referenceNumber;
        this.notes = notes;
        this.anonymousToFamily = anonymousToFamily;
    }

    public PayerProfile getPayerProfile() {
        return payerProfile;
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
        if (paymentPurpose != PaymentPurpose.STUDENT_ACCOUNT) {
            return BigDecimal.ZERO;
        }
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

    public String getReceivedByUserId() {
        return receivedByUserId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isAnonymousToFamily() {
        return anonymousToFamily;
    }

    public List<PaymentAllocation> getAllocations() {
        return allocations;
    }
}
