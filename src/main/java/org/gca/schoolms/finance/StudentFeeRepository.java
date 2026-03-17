package org.gca.schoolms.finance;

import java.util.List;
import java.util.Optional;
import org.gca.schoolms.enrollment.EnrollmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentFeeRepository extends JpaRepository<StudentFee, Long> {
    List<StudentFee> findTop20ByOrderByAssessedAtDescIdDesc();
    List<StudentFee> findByFamilyAccountOrderByAssessedAtDescIdDesc(FamilyAccount familyAccount);
    List<StudentFee> findByStudentOrderByAssessedAtAscIdAsc(org.gca.schoolms.records.Student student);
    List<StudentFee> findAllByOrderByAssessedAtAscIdAsc();
    Optional<StudentFee> findByEnrollmentRequest(EnrollmentRequest enrollmentRequest);
}
