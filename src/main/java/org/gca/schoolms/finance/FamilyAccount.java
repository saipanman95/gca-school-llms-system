package org.gca.schoolms.finance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.gca.schoolms.organization.Campus;

@Entity
public class FamilyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String primaryGuardianName;

    @Column(nullable = false)
    private String primaryGuardianEmail;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "campus_id", nullable = false)
    private Campus campus;

    protected FamilyAccount() {
    }

    public FamilyAccount(String accountNumber, String accountName, String primaryGuardianName,
                         String primaryGuardianEmail, Campus campus) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.primaryGuardianName = primaryGuardianName;
        this.primaryGuardianEmail = primaryGuardianEmail;
        this.campus = campus;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getPrimaryGuardianName() {
        return primaryGuardianName;
    }

    public String getPrimaryGuardianEmail() {
        return primaryGuardianEmail;
    }

    public Campus getCampus() {
        return campus;
    }
}
