package org.gca.schoolms.records;

import org.gca.schoolms.finance.FamilyAccount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    long countByStatus(StudentStatus status);
    List<Student> findTop10ByOrderByLastNameAscFirstNameAsc();
    long countByCampusActiveTrueAndStatus(StudentStatus status);
    List<Student> findByFamilyAccountOrderByLastNameAscFirstNameAsc(FamilyAccount familyAccount);
}
