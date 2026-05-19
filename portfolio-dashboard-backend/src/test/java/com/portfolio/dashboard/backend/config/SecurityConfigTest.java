package com.portfolio.dashboard.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
        ReflectionTestUtils.setField(securityConfig, "adminUsername", "admin");
        ReflectionTestUtils.setField(securityConfig, "adminPassword", "secret");
    }

    @Test
    void passwordEncoderMatchesRawPassword() {
        assertThat(securityConfig.passwordEncoder().matches("secret", securityConfig.passwordEncoder().encode("secret")))
                .isTrue();
    }

    @Test
    void userDetailsServiceUsesConfiguredAdminCredentials() {
        UserDetailsService userDetailsService = securityConfig.userDetailsService();

        UserDetails user = userDetailsService.loadUserByUsername("admin");

        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
        assertThat(securityConfig.passwordEncoder().matches("secret", user.getPassword())).isTrue();
    }
}
