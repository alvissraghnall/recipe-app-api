package io.alviss.recipe_api.recipe_api.auth.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GenderEnumValidator implements ConstraintValidator<ValidateGender, String> {

    private Set<String> allowedValues;

    @Override
    public void initialize(ValidateGender targetGender) {
        Class<? extends Enum> enumSelected = targetGender.targetClassType();
        allowedValues = (Set<String>) EnumSet.allOf(enumSelected).stream().map(e -> ((Enum<? extends Enum<?>>) e).name())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && allowedValues.contains(value);
    }
}
