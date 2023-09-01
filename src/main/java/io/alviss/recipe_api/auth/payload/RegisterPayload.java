package io.alviss.recipe_api.auth.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.alviss.recipe_api.auth.validators.ValidateEnum;
import io.alviss.recipe_api.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterPayload {
    
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

}
