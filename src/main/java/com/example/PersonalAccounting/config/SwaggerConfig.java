package com.example.PersonalAccounting.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api") // Назва групи
                .pathsToMatch("/api/**") // Шлях до API
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin-api") // Назва групи для адмінських ендпоінтів
                .pathsToMatch("/admin/**") // Шлях до адмінських ендпоінтів
                .build();
    }
}
