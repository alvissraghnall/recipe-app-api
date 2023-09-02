package io.alviss.recipe_api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.alviss.recipe_api.user.UserDTO;

@RestController
@RequestMapping("/whoami")
public class WhoAmIResource {
    
    @GetMapping
    public ResponseEntity<UserDTO> getAuthenticatedUser (
        @AuthenticationPrincipal UserDTO authUser,
        @CurrentSecurityContext UserDTO ctx
    ) {
        System.out.println(authUser);
        System.out.println(ctx);
        return ResponseEntity.ok().body(ctx);
    }
    
}
