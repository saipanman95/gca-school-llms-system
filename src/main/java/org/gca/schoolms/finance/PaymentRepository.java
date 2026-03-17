package org.gca.schoolms.finance;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findTop20ByOrderByPaymentDateDescIdDesc();
    List<Payment> findByFamilyAccountOrderByPaymentDateDescIdDesc(FamilyAccount familyAccount);
}
