package org.gca.schoolms.finance;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolProjectTypeRepository extends JpaRepository<SchoolProjectType, Long> {
    List<SchoolProjectType> findAllByOrderByNameAsc();
    Optional<SchoolProjectType> findByCode(String code);
}
