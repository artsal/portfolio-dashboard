package com.portfolio.dashboard.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class WebConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Allow these origins (adjust Netlify domain once live)
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",     // Vite dev
                "http://localhost:4173",     // Vite preview
                "https://arthur-portfolio-dashboard.netlify.app" // Netlify production
                                        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        // ✅ Apply CORS globally
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        System.out.println("✅ Global CORS filter active for origins: " + config.getAllowedOrigins());
        return new CorsFilter(source);
    }
}
