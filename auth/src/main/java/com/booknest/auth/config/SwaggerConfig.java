package com.booknest.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BookNest — Auth / User Service API")
                        .description("""
                            Authentication & user management service for the BookNest e-commerce bookstore platform.
                            
                            **Responsibilities:**
                            - Customer & Admin registration
                            - Email/password login → JWT token
                            - GitHub OAuth2 login
                            - Token refresh & logout
                            - User profile management
                            - Password change
                            
                            **Base URL:** `http://localhost:8081`
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("BookNest Team")
                                .email("support@booknest.com"))
                        .license(new License()
                                .name("MIT License")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local Development")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter the JWT token obtained from POST /auth/login")));
    }
}
