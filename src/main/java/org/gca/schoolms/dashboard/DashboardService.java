package org.gca.schoolms.dashboard;

import java.math.BigDecimal;
import org.gca.schoolms.academics.AttendanceRecordRepository;
import org.gca.schoolms.academics.SectionRepository;
import org.gca.schoolms.finance.FinanceLedgerService;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.records.StudentRepository;
import org.gca.schoolms.records.StudentStatus;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final FinanceLedgerService financeLedgerService;
    private final SectionRepository sectionRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final CampusRepository campusRepository;

    public DashboardService(StudentRepository studentRepository, FinanceLedgerService financeLedgerService,
                            SectionRepository sectionRepository, AttendanceRecordRepository attendanceRecordRepository,
                            CampusRepository campusRepository) {
        this.studentRepository = studentRepository;
        this.financeLedgerService = financeLedgerService;
        this.sectionRepository = sectionRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.campusRepository = campusRepository;
    }

    public DashboardMetrics loadMetrics() {
        long activeStudents = studentRepository.countByStatus(StudentStatus.ACTIVE);
        long storedRecords = studentRepository.count() * 4;
        BigDecimal outstandingBalance = financeLedgerService.totalOutstandingBalance();
        long activeSections = sectionRepository.count();
        long activeCampuses = campusRepository.countByActiveTrue();
        long openInvoices = financeLedgerService.openChargeCount();
        long absencesToday = attendanceRecordRepository.countAbsences();
        return new DashboardMetrics(activeCampuses, activeStudents, storedRecords, outstandingBalance,
            activeSections, openInvoices, absencesToday);
    }
}
