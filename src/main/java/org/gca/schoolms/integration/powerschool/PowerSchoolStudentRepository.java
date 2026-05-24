package org.gca.schoolms.integration.powerschool;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PowerSchoolStudentRepository extends JpaRepository<PowerSchoolStudent, Integer> {
    List<PowerSchoolStudent> findTop10ByOrderByLastNameAscFirstNameAsc();
}
