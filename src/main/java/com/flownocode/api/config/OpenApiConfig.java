package com.flownocode.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Flow No Code API")
                        .description("""
                                Workflow Engine — Technical Challenge API.

                                Use the request/response examples on each endpoint to try the \
                                Prime Number Validation Flow (same payloads documented in the README).
                                """)
                        .version("v1"));
    }
}
