package io.alviss.recipe_api.recipe;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.alviss.recipe_api.user.User;


@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    boolean existsByDescriptionIgnoreCase(String description);

    Set<Recipe> findByAuthor (User author);

}
