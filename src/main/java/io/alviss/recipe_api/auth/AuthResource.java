package io.alviss.recipe_api.auth;

import io.alviss.recipe_api.auth.login_attempts.LoginAttemptsService;
import io.alviss.recipe_api.auth.mail.VerificationEmailServiceImpl;
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
import org.springframework.web.context.request.WebRequest;

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
        final UserDTO user = userService.loadUserByUsername(loginPayload.getEmail());

        final User userEntity = userService.mapToEntity(user, new User());

        if (!loginAttemptsService.incrementAttemptsAndCheckAcctLocked(userEntity)) {
            throw new AccountLockedException("Account locked due to multiple failed login attempts.");
        }

        final boolean passwordMatches = passwordEncoder.matches(loginPayload.getPassword(), user.getPassword());

        if (!passwordMatches) throw new InvalidPasswordException();

        // userService.create(user)
        final VerificationToken _tkn = verificationTokenService.findByUser(userEntity);

        if (user.isEnabled() == false){
            if (_tkn.getExpiryDate().after(new Date())){
                userService.delete(user.getId());
                return ResponseEntity.badRequest().body(new MessageResponse("Did not verify email so token expired. Please recreate account."));
            }  
            return ResponseEntity.badRequest().body(new MessageResponse("Email not verified yet. Please verify e-mail address to continue"));
        }

        loginAttemptsService.resetAttempts(userEntity);

        String token = tokenManager.generateJwtToken(user);

        final JwtResponse response = new JwtResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setID(user.getId().toString());

        return ResponseEntity.ok(response);

    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> registerUser (@Valid @RequestBody RegisterPayload registerRequest, final HttpServletRequest request) {
        if (userService.emailExists(registerRequest.getEmail())) {
            throw new EmailInUseException();
        }

        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        
        User createdUser = userService.create(registerRequest);
        String token = UUID.randomUUID().toString();

        verificationTokenService.create(createdUser, token);

        final String recipientAddress = createdUser.getEmail();
        final String recipientName = createdUser.getName();
        final String subject = "Registration Confirmation";
        verificationEmailService.sendMessage(recipientAddress, subject, verificationEmailService.buildEmail(recipientName, getVerificationUrl(request, token)));

        return ResponseEntity.created(URI.create(getAppURL(request))).body(new MessageResponse("User successfully registered"));
    }

    @GetMapping("/confirm-registration")
    public ResponseEntity<?> confirmRegistration (@RequestParam("token") String confirmationToken, HttpServletRequest request) {
        VerificationToken token = verificationTokenService.find(confirmationToken);

        if (token != null) {
            System.out.println(token.toString());
            final User user = token.getUser();
            if (token.getUser().isEnabled()) {
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(new MessageResponse("User already verified!"));
            }
            user.setEnabled(true);
            userService.saveUpdated(user);
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

    private String getVerificationUrl (final HttpServletRequest req, String token) {
        return "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/api/v1/auth/confirm-registration?token=" + token;
    }
}
