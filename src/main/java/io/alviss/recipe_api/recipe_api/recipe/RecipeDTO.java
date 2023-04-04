package io.alviss.recipe_api.recipe_api.recipe;

import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.alviss.recipe_api.recipe_api.model.Ingredient;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RecipeDTO {

    private UUID id;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 255)
    private String image;

    private Integer prepTime;

    private Integer servingSize;

    @NotNull
    @Size(max = 255)
    private String description;

    private Integer rating;

    @NotNull
    private Set<Ingredient> ingredients;

    @NotNull
    @Size(max = 255)
    private String category;

    private String nutrients;

    private Integer cookTime;

    @NotNull
    @Size(max = 255)
    private String country;

    @NotNull
    private UUID user;

}
