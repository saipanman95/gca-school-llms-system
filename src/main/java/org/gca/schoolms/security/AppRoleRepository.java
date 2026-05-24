package org.gca.schoolms.security;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    Optional<AppRole> findByCode(RoleName code);
    java.util.List<AppRole> findAllByOrderByCodeAsc();
}
