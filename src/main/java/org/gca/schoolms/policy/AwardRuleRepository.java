package org.gca.schoolms.policy;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardRuleRepository extends JpaRepository<AwardRule, Long> {
    List<AwardRule> findAllByRuleSetOrderByAwardCategoryAscNameAsc(AwardRuleSet ruleSet);
}
