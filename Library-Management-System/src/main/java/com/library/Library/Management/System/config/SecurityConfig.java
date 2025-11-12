package com.library.Library.Management.System.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // ✅ 1. Password encryption bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ 2. Modern Spring Security setup (no deprecation)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (only for development)
                .csrf(AbstractHttpConfigurer::disable)

                // ✅ Authorization configuration (new DSL)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/test/**").permitAll()
                        .anyRequest().authenticated()
                )

                // ✅ Modern HTTP Basic configuration
                .httpBasic(httpBasic -> {});  // Empty lambda = default behavior

        return http.build();
    }
}
