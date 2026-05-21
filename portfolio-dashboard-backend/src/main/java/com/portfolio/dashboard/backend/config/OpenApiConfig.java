package com.portfolio.dashboard.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI portfolioDashboardOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server()
                        .url("/pdbapp")
                        .description("Local backend context path"))
                .schemaRequirement("basicAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")
                        .description("HTTP Basic authentication for admin-only create, update, and delete endpoints."))
                .info(new Info()
                        .title("Portfolio Dashboard API")
                        .version("1.0.0")
                        .description("REST API for portfolio projects, skills, certifications, overview metrics, and contact form submissions.")
                        .contact(new Contact()
                                .name("Arthur Salla")
                                .email("arthurs81@gmail.com"))
                        .license(new License()
                                .name("Portfolio Dashboard")));
    }
}
