package io.alviss.recipe_api.user;

import io.alviss.recipe_api.auth.login_attempts.LoginAttempts;
import io.alviss.recipe_api.auth.verification.VerificationToken;
import io.alviss.recipe_api.model.Gender;
import io.alviss.recipe_api.recipe.Recipe;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "\"user\"")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 25)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String country;

    @OneToOne(targetEntity = VerificationToken.class)
    private VerificationToken verificationToken;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean enabled;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Recipe> recipes;

    @ManyToMany
    private Set<Recipe> favourites = new HashSet<Recipe>();

    @Column
    private boolean accountNonLocked;

    @OneToOne(targetEntity = LoginAttempts.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private LoginAttempts loginAttempts;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
