package org.gca.schoolms.academics;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findTop10ByOrderByTermNameDescCourseCodeAsc();
}
