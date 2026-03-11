package org.gca.schoolms.portal;

import java.time.LocalDate;
import java.util.List;
import org.gca.schoolms.enrollment.EnrollmentRequest;
import org.gca.schoolms.enrollment.EnrollmentRequestRepository;
import org.gca.schoolms.enrollment.EnrollmentRequestStatus;
import org.gca.schoolms.finance.FamilyAccount;
import org.gca.schoolms.finance.FamilyAccountRepository;
import org.gca.schoolms.finance.InvoiceRepository;
import org.gca.schoolms.organization.CampusRepository;
import org.gca.schoolms.records.Student;
import org.gca.schoolms.records.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuardianPortalService {

    private final FamilyAccountRepository familyAccountRepository;
    private final StudentRepository studentRepository;
    private final InvoiceRepository invoiceRepository;
    private final EnrollmentRequestRepository enrollmentRequestRepository;
    private final CampusRepository campusRepository;

    public GuardianPortalService(FamilyAccountRepository familyAccountRepository, StudentRepository studentRepository,
                                 InvoiceRepository invoiceRepository, EnrollmentRequestRepository enrollmentRequestRepository,
                                 CampusRepository campusRepository) {
        this.familyAccountRepository = familyAccountRepository;
        this.studentRepository = studentRepository;
        this.invoiceRepository = invoiceRepository;
        this.enrollmentRequestRepository = enrollmentRequestRepository;
        this.campusRepository = campusRepository;
    }

    public GuardianDashboardView loadDashboard(String username) {
        FamilyAccount familyAccount = resolveFamilyAccount(username);
        List<Student> students = studentRepository.findByFamilyAccountOrderByLastNameAscFirstNameAsc(familyAccount);
        List<GuardianDashboardStudent> dashboardStudents = students.stream()
            .map(student -> new GuardianDashboardStudent(
                student.getDisplayName(),
                student.getGradeLevel().getLabel(),
                student.getCampus().getCode(),
                student.getStatus().name()))
            .toList();
        return new GuardianDashboardView(
            familyAccount.getAccountName(),
            familyAccount.getPrimaryGuardianName(),
            students.size(),
            invoiceRepository.sumOutstandingBalanceByFamilyAccount(familyAccount).orElse(java.math.BigDecimal.ZERO),
            dashboardStudents,
            enrollmentRequestRepository.findByFamilyAccountOrderBySubmittedOnDesc(familyAccount)
        );
    }

    public FamilyAccount resolveFamilyAccount(String username) {
        if ("guardian".equals(username)) {
            return familyAccountRepository.findByAccountNumber("FA-1001")
                .orElseThrow();
        }
        return familyAccountRepository.findTop10ByOrderByAccountNameAsc().stream().findFirst().orElseThrow();
    }

    public List<Student> loadStudentsForGuardian(String username) {
        return studentRepository.findByFamilyAccountOrderByLastNameAscFirstNameAsc(resolveFamilyAccount(username));
    }

    @Transactional
    public void submitEnrollmentRequest(String username, GuardianEnrollmentForm form) {
        FamilyAccount familyAccount = resolveFamilyAccount(username);
        Student existingStudent = form.getExistingStudentId() == null ? null :
            studentRepository.findById(form.getExistingStudentId()).orElse(null);
        EnrollmentRequest request = new EnrollmentRequest(
            familyAccount,
            existingStudent,
            campusRepository.findById(form.getCampusId()).orElseThrow(),
            form.getRequestType(),
            EnrollmentRequestStatus.SUBMITTED,
            form.getSchoolYear(),
            form.getStudentFirstName(),
            form.getStudentLastName(),
            form.getRequestedGradeLevel(),
            LocalDate.now()
        );
        enrollmentRequestRepository.save(request);
    }
}
