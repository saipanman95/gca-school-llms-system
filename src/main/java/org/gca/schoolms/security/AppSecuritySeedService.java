package org.gca.schoolms.security;

import java.util.List;
import java.util.Set;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AppSecuritySeedService implements ApplicationRunner {

    private final AppRoleRepository appRoleRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppSecuritySeedService(AppRoleRepository appRoleRepository, AppUserRepository appUserRepository,
                                  PasswordEncoder passwordEncoder) {
        this.appRoleRepository = appRoleRepository;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(org.springframework.boot.ApplicationArguments args) {
        ensureRole(RoleName.SYSTEM_ADMIN, "System Admin", "Full system-wide administrative access.");
        ensureRole(RoleName.SCHOOL_ADMIN, "School Admin", "School-level operational administration access.");
        ensureRole(RoleName.SCHOOL_STAFF, "School Staff", "Registrar and staff access for records and academics.");
        ensureRole(RoleName.SCHOOL_FINANCE, "School Finance", "Finance office access for billing and clearance.");
        ensureRole(RoleName.SCHOOL_CASHIER, "School Cashier", "Cashier access for payment posting and receipts.");
        ensureRole(RoleName.GUIDANCE_COUNSELOR, "Guidance Counselor", "Counselor access for attendance and limited student records.");
        ensureRole(RoleName.PARENT_GUARDIAN, "Parent Guardian", "Family portal access for guardians.");
        ensureRole(RoleName.STUDENT, "Student", "Student-facing access.");

        ensureUser("sysadmin", "System Administrator", Set.of(RoleName.SYSTEM_ADMIN));
        ensureUser("principal", "Principal", Set.of(RoleName.SCHOOL_ADMIN));
        ensureUser("registrar", "Registrar", Set.of(RoleName.SCHOOL_STAFF));
        ensureUser("finance", "Finance Office", Set.of(RoleName.SCHOOL_FINANCE));
        ensureUser("cashier", "Cashier", Set.of(RoleName.SCHOOL_CASHIER));
        ensureUser("counselor", "Guidance Counselor", Set.of(RoleName.GUIDANCE_COUNSELOR));
        ensureUser("guardian", "Parent Guardian", Set.of(RoleName.PARENT_GUARDIAN));
        ensureUser("student", "Student", Set.of(RoleName.STUDENT));
    }

    private void ensureRole(RoleName code, String name, String description) {
        if (appRoleRepository.findByCode(code).isPresent()) {
            return;
        }
        appRoleRepository.save(new AppRole(code, name, description));
    }

    private void ensureUser(String username, String displayName, Set<RoleName> roleNames) {
        if (appUserRepository.existsByUsername(username)) {
            return;
        }
        List<AppRole> roles = roleNames.stream()
            .map(roleName -> appRoleRepository.findByCode(roleName).orElseThrow())
            .toList();
        AppUser user = new AppUser(username, passwordEncoder.encode("change-me"), true, displayName, null);
        user.updateAccount(user.getPasswordHash(), true, displayName, null, roles);
        appUserRepository.save(user);
    }
}
