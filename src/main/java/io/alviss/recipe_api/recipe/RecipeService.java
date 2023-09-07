package io.alviss.recipe_api.recipe;

import io.alviss.recipe_api.model.Category;
import io.alviss.recipe_api.user.User;
import io.alviss.recipe_api.user.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(final RecipeRepository recipeRepository,
            final UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<RecipeDTO> findAll() {
        return recipeRepository.findAll(Sort.by("id"))
                .stream()
                .map(recipe -> mapToDTO(recipe, new RecipeDTO()))
                .collect(Collectors.toList());
    }

    public RecipeDTO find (final UUID id) {
        return recipeRepository.findById(id)
                .map(recipe -> mapToDTO(recipe, new RecipeDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe with ID provided not found!"));
    }

    // Internal method that returns the entity
    public Recipe findRecipeById(final UUID id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe with ID provided not found!"));
    }

    public RecipeDTO create(final RecipeDTO recipeDTO) {
        final Recipe recipe = new Recipe();
        mapToEntity(recipeDTO, recipe);
        recipeRepository.save(recipe);
        return recipeDTO;
    }

    public void update(final UUID id, final RecipeDTO recipeDTO) {
        final Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(recipeDTO, recipe);
        recipeRepository.save(recipe);
    }

    public void delete(final UUID id) throws IllegalArgumentException {
        recipeRepository.deleteById(id);
    }

    /**
     * Get a list of recipes created by a specific user.
     *
     * @param user The user for whom to retrieve recipes.
     * @return A list of recipes created by the user.
     */
    public Set<RecipeDTO> getRecipesByUser(User user) {
        return recipeRepository.findByAuthor(user).stream().map(r -> mapToDTO(r, new RecipeDTO())).collect(Collectors.toSet());
    }

    // public String 

    public RecipeDTO mapToDTO(final Recipe recipe, final RecipeDTO recipeDTO) {
        recipeDTO.setId(recipe.getId());
        recipeDTO.setName(recipe.getName());
        recipeDTO.setImage(recipe.getImage());
        recipeDTO.setPrepTime(recipe.getPrepTime());
        recipeDTO.setServingSize(recipe.getServingSize());
        recipeDTO.setDescription(recipe.getDescription());
        recipeDTO.setRating(recipe.getRating());
        recipeDTO.setIngredients(recipe.getIngredients());
        recipeDTO.setCategory(recipe.getCategory().name());
        recipeDTO.setNutrients(recipe.getNutrients());
        recipeDTO.setCookTime(recipe.getCookTime());
        recipeDTO.setCountry(recipe.getCountry());
        recipeDTO.setAuthor(recipe.getAuthor());
        return recipeDTO;
    }

    public Recipe mapToEntity(final RecipeDTO recipeDTO, final Recipe recipe) {
        recipe.setName(recipeDTO.getName());
        recipe.setImage(recipeDTO.getImage());
        recipe.setPrepTime(recipeDTO.getPrepTime());
        recipe.setServingSize(recipeDTO.getServingSize());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setRating(recipeDTO.getRating());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setCategory(Category.valueOf(recipeDTO.getCategory()));
        recipe.setNutrients(recipeDTO.getNutrients());
        recipe.setCookTime(recipeDTO.getCookTime());
        recipe.setCountry(recipeDTO.getCountry());
        final User user = recipeDTO.getAuthor();
        recipe.setAuthor(user);
        return recipe;
    }

    public boolean descriptionExists(final String description) {
        return recipeRepository.existsByDescriptionIgnoreCase(description);
    }

}
