package io.alviss.recipe_api.recipe_api.favourites;

import java.util.UUID;
import io.alviss.recipe_api.recipe_api.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FavouritesRepository extends JpaRepository<User, UUID> {

    // boolean existsByDescriptionIgnoreCase(String description);

}
