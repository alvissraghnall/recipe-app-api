package io.alviss.recipe_api.recipe_api.auth.validators;

import io.alviss.recipe_api.recipe_api.user.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserDTO> {

    @Override
    public void initialize(PasswordMatch passwordMatch) {
    }

    @Override
    public boolean isValid(UserDTO user, ConstraintValidatorContext context) {
        return user.getConfirmPassword() != null && user.getConfirmPassword().equals(user.getPassword());
    }
}
