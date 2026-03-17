package org.gca.schoolms.finance;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayerProfileRepository extends JpaRepository<PayerProfile, Long> {
    List<PayerProfile> findAllByOrderByLastNameAscFirstNameAsc();
    Optional<PayerProfile> findByFamilyAccountAndFirstNameAndLastName(FamilyAccount familyAccount, String firstName, String lastName);
}
