package io.alviss.recipe_api.recipe_api.config.exception;

public class InvalidPasswordException extends Throwable {

    public InvalidPasswordException() {
        super("Password entered does not match. Verify, and try again.");
    }

    public InvalidPasswordException (String msg) { super(msg); }
}
