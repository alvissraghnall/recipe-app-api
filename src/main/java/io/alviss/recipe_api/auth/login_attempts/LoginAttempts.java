package io.alviss.recipe_api.auth.login_attempts;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import javax.persistence.*;

import io.alviss.recipe_api.user.User;

@Entity
@Data
@NoArgsConstructor
public class LoginAttempts {

    public LoginAttempts (User user) {
        this.user = user;
    }

    public LoginAttempts (User user, int attempts) {
        this.user = user;
        this.attempts = attempts;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private int attempts = 0;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User user;

    @Column
    private LocalDateTime lastFailedLoginAttempt;
}
