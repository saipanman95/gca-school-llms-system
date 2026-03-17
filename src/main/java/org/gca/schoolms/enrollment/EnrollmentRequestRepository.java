package org.gca.schoolms.enrollment;

import java.util.List;
import java.util.Optional;
import org.gca.schoolms.finance.FamilyAccount;
import org.gca.schoolms.records.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRequestRepository extends JpaRepository<EnrollmentRequest, Long> {
    List<EnrollmentRequest> findByFamilyAccountOrderBySubmittedOnDesc(FamilyAccount familyAccount);
    List<EnrollmentRequest> findAllByOrderBySubmittedOnDescIdDesc();
    List<EnrollmentRequest> findByStatusNotOrderBySubmittedOnDescIdDesc(EnrollmentRequestStatus status);
    Optional<EnrollmentRequest> findTopByStudentOrderBySubmittedOnDesc(Student student);
}
