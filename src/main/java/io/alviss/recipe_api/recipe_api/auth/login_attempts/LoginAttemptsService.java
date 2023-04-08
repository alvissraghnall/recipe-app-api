package io.alviss.recipe_api.recipe_api.auth.login_attempts;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import io.alviss.recipe_api.recipe_api.user.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginAttemptsService {

    private LoginAttemptsRepository loginAttemptsRepository;
    
    public void incrementAttemptsForUser (User user) {
        LoginAttempts attempts = loginAttemptsRepository.findLoginAttemptsByUser(user)
            .orElse(new LoginAttempts(user));
        attempts.setAttempts(attempts.getAttempts()+1);

        loginAttemptsRepository.save(attempts);
    }
}
