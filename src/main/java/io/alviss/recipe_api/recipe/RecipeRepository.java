package io.alviss.recipe_api.recipe;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    boolean existsByDescriptionIgnoreCase(String description);

}
