package io.alviss.recipe_api.auth.mail;

public interface VerificationEmailService {

    void sendMessage(String to, String text);
}
