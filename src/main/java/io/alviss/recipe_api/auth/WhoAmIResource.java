package io.alviss.recipe_api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.alviss.recipe_api.user.UserDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/whoami")
@RequiredArgsConstructor
public class WhoAmIResource {

    // private final UserService userService;
    
    @GetMapping
    public ResponseEntity<UserDTO> getAuthenticatedUser (
        @AuthenticationPrincipal UserDTO authUser
    ) {
        System.out.println(authUser);
        return ResponseEntity.ok(authUser);
    }
    
}
