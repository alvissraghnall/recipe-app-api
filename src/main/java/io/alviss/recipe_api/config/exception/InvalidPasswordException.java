package io.alviss.recipe_api.config.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidPasswordException extends AuthenticationException {

    public InvalidPasswordException() {
        super("Password entered is incorrect. Verify, and try again.");
    }

    public InvalidPasswordException (String msg) { super(msg); }
}
