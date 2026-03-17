package org.gca.schoolms.finance;

import java.util.List;
import java.util.Optional;
import org.gca.schoolms.enrollment.EnrollmentRequest;
import org.gca.schoolms.records.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentFeeRepository extends JpaRepository<StudentFee, Long> {
    List<StudentFee> findTop20ByOrderByAssessedAtDescIdDesc();
    List<StudentFee> findByFamilyAccountOrderByAssessedAtDescIdDesc(FamilyAccount familyAccount);
    List<StudentFee> findByStudentOrderByAssessedAtAscIdAsc(Student student);
    List<StudentFee> findAllByOrderByAssessedAtAscIdAsc();
    Optional<StudentFee> findByEnrollmentRequest(EnrollmentRequest enrollmentRequest);
    long countByStudentAndFeeTypeAndSchoolYearAndStatus(Student student, FeeType feeType, String schoolYear, StudentFeeStatus status);
    long countByFeeType(FeeType feeType);
}
