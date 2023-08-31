package io.alviss.recipe_api.user;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Transient;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.alviss.recipe_api.auth.validators.PasswordMatch;
import io.alviss.recipe_api.auth.validators.ValidateEnum;
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
public class UserDTO implements UserDetails {

    private UUID id;

    public UserDTO(String email, String password, String name, String gender, String country) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.country = country;
    }

    @NotNull
    @Size(max = 255)
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Enter a valid email!")
    private String email;

    @NotNull
    @Size(min = 8, max = 255)
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least 1 number!")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull
    @Size(max = 255)
    @NotEmpty
    @NotBlank
    private String name;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String confirmPassword;

    @NotNull
    @ValidateEnum(targetClassType = Gender.class)
    private String gender;

    @NotNull
    @NotEmpty
    @NotBlank
    private String country;

    private VerificationToken verificationToken;

    private boolean enabled;

    @NotNull
    private boolean accountNonLocked;

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
