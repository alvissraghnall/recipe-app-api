package io.alviss.recipe_api.auth.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Constraint(validatedBy = PasswordMatchValidator.class)
public @interface PasswordMatch {

    String message() default "Passwords must match!";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload() default {};

}
