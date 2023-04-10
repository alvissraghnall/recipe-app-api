package io.alviss.recipe_api.recipe_api.recipe;

import io.alviss.recipe_api.recipe_api.user.User;
import io.alviss.recipe_api.recipe_api.user.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RecipeService(final RecipeRepository recipeRepository,
            final UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    public List<RecipeDTO> findAll() {
        return recipeRepository.findAll(Sort.by("id"))
                .stream()
                .map(recipe -> mapToDTO(recipe, new RecipeDTO()))
                .collect(Collectors.toList());
    }

    public RecipeDTO get(final UUID id) {
        return recipeRepository.findById(id)
                .map(recipe -> mapToDTO(recipe, new RecipeDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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

    // public String 

    private RecipeDTO mapToDTO(final Recipe recipe, final RecipeDTO recipeDTO) {
        recipeDTO.setId(recipe.getId());
        recipeDTO.setName(recipe.getName());
        recipeDTO.setImage(recipe.getImage());
        recipeDTO.setPrepTime(recipe.getPrepTime());
        recipeDTO.setServingSize(recipe.getServingSize());
        recipeDTO.setDescription(recipe.getDescription());
        recipeDTO.setRating(recipe.getRating());
        recipeDTO.setIngredients(recipe.getIngredients());
        recipeDTO.setCategory(recipe.getCategory());
        recipeDTO.setNutrients(recipe.getNutrients());
        recipeDTO.setCookTime(recipe.getCookTime());
        recipeDTO.setCountry(recipe.getCountry());
        recipeDTO.setUser(recipe.getUser() == null ? null : recipe.getUser().getId());
        return recipeDTO;
    }

    private Recipe mapToEntity(final RecipeDTO recipeDTO, final Recipe recipe) {
        recipe.setName(recipeDTO.getName());
        recipe.setImage(recipeDTO.getImage());
        recipe.setPrepTime(recipeDTO.getPrepTime());
        recipe.setServingSize(recipeDTO.getServingSize());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setRating(recipeDTO.getRating());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setCategory(recipeDTO.getCategory());
        recipe.setNutrients(recipeDTO.getNutrients());
        recipe.setCookTime(recipeDTO.getCookTime());
        recipe.setCountry(recipeDTO.getCountry());
        final User user = recipeDTO.getUser() == null ? null : userRepository.findById(recipeDTO.getUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        recipe.setUser(user);
        return recipe;
    }

    public boolean descriptionExists(final String description) {
        return recipeRepository.existsByDescriptionIgnoreCase(description);
    }

}
