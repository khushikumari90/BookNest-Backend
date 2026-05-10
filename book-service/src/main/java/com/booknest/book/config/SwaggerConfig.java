package com.booknest.book.config;

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
                .title("BookNest — Book / Catalog Service API")
                .description("""
                    Book catalog microservice for BookNest e-commerce bookstore.
                    
                    **Responsibilities:**
                    - Full book catalog CRUD (Admin)
                    - Search by title, author, genre, ISBN, keyword
                    - Price range filtering
                    - Featured books curation
                    - Stock & rating management
                    
                    **Base URL:** `http://localhost:8082`
                    """)
                .version("1.0.0")
                .contact(new Contact().name("BookNest Team").email("support@booknest.com")))
            .servers(List.of(new Server().url("http://localhost:8082").description("Local Development")))
            .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
            .components(new Components().addSecuritySchemes("BearerAuth",
                new SecurityScheme().type(SecurityScheme.Type.HTTP)
                    .scheme("bearer").bearerFormat("JWT")));
    }
}
