package com.booknest.wallet.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
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
                .title("BookNest \u2014 Wallet & Payment Service API")
                .description("E-wallet top-up, payment deduction, Razorpay integration, and full transaction statement history.\n\n**Base URL:** `http://localhost:8085`")
                .version("1.0.0")
                .contact(new Contact()
                    .name("BookNest Team")
                    .email("support@booknest.com")))
            .servers(List.of(
                new Server().url("http://localhost:8085").description("Local Development")))
            .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
            .components(new Components()
                .addSecuritySchemes("BearerAuth", new SecurityScheme()
                    .name("BearerAuth")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token from POST /auth/login")));
    }
}
