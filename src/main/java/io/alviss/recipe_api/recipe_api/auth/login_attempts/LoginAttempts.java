package io.alviss.recipe_api.recipe_api.auth.login_attempts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import io.alviss.recipe_api.recipe_api.user.User;

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

    @OneToOne(targetEntity = User.class, cascade = CascadeType.ALL)
    private final User user;


}
