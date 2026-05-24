package org.gca.schoolms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/webjars/**", "/login", "/error", "/favicon.ico").permitAll()
                .requestMatchers("/", "/dashboard").authenticated()
                .requestMatchers("/portal/guardian/**").hasAnyRole("PARENT_GUARDIAN", "SYSTEM_ADMIN")
                .requestMatchers("/admin/imports/**").hasAnyRole("SYSTEM_ADMIN", "SCHOOL_ADMIN", "SCHOOL_STAFF")
                .requestMatchers("/reports/**").hasAnyRole("SYSTEM_ADMIN", "SCHOOL_ADMIN", "SCHOOL_STAFF")
                .requestMatchers("/records/**").hasAnyRole("SYSTEM_ADMIN", "SCHOOL_ADMIN", "SCHOOL_STAFF", "GUIDANCE_COUNSELOR")
                .requestMatchers("/finance/**").hasAnyRole("SYSTEM_ADMIN", "SCHOOL_ADMIN", "SCHOOL_FINANCE", "SCHOOL_CASHIER")
                .requestMatchers("/academics/**").hasAnyRole("SYSTEM_ADMIN", "SCHOOL_ADMIN", "SCHOOL_STAFF", "GUIDANCE_COUNSELOR")
                .anyRequest().authenticated())
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/dashboard", true).permitAll())
            .logout(Customizer.withDefaults())
            .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
