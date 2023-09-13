package io.alviss.recipe_api.recipe;

import io.alviss.recipe_api.auth.MessageResponse;
import io.alviss.recipe_api.config.cloudinary.CloudinaryService;
import io.alviss.recipe_api.user.User;
import io.alviss.recipe_api.user.UserDTO;
import io.alviss.recipe_api.user.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cloudinary.utils.ObjectUtils;


@RestController
@AllArgsConstructor
@RequestMapping(value = "/recipes", produces = MediaType.APPLICATION_JSON_VALUE)
public class RecipeResource {

    private final RecipeService recipeService;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;
    

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipe(@PathVariable final UUID id) {
        RecipeDTO recipe = recipeService.find(id);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<RecipeDTO> createRecipe(
        @RequestBody @Valid final RecipeDTO recipeDTO,
        @AuthenticationPrincipal UserDTO user
    ) {
        final User mainUser = userService.mapToEntity(user, new User());
        recipeDTO.setAuthor(mainUser);
        RecipeDTO createdRecipe = recipeService.create(recipeDTO);
        return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRecipe.getId())
                .toUri()
        ).body(createdRecipe);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRecipe(
            @PathVariable final UUID id,
            @RequestBody final RecipeDTO recipeDTO,
            @AuthenticationPrincipal UserDTO user        
    ) {
        recipeService.update(id, recipeDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteRecipe(
        @PathVariable final UUID id,
        @AuthenticationPrincipal UserDTO user        
    ) {
        try {
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/image/upload")
    public ResponseEntity<Map<String, String>> submitRecipeImage (
        @RequestParam("image") MultipartFile imgFile,
        @AuthenticationPrincipal UserDTO user
    ) {
        String imgUrl = cloudinaryService.uploadFile(imgFile);

        return ResponseEntity.status(HttpStatus.CREATED).body((Map<String, String>)ObjectUtils.asMap("url", imgUrl));
    }

}
