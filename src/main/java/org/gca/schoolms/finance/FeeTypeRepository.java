package org.gca.schoolms.finance;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeTypeRepository extends JpaRepository<FeeType, Long> {
    List<FeeType> findAllByOrderByNameAsc();
    Optional<FeeType> findByCode(String code);
}
