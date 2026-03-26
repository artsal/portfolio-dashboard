package com.portfolio.dashboard.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 🔐 Read credentials from environment variables or properties
    @Value("${VITE_ADMIN_USERNAME}")
    private String adminUsername;

    @Value("${VITE_ADMIN_PASSWORD}")
    private String adminPassword;

    // ✅ Main Security Configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for API use cases (we’re using tokenless REST)
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // ✅ enable CORS handling

        // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public access for GET requests
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        // Allow contact form submissions without authentication
                        .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
                        // Require authentication for all other data-modifying requests
                        .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()
                        // Everything else allowed
                        .anyRequest().permitAll()
                                      )

                // Enable HTTP Basic auth (modern style)
                .httpBasic(httpBasic -> {
                });

        return http.build();
    }

    // ✅ Define in-memory admin user
    @Bean
    public UserDetailsService userDetailsService() {
        var user = User.builder()
                .username(adminUsername)
                .password(passwordEncoder().encode(adminPassword))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    // ✅ Password encoder for secure hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}