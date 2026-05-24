package org.gca.schoolms.policy;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradingScaleSetRepository extends JpaRepository<GradingScaleSet, Long> {
    Optional<GradingScaleSet> findByCode(String code);
}
