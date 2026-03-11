package org.gca.schoolms.finance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal outstandingAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Column(nullable = false)
    private LocalDate dueDate;

    protected Invoice() {
    }

    public Invoice(String accountName, String description, BigDecimal totalAmount, BigDecimal outstandingAmount,
                   InvoiceStatus status, LocalDate dueDate) {
        this.accountName = accountName;
        this.description = description;
        this.totalAmount = totalAmount;
        this.outstandingAmount = outstandingAmount;
        this.status = status;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
