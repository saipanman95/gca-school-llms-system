package org.gca.schoolms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/webjars/**", "/login").permitAll()
                .requestMatchers("/", "/dashboard").authenticated()
                .requestMatchers("/portal/guardian/**").hasRole("PARENT_GUARDIAN")
                .requestMatchers("/records/**").hasAnyRole("SYSTEM_ADMIN", "SCHOOL_ADMIN", "SCHOOL_STAFF")
                .requestMatchers("/finance/**").hasAnyRole("SYSTEM_ADMIN", "SCHOOL_ADMIN", "SCHOOL_FINANCE")
                .requestMatchers("/academics/**").hasAnyRole("SYSTEM_ADMIN", "SCHOOL_ADMIN", "SCHOOL_STAFF")
                .anyRequest().authenticated())
            .formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/dashboard", true).permitAll())
            .logout(Customizer.withDefaults())
            .build();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
            User.withUsername("sysadmin").password(passwordEncoder.encode("change-me")).roles("SYSTEM_ADMIN").build(),
            User.withUsername("principal").password(passwordEncoder.encode("change-me")).roles("SCHOOL_ADMIN").build(),
            User.withUsername("registrar").password(passwordEncoder.encode("change-me")).roles("SCHOOL_STAFF").build(),
            User.withUsername("finance").password(passwordEncoder.encode("change-me")).roles("SCHOOL_FINANCE").build(),
            User.withUsername("guardian").password(passwordEncoder.encode("change-me")).roles("PARENT_GUARDIAN").build(),
            User.withUsername("student").password(passwordEncoder.encode("change-me")).roles("STUDENT").build()
        );
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
