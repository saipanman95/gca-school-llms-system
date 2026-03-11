package org.gca.schoolms.finance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.gca.schoolms.organization.Campus;

@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "family_account_id", nullable = false)
    private FamilyAccount familyAccount;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal outstandingAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;

    @Column(nullable = false)
    private LocalDate dueDate;

    protected Invoice() {
    }

    public Invoice(FamilyAccount familyAccount, String description, BigDecimal totalAmount, BigDecimal outstandingAmount,
                   InvoiceStatus status, Campus campus, LocalDate dueDate) {
        this.familyAccount = familyAccount;
        this.description = description;
        this.totalAmount = totalAmount;
        this.outstandingAmount = outstandingAmount;
        this.status = status;
        this.campus = campus;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public String getAccountName() {
        return familyAccount.getAccountName();
    }

    public FamilyAccount getFamilyAccount() {
        return familyAccount;
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

    public Campus getCampus() {
        return campus;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
