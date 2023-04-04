package io.alviss.recipe_api.recipe_api.auth.verification;

import io.alviss.recipe_api.recipe_api.user.User;
import io.alviss.recipe_api.recipe_api.user.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationToken create (final User user, final String token) {
        final VerificationTokenDTO newToken = new VerificationTokenDTO(token, user);
        final VerificationToken newTokenEntity = newToken.mapDtoToEntity();
        verificationTokenRepository.save(newTokenEntity);
        return newTokenEntity;
    }

    public VerificationToken find (final String token) {
        return verificationTokenRepository.findByToken(
                token
        );
    }

    public VerificationToken findByUser (final User user) {
        return verificationTokenRepository.findByUser(user);
    }
}
