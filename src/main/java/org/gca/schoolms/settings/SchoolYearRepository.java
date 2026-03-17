package org.gca.schoolms.settings;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolYearRepository extends JpaRepository<SchoolYear, Long> {
    List<SchoolYear> findAllByOrderByStartDateDesc();
    Optional<SchoolYear> findByLabel(String label);
}
