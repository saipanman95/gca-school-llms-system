package org.gca.schoolms.finance;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyAccountRepository extends JpaRepository<FamilyAccount, Long> {
    List<FamilyAccount> findTop10ByOrderByAccountNameAsc();
}
