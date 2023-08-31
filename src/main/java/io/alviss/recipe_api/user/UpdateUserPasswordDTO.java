package io.alviss.recipe_api.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

import io.alviss.recipe_api.auth.validators.PasswordMatch;

@Getter
@Setter
@PasswordMatch
public class UpdateUserPasswordDTO {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmNewPassword;
}
