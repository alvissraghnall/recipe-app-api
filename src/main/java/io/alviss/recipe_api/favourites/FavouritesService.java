package io.alviss.recipe_api.favourites;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.alviss.recipe_api.recipe.Recipe;
import io.alviss.recipe_api.recipe.RecipeDTO;
import io.alviss.recipe_api.recipe.RecipeService;
import io.alviss.recipe_api.user.User;
import io.alviss.recipe_api.user.UserDTO;
import io.alviss.recipe_api.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavouritesService {

    private final UserService userService;

    private final RecipeService recipeService;
    
    public void addRecipe (
        final UserDTO authUser,
        String recipeId
    ) {
        RecipeDTO recipe = recipeService.find(UUID.fromString(recipeId));

        Set<Recipe> userRecipes = authUser.getRecipes();

        userRecipes.add(recipeService.mapToEntity(recipe, new Recipe()));

        authUser.setRecipes(userRecipes);

        User updatedUser = userService.mapToEntity(authUser, new User());

        userService.saveUpdated(updatedUser);

        return;
    }

    public void removeRecipe (
        final UserDTO authUser,
        String recipeId
    ) {
        authUser.getRecipes().removeIf(rec -> rec.getId().equals(UUID.fromString(recipeId)));

        userService.saveUpdated(userService.mapToEntity(authUser, new User()));
    }

    public Set<RecipeDTO> getUserRecipes (
        final UserDTO authUser
    ) {
        return authUser.getRecipes().stream().map(rec -> recipeService.mapToDTO(rec, new RecipeDTO())).collect(Collectors.toSet());
    }
}
