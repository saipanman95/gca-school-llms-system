package org.gca.schoolms.policy;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradingScaleBandRepository extends JpaRepository<GradingScaleBand, Long> {
    List<GradingScaleBand> findAllByScaleSetOrderByTrackCodeAscSortOrderAsc(GradingScaleSet scaleSet);
}
