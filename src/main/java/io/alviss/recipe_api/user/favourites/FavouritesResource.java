package io.alviss.recipe_api.user.favourites;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.alviss.recipe_api.auth.MessageResponse;
import io.alviss.recipe_api.recipe.Recipe;
import io.alviss.recipe_api.recipe.RecipeDTO;
import io.alviss.recipe_api.recipe.RecipeService;
import io.alviss.recipe_api.user.UserDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/favourites")
@RequiredArgsConstructor
public class FavouritesResource {

    private final FavouritesService favouritesService;
    private final RecipeService recipeService;

    @PostMapping("/toggle/{recipeId}")
    public ResponseEntity<MessageResponse> toggleUserFavorites(
        @PathVariable String recipeId,
        @AuthenticationPrincipal UserDTO authenticatedUser
    ) {
        Recipe recipe = recipeService.findRecipeById(UUID.fromString(recipeId));

        favouritesService.toggleRecipeInFavorites(authenticatedUser, recipe);
        return ResponseEntity.ok(new MessageResponse("Recipe added to favorites successfully."));
    }

    @GetMapping
    public ResponseEntity<Set<RecipeDTO>> getUserFavorites(
        @AuthenticationPrincipal UserDTO authenticatedUser
    ) {
       
        Set<RecipeDTO> favoriteRecipes = favouritesService.getUserRecipes(authenticatedUser).stream().map(rec -> recipeService.mapToDTO(rec, new RecipeDTO())).collect(Collectors.toSet());
        return ResponseEntity.ok(favoriteRecipes);
    }

    
}