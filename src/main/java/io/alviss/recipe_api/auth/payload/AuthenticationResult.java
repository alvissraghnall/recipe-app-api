package io.alviss.recipe_api.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class AuthenticationResult {
    private boolean success;
    private String message;
    private JwtResponse jwtResponse;
}
