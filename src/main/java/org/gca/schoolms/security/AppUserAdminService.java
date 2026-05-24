package org.gca.schoolms.security;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserAdminService {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserAdminService(AppUserRepository appUserRepository, AppRoleRepository appRoleRepository,
                               PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<AppUser> users() {
        return appUserRepository.findAllByOrderByUsernameAsc();
    }

    @Transactional(readOnly = true)
    public List<AppRole> roles() {
        return appRoleRepository.findAllByOrderByCodeAsc();
    }

    @Transactional(readOnly = true)
    public AppUserForm buildForm(String username) {
        AppUserForm form = new AppUserForm();
        if (username == null || username.isBlank()) {
            return form;
        }
        AppUser appUser = appUserRepository.findByUsername(normalizeUsername(username)).orElseThrow();
        form.setUsername(appUser.getUsername());
        form.setDisplayName(appUser.getDisplayName());
        form.setEmail(appUser.getEmail());
        form.setEnabled(appUser.isEnabled());
        form.setRoles(appUser.getRoles().stream().map(AppRole::getCode).collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new)));
        return form;
    }

    @Transactional
    public void saveUser(AppUserForm form) {
        String username = normalizeUsername(form.getUsername());
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (form.getRoles() == null || form.getRoles().isEmpty()) {
            throw new IllegalArgumentException("Select at least one role.");
        }
        Set<AppRole> roles = form.getRoles().stream()
            .map(roleName -> appRoleRepository.findByCode(roleName).orElseThrow())
            .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
        AppUser existing = appUserRepository.findByUsername(username).orElse(null);
        if (existing == null) {
            if (form.getPassword() == null || form.getPassword().isBlank()) {
                throw new IllegalArgumentException("Password is required for new users.");
            }
            AppUser appUser = new AppUser(
                username,
                passwordEncoder.encode(form.getPassword()),
                form.isEnabled(),
                blankToNull(form.getDisplayName()),
                blankToNull(form.getEmail())
            );
            appUser.updateAccount(
                appUser.getPasswordHash(),
                form.isEnabled(),
                blankToNull(form.getDisplayName()),
                blankToNull(form.getEmail()),
                roles
            );
            appUserRepository.save(appUser);
            return;
        }
        existing.updateAccount(
            form.getPassword() == null || form.getPassword().isBlank() ? null : passwordEncoder.encode(form.getPassword()),
            form.isEnabled(),
            blankToNull(form.getDisplayName()),
            blankToNull(form.getEmail()),
            roles
        );
        appUserRepository.save(existing);
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
