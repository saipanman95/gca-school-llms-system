package org.gca.schoolms.policy;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardRuleSetRepository extends JpaRepository<AwardRuleSet, Long> {
    Optional<AwardRuleSet> findByCode(String code);
}
