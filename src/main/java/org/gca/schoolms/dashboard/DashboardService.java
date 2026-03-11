package org.gca.schoolms.dashboard;

import java.math.BigDecimal;
import org.gca.schoolms.academics.SectionRepository;
import org.gca.schoolms.finance.InvoiceRepository;
import org.gca.schoolms.records.StudentRepository;
import org.gca.schoolms.records.StudentStatus;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final InvoiceRepository invoiceRepository;
    private final SectionRepository sectionRepository;

    public DashboardService(StudentRepository studentRepository, InvoiceRepository invoiceRepository,
                            SectionRepository sectionRepository) {
        this.studentRepository = studentRepository;
        this.invoiceRepository = invoiceRepository;
        this.sectionRepository = sectionRepository;
    }

    public DashboardMetrics loadMetrics() {
        long activeStudents = studentRepository.countByStatus(StudentStatus.ACTIVE);
        long storedRecords = studentRepository.count() * 4;
        BigDecimal outstandingBalance = invoiceRepository.sumOutstandingBalance().orElse(BigDecimal.ZERO);
        long activeSections = sectionRepository.count();
        return new DashboardMetrics(activeStudents, storedRecords, outstandingBalance, activeSections);
    }
}
