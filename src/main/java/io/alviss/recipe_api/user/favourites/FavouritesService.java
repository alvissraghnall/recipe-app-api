package io.alviss.recipe_api.user.favourites;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import io.alviss.recipe_api.recipe.Recipe;
import io.alviss.recipe_api.user.UserDTO;
import io.alviss.recipe_api.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavouritesService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Set<Recipe> getUserRecipes (
        final UserDTO authUser
    ) {
        authUser.getFavourites().size();
        System.out.println(authUser.toString());

        return authUser.getFavourites();
    }

    @Transactional(readOnly = true)
    public void toggleRecipeInFavorites(final UserDTO user, Recipe recipe) {
        Set<Recipe> favorites = user.getFavourites();

        if (favorites.contains(recipe)) {
            favorites.remove(recipe);
        } else {
            favorites.add(recipe);
        }

        user.setFavourites(favorites);

        // Update the user to persist the changes
        userRepository.save(user);
    }
}
