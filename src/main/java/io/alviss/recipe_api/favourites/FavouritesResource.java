package io.alviss.recipe_api.favourites;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.alviss.recipe_api.auth.MessageResponse;
import io.alviss.recipe_api.recipe.RecipeDTO;
import io.alviss.recipe_api.user.UserDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/favourites")
@RequiredArgsConstructor
public class FavouritesResource {

    private final FavouritesService favouritesService;

    @PostMapping("/add/{recipeId}")
    public ResponseEntity<MessageResponse> addToFavorites(
        @PathVariable String recipeId,
        @AuthenticationPrincipal UserDTO authenticatedUser
    ) {
        
        favouritesService.addRecipe(authenticatedUser, recipeId);
        return ResponseEntity.ok(new MessageResponse("Recipe added to favorites successfully."));
    }

    @DeleteMapping("/remove/{recipeId}")
    public ResponseEntity<MessageResponse> removeFromFavorites(
        @PathVariable String recipeId,
        @AuthenticationPrincipal UserDTO authenticatedUser
    ) {
        favouritesService.removeRecipe(authenticatedUser, recipeId);
        
        return ResponseEntity.ok(new MessageResponse("Recipe removed from favorites successfully."));
    }

    @GetMapping
    public ResponseEntity<Set<RecipeDTO>> getUserFavorites(
        @AuthenticationPrincipal UserDTO authenticatedUser
    ) {
       
        Set<RecipeDTO> favoriteRecipes = favouritesService.getUserRecipes(authenticatedUser);
        return ResponseEntity.ok(favoriteRecipes);
    }

    
}