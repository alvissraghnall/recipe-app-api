package io.alviss.recipe_api.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi recipeApi() {
        return GroupedOpenApi.builder()
                .group("recipe-api")
                .pathsToMatch("/api/v1/**") // Define the paths to include in the documentation
                .build();
    }
}
