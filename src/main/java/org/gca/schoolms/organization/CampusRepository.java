package org.gca.schoolms.organization;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampusRepository extends JpaRepository<Campus, Long> {
    long countByActiveTrue();
    List<Campus> findAllByOrderByNameAsc();
    Optional<Campus> findByCode(String code);
}
