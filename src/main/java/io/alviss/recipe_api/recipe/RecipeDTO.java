package io.alviss.recipe_api.recipe;

import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.alviss.recipe_api.auth.validators.ValidateEnum;
import io.alviss.recipe_api.model.Category;
import io.alviss.recipe_api.model.Ingredient;
import io.alviss.recipe_api.user.User;
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
    @NotEmpty
    private Set<Ingredient> ingredients;

    @NotNull
    @Size(max = 255)
    @ValidateEnum(targetClassType = Category.class, message = "Pass in a valid Category. Supported values are: 'BREAKFAST', 'LUNCH' & 'DINNER' ")
    private String category;

    private String nutrients;

    private Integer cookTime;

    @NotNull
    @Size(max = 255)
    private String country;

    private User author;

}
