package io.alviss.recipe_api.auth.login_attempts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import javax.persistence.*;

import io.alviss.recipe_api.user.User;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class LoginAttempts {

    public LoginAttempts (User user, int attempts) {
        this.user = user;
        this.attempts = attempts;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private int attempts = 0;

    @OneToOne(targetEntity = User.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final User user;

    @Column
    private LocalDateTime lastFailedLoginAttempt;
}
