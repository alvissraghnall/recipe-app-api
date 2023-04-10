package io.alviss.recipe_api.recipe_api.auth.login_attempts;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import io.alviss.recipe_api.recipe_api.user.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginAttemptsService {

    private LoginAttemptsRepository loginAttemptsRepository;
    
    public boolean incrementAttemptsAndCheckAcctLocked (User user) {
        LoginAttempts attempts = loginAttemptsRepository.findLoginAttemptsByUser(user)
            .orElse(new LoginAttempts(user));
        attempts.setAttempts(attempts.getAttempts()+1);

        if(attempts.getAttempts() >= 5 && user.isAccountNonLocked() == true) {
            attempts.setLastFailedLoginAttempt(LocalDateTime.now());
            user.setAccountNonLocked(false);
        }

        if (user.isAccountNonLocked() == false && attempts.getLastFailedLoginAttempt().isAfter(attempts.getLastFailedLoginAttempt().plusHours(5))) {
            user.setAccountNonLocked(true);
        }

        loginAttemptsRepository.save(attempts);

        return user.isAccountNonLocked() == true ? attempts.getAttempts() < 5 : true;
        
    }

    // protected boolean checkAccountLockedStatus (User user) {
    //     return user.isAccountNonLocked() == false && l
    // }

    public void resetAttempts (User user) {
        LoginAttempts attempts = loginAttemptsRepository.findLoginAttemptsByUser(user)
            .get();
        attempts.setAttempts(0);
        attempts.setLastFailedLoginAttempt(null);

        loginAttemptsRepository.save(attempts);
    }
}
