package org.gca.schoolms.finance;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeScheduleRepository extends JpaRepository<FeeSchedule, Long> {
    List<FeeSchedule> findAllByOrderBySchoolYearDescNameAsc();
}
