package io.alviss.recipe_api.auth.login_attempts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.alviss.recipe_api.user.User;

import java.util.Optional;

@Repository
public interface LoginAttemptsRepository extends JpaRepository<LoginAttempts, Integer> {

    Optional<LoginAttempts> findLoginAttemptsByUser(User user);


}
