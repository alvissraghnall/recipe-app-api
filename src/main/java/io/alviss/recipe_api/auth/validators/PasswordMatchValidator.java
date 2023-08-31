package io.alviss.recipe_api.auth.validators;

import io.alviss.recipe_api.user.UpdateUserPasswordDTO;
import io.alviss.recipe_api.user.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    @Override
    public void initialize(PasswordMatch passwordMatch) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof UserDTO) {
            UserDTO user = (UserDTO) obj;
            return user.getConfirmPassword() != null && user.getConfirmPassword().equals(user.getPassword());
        } else if (obj instanceof UpdateUserPasswordDTO) {
            final UpdateUserPasswordDTO user = (UpdateUserPasswordDTO) obj;
            return user.getConfirmNewPassword() != null && user.getConfirmNewPassword().equals(user.getNewPassword());
        }
        return false;
    }

}
