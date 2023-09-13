package io.alviss.recipe_api.user;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import io.alviss.recipe_api.auth.validators.PasswordMatch;
import io.alviss.recipe_api.auth.verification.VerificationToken;
import io.alviss.recipe_api.model.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Getter
@Setter
@NoArgsConstructor
@PasswordMatch
public class UserDTO extends User implements UserDetails {

    private UUID id;

    public UserDTO(String email, String password, String name, Gender gender, String country) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.country = country;
    }

    private String email;

    private String password;

    private String name;

    private Gender gender;

    private String country;

    private VerificationToken verificationToken;

    private boolean enabled = false;

    private boolean accountNonLocked = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO user = (UserDTO) o;
        return Objects.equals(id, user.id);
    }
}
