package io.alviss.recipe_api.recipe_api.config;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FieldError {

    private String field;
    private String errorCode;
    private String message;

}
