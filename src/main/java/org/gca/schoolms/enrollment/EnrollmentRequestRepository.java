package org.gca.schoolms.enrollment;

import java.util.List;
import org.gca.schoolms.finance.FamilyAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRequestRepository extends JpaRepository<EnrollmentRequest, Long> {
    List<EnrollmentRequest> findByFamilyAccountOrderBySubmittedOnDesc(FamilyAccount familyAccount);
}
