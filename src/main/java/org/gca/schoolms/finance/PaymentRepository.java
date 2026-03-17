package org.gca.schoolms.finance;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @EntityGraph(attributePaths = {
        "familyAccount",
        "targetStudent",
        "schoolProjectType",
        "allocations",
        "allocations.studentFee",
        "allocations.studentFee.student"
    })
    List<Payment> findTop20ByOrderByPaymentDateDescIdDesc();

    @EntityGraph(attributePaths = {
        "familyAccount",
        "targetStudent",
        "schoolProjectType",
        "allocations",
        "allocations.studentFee",
        "allocations.studentFee.student"
    })
    List<Payment> findByFamilyAccountOrderByPaymentDateDescIdDesc(FamilyAccount familyAccount);
}
