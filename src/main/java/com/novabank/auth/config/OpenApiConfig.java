package com.novabank.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Nova Bank Auth API")
                                .version("1.0")
                                .description(
                                        "Authentication APIs for Nova Bank"
                                )
                                .contact(
                                        new Contact()
                                                .name("Nova Bank Backend Team")
                                                .email("support@novabank.com")
                                )
                );
    }
}