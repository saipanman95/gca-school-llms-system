package org.gca.schoolms.integration.powerschool;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherCourseStageRepository extends JpaRepository<TeacherCourseStage, Long> {
    List<TeacherCourseStage> findAllByOrderBySchoolYearAscTeacherNameAscCourseNameAsc();
}
