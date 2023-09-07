package io.alviss.recipe_api.config.exception;

public class InvalidJwtException extends RuntimeException {

    public InvalidJwtException (String msg) { super(msg); }

}
