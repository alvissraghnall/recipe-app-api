package io.alviss.recipe_api.recipe_api.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class LoginAttempts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private int attempts;

    @OneToOne(targetEntity = User.class)
    private User user;


}
