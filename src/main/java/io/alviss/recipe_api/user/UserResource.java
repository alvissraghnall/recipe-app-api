package io.alviss.recipe_api.user;

import io.alviss.recipe_api.config.exception.InvalidPasswordException;
import io.alviss.recipe_api.recipe.RecipeDTO;
import io.alviss.recipe_api.recipe.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {

    private final UserService userService;

    private final RecipeService recipeService;

    private final PasswordEncoder passwordEncoder;

    public UserResource(final UserService userService, PasswordEncoder passwordEncoder, final RecipeService recipeService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable final UUID id) {
        return ResponseEntity.ok(userService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable final UUID id,
            @RequestBody @Valid final UserDTO userDTO) {
        userService.update(id, userDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-password/{id}")
    public ResponseEntity<Void> updateUserPassword (@PathVariable final String id,
                                                    @RequestBody @Valid final UpdateUserPasswordDTO updateUserPasswordDTO,
                                                    @AuthenticationPrincipal UserDTO authUser,
                                                    @CurrentSecurityContext UserDTO ctx
                                                    ) throws InvalidPasswordException {

//        if ()

        final UUID userId = UUID.fromString(id);

        final UserDTO user = userService.get(userId);

        final boolean isValidPassword = passwordEncoder.matches(updateUserPasswordDTO.getOldPassword(), user.getPassword());

        if (!isValidPassword) {
            throw new InvalidPasswordException("Password entered incorrect!");
        }

        user.setPassword(updateUserPasswordDTO.getNewPassword());
        userService.update(userId, user);

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    @Operation(summary = "Delete a specific user")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "The unique identifier of the user") @PathVariable final UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    
    /**
     * Retrieve a list of recipes created by a specific user.
     *
     * @param id The unique identifier of the user.
     * @return A list of recipes created by the user.
     */
    @GetMapping("/recipes")
    @Operation(summary = "Retrieve a list of recipes created by a specific user")
    public ResponseEntity<Set<RecipeDTO>> getUserRecipes (            
        @AuthenticationPrincipal UserDTO authenticatedUser
    ) {
        Set<RecipeDTO> userRecipes = recipeService.getRecipesByUser(authenticatedUser);

        if (userRecipes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(userRecipes);
        }
    }

}
