package org.gca.schoolms.finance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findTop10ByOrderByDueDateAsc();

    @Query("select coalesce(sum(i.outstandingAmount), 0) from Invoice i where i.status <> org.gca.schoolms.finance.InvoiceStatus.PAID")
    Optional<BigDecimal> sumOutstandingBalance();

    @Query("select count(i) from Invoice i where i.status <> org.gca.schoolms.finance.InvoiceStatus.PAID")
    long countOpenInvoices();
}
