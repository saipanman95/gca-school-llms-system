package org.gca.schoolms.policy;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradingSpecialMarkRepository extends JpaRepository<GradingSpecialMark, Long> {
    List<GradingSpecialMark> findAllByScaleSetOrderBySortOrderAsc(GradingScaleSet scaleSet);
}
