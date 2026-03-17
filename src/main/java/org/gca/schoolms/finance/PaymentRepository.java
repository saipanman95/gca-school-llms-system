package org.gca.schoolms.finance;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @EntityGraph(attributePaths = {
        "payerProfile",
        "familyAccount",
        "targetStudent",
        "schoolProjectType",
        "allocations",
        "allocations.studentFee",
        "allocations.studentFee.student"
    })
    List<Payment> findTop20ByOrderByPaymentDateDescIdDesc();

    @EntityGraph(attributePaths = {
        "payerProfile",
        "familyAccount",
        "targetStudent",
        "schoolProjectType",
        "allocations",
        "allocations.studentFee",
        "allocations.studentFee.student"
    })
    List<Payment> findByFamilyAccountOrderByPaymentDateDescIdDesc(FamilyAccount familyAccount);

    @EntityGraph(attributePaths = {
        "payerProfile",
        "familyAccount",
        "targetStudent",
        "schoolProjectType",
        "allocations",
        "allocations.studentFee",
        "allocations.studentFee.student",
        "allocations.studentFee.feeType"
    })
    Optional<Payment> findWithDetailsById(Long id);
}
