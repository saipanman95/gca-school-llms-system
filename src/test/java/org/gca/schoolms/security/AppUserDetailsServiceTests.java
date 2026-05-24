package org.gca.schoolms.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
class AppUserDetailsServiceTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AppUserDetailsService appUserDetailsService;

    @Test
    void loadsUserDetailsFromDatabaseRoles() {
        jdbcTemplate.update("delete from app_user_role where user_id in (select id from app_user where username = ?)", "db-user");
        jdbcTemplate.update("delete from app_user where username = ?", "db-user");
        jdbcTemplate.update(
            """
            insert into app_user (
                username,
                password_hash,
                enabled,
                account_non_expired,
                account_non_locked,
                credentials_non_expired,
                display_name,
                email,
                created_at,
                updated_at
            ) values (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, current_timestamp)
            """,
            "db-user",
            "{bcrypt}$2a$10$CFmAr2baQUZC..fp1JvU2OxSG0idFSEovXSZOoMAAOp8Wck0Aex3e",
            true,
            true,
            true,
            true,
            "Database User",
            null
        );
        jdbcTemplate.update(
            """
            insert into app_user_role (user_id, role_id)
            select u.id, r.id
            from app_user u
            join app_role r on r.code = ?
            where u.username = ?
            """,
            "SYSTEM_ADMIN",
            "db-user"
        );

        UserDetails userDetails = appUserDetailsService.loadUserByUsername("db-user");

        assertEquals("db-user", userDetails.getUsername());
        assertEquals(
            "{bcrypt}$2a$10$CFmAr2baQUZC..fp1JvU2OxSG0idFSEovXSZOoMAAOp8Wck0Aex3e",
            userDetails.getPassword()
        );
        assertTrue(
            userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_SYSTEM_ADMIN"))
        );
    }
}
