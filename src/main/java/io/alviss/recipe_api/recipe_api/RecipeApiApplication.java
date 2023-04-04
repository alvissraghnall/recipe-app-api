package io.alviss.recipe_api.recipe_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication
public class RecipeApiApplication {

    public static void main(final String[] args) {

        SpringApplication.run(RecipeApiApplication.class, args);
    }

}
