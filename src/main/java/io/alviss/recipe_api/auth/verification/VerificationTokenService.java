package io.alviss.recipe_api.auth.verification;

import io.alviss.recipe_api.user.User;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    
    @Transactional
    public VerificationToken create (final User user, final UUID token) {
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

    // public VerificationToken findByUserDTO (final UserDTO user) {
    //     return verificationTokenRepository.findByUser(userService.mapToEntity(user, new User()));
    // }

    public VerificationToken findByUser (final User user) {

        return verificationTokenRepository.findByUser(user);
    }

    public void update (final VerificationToken token) {
        final User user = token.getUser();
        user.setEnabled(true);

        token.setUser(user);

        verificationTokenRepository.save(token);
    }
}
