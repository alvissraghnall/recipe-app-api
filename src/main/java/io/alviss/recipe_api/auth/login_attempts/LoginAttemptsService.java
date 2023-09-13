package io.alviss.recipe_api.auth.login_attempts;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.alviss.recipe_api.user.User;
import io.alviss.recipe_api.user.UserRepository;
import io.alviss.recipe_api.user.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class LoginAttemptsService {

    private LoginAttemptsRepository loginAttemptsRepository;
    // private UserService userService;
    private UserRepository userRepository;
    
    @Transactional
    public boolean incrementAttemptsAndCheckAcctLocked (User user) {

        // User managedUser = userRepository.findById(user.getId()).orElse(null);

        // if (managedUser == null) {
        //     // Handle the case where the user is not found (e.g., throw an exception or return an error)
        //     return false;
        // }

        LoginAttempts attempts = loginAttemptsRepository.findLoginAttemptsByUser(user)
            .orElse(new LoginAttempts(user));
        attempts.setAttempts(attempts.getAttempts() + 1);

        if (attempts.getAttempts() >= 5 && user.isAccountNonLocked()) {
            attempts.setLastFailedLoginAttempt(LocalDateTime.now());
            user.setAccountNonLocked(false);
        }
    
        if (!user.isAccountNonLocked()) {
            if (attempts.getLastFailedLoginAttempt() == null || attempts.getLastFailedLoginAttempt().isBefore(LocalDateTime.now().minusHours(5))) {
                user.setAccountNonLocked(true);
            }
        }
    

        user.setLoginAttempts(attempts);
        // userService.saveUpdated(user);

        // attempts.setUser(user);
    
        // loginAttemptsRepository.save(attempts);

        return user.isAccountNonLocked() || attempts.getAttempts() < 5;
        
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

    public Optional<LoginAttempts> getByUser (User user) {
        return loginAttemptsRepository.findLoginAttemptsByUser(user);
    }
}
