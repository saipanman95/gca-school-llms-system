package org.gca.schoolms.finance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class PaymentAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_fee_id", nullable = false)
    private StudentFee studentFee;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountApplied;

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    protected PaymentAllocation() {
    }

    public PaymentAllocation(Payment payment, StudentFee studentFee, BigDecimal amountApplied, LocalDateTime appliedAt) {
        this.payment = payment;
        this.studentFee = studentFee;
        this.amountApplied = amountApplied;
        this.appliedAt = appliedAt;
    }

    public BigDecimal getAmountApplied() {
        return amountApplied;
    }

    public StudentFee getStudentFee() {
        return studentFee;
    }
}
