package io.alviss.recipe_api.recipe_api.auth.validators;

import io.alviss.recipe_api.recipe_api.model.Gender;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidateEnum {
    String message () default "Pass in a valid Gender. Supported values are: 'MALE', 'FEMALE', and 'NONBINARY'";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> targetClassType();
}
