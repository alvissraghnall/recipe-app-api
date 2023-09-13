package io.alviss.recipe_api.auth;

import io.alviss.recipe_api.auth.login_attempts.LoginAttemptsService;
import io.alviss.recipe_api.auth.mail.VerificationEmailServiceImpl;
import io.alviss.recipe_api.auth.payload.AuthenticationResult;
import io.alviss.recipe_api.auth.payload.JwtResponse;
import io.alviss.recipe_api.auth.payload.LoginPayload;
import io.alviss.recipe_api.auth.payload.RegisterPayload;
import io.alviss.recipe_api.auth.verification.VerificationToken;
import io.alviss.recipe_api.auth.verification.VerificationTokenService;
import io.alviss.recipe_api.config.exception.EmailInUseException;
import io.alviss.recipe_api.config.exception.InvalidPasswordException;
import io.alviss.recipe_api.config.jwt.TokenManager;
import io.alviss.recipe_api.user.User;
import io.alviss.recipe_api.user.UserDTO;
import io.alviss.recipe_api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountLockedException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthResource {

    @Value("${base-url") private String baseUrl;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenManager tokenManager;
    private final VerificationTokenService verificationTokenService;
    private final VerificationEmailServiceImpl verificationEmailService;
    private final LoginAttemptsService loginAttemptsService;

//    public AuthController(final UserService userService, final PasswordEncoder passwordEncoder, final TokenManager tokenManager) {
//        this.userService = userService;
//        this.passwordEncoder = passwordEncoder;
//        this.tokenManager = tokenManager;
//    }

    @PostMapping(value = "/signin")
    public ResponseEntity<?> authenticateUser (@Valid @RequestBody LoginPayload loginPayload) throws InvalidPasswordException, AccountLockedException {
        AuthenticationResult authenticationResult = userService.authenticateUser(loginPayload);

        if (authenticationResult.isSuccess()) {
            return ResponseEntity.ok(authenticationResult.getJwtResponse());
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse(authenticationResult.getMessage()));
        }
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> registerUser (@Valid @RequestBody RegisterPayload registerRequest, final HttpServletRequest request) {
        if (userService.emailExists(registerRequest.getEmail())) {
            throw new EmailInUseException();
        }

        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        
        User createdUser = userService.create(registerRequest);
    
        final String recipientAddress = createdUser.getEmail();
        final String recipientName = createdUser.getName();
        final String subject = "Registration Confirmation";
        verificationEmailService.sendMessage(recipientAddress, subject, verificationEmailService.buildEmail(recipientName, getVerificationUrl(request, createdUser.getVerificationToken().getToken())));

        return ResponseEntity.created(URI.create(getAppURL(request))).body(new MessageResponse("User successfully registered"));
    }

    @GetMapping("/confirm-registration")
    public ResponseEntity<?> confirmRegistration (@RequestParam("token") String confirmationToken, HttpServletRequest request) {
        VerificationToken token = verificationTokenService.find(confirmationToken);

        if (token != null) {
            if (token.getUser().isEnabled()) {
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(new MessageResponse("User already verified!"));
            }
            verificationTokenService.update(token);
            return ResponseEntity.ok(new MessageResponse("Account successfully verified"));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Token provided does not exist"));

    }

    @GetMapping("/resend-token")
    public ResponseEntity<?> resendToken (@RequestParam("email") String email, HttpServletRequest request) {
        final String subject = "Registration Confirmation";
        User user = userService.findThruEmail(email);
        VerificationToken token = verificationTokenService.findByUser(
                user
        );

        if (token != null) {
            verificationEmailService.sendMessage(user.getEmail(), subject, verificationEmailService.buildEmail(user.getName(), getVerificationUrl(request, token.getToken())));
            return ResponseEntity.ok(new MessageResponse("Verification mail successfully sent!"));
        }

        return ResponseEntity.badRequest().body("User with email provided does not exist.");

    }

    private String getAppURL (final HttpServletRequest req) {
        return "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
    }

    private String getVerificationUrl (final HttpServletRequest req, UUID token) {
        return "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/api/v1/auth/confirm-registration?token=" + token.toString();
    }
}
