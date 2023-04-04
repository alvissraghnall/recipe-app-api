package io.alviss.recipe_api.recipe_api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginAttemptsRepository extends JpaRepository<LoginAttempts, Integer> {

    Optional<User> findLoginAttemptsByUser(User user);


}
