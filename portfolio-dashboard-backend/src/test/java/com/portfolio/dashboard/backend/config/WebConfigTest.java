package com.portfolio.dashboard.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;

class WebConfigTest {

    @Test
    void corsFilterIsCreated() {
        CorsFilter corsFilter = new WebConfig().corsFilter();

        assertThat(corsFilter).isNotNull();
    }
}
