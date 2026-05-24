package org.gca.schoolms.dashboard;

import java.math.BigDecimal;
import org.gca.schoolms.academics.AttendanceRecordRepository;
import org.gca.schoolms.academics.SectionRepository;
import org.gca.schoolms.finance.FinanceLedgerService;
import org.gca.schoolms.integration.powerschool.PowerSchoolSectionRepository;
import org.gca.schoolms.integration.powerschool.PowerSchoolStoredGradeRepository;
import org.gca.schoolms.integration.powerschool.PowerSchoolStudentRepository;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.records.StudentRepository;
import org.gca.schoolms.records.StudentStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final FinanceLedgerService financeLedgerService;
    private final SectionRepository sectionRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final CampusRepository campusRepository;
    private final PowerSchoolStudentRepository powerSchoolStudentRepository;
    private final PowerSchoolStoredGradeRepository powerSchoolStoredGradeRepository;
    private final PowerSchoolSectionRepository powerSchoolSectionRepository;

    public DashboardService(StudentRepository studentRepository, FinanceLedgerService financeLedgerService,
                            SectionRepository sectionRepository, AttendanceRecordRepository attendanceRecordRepository,
                            CampusRepository campusRepository,
                            PowerSchoolStudentRepository powerSchoolStudentRepository,
                            PowerSchoolStoredGradeRepository powerSchoolStoredGradeRepository,
                            PowerSchoolSectionRepository powerSchoolSectionRepository) {
        this.studentRepository = studentRepository;
        this.financeLedgerService = financeLedgerService;
        this.sectionRepository = sectionRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.campusRepository = campusRepository;
        this.powerSchoolStudentRepository = powerSchoolStudentRepository;
        this.powerSchoolStoredGradeRepository = powerSchoolStoredGradeRepository;
        this.powerSchoolSectionRepository = powerSchoolSectionRepository;
    }

    public DashboardMetrics loadMetrics() {
        try {
            long activeStudents = studentRepository.countByStatus(StudentStatus.ACTIVE);
            long storedRecords = studentRepository.count() * 4;
            BigDecimal outstandingBalance = financeLedgerService.totalOutstandingBalance();
            long activeSections = sectionRepository.count();
            long activeCampuses = campusRepository.countByActiveTrue();
            long openInvoices = financeLedgerService.openChargeCount();
            long absencesToday = attendanceRecordRepository.countAbsences();
            return new DashboardMetrics(activeCampuses, activeStudents, storedRecords, outstandingBalance,
                activeSections, openInvoices, absencesToday);
        } catch (RuntimeException exception) {
            if (!isSchemaCompatibilityException(exception)) {
                throw exception;
            }
            long activeCampuses = campusRepository.countByActiveTrue();
            long activeStudents = powerSchoolStudentRepository.count();
            long storedRecords = powerSchoolStoredGradeRepository.count();
            long activeSections = powerSchoolSectionRepository.count();
            return new DashboardMetrics(activeCampuses, activeStudents, storedRecords, BigDecimal.ZERO,
                activeSections, 0, 0);
        }
    }

    private boolean isSchemaCompatibilityException(RuntimeException exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof DataAccessException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
