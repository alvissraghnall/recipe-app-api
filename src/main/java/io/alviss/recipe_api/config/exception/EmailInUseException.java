package io.alviss.recipe_api.config.exception;

import org.springframework.security.core.AuthenticationException;

public class EmailInUseException extends AuthenticationException {
    
    public EmailInUseException() {
        super("Email address already in use");
    }

    public EmailInUseException (String msg) { super(msg); }
}

