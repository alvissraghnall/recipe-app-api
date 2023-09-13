package io.alviss.recipe_api.user;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.security.auth.login.AccountLockedException;

import io.alviss.recipe_api.auth.login_attempts.LoginAttemptsRepository;
import io.alviss.recipe_api.auth.login_attempts.LoginAttemptsService;
import io.alviss.recipe_api.auth.payload.AuthenticationResult;
import io.alviss.recipe_api.auth.payload.JwtResponse;
import io.alviss.recipe_api.auth.payload.LoginPayload;
import io.alviss.recipe_api.auth.payload.RegisterPayload;
import io.alviss.recipe_api.auth.verification.VerificationToken;
import io.alviss.recipe_api.config.exception.InvalidPasswordException;
import io.alviss.recipe_api.config.jwt.TokenManager;
import io.alviss.recipe_api.model.Gender;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsManager, UserDetailsPasswordService {

    private final UserRepository userRepository;
    private final LoginAttemptsRepository loginAttemptsRepository;
    private final LoginAttemptsService loginAttemptsService;
    private final TokenManager tokenManager;
    
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> findAll() {
        return userRepository.findAll(Sort.by("id"))
                .stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .collect(Collectors.toList());
    }

    public UserDTO get(final UUID id) {
        return userRepository.findById(id)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public User create(final RegisterPayload payload) {
        final User user = new User();
        VerificationToken token = new VerificationToken(user);
        user.setVerificationToken(token);
        mapPayloadToUser(payload, user);
        return userRepository.save(user);
    }

    public void update(final UUID id, final UserDTO payload) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(payload, user);
        userRepository.save(user);
    }

    public void delete(final UUID id) {
        userRepository.deleteById(id);
    }

    public void lockUserAcct (User user) {
        user.setAccountNonLocked(false);
        userRepository.save(user);
    }

    private UserDTO mapToDTO(final User user, final UserDTO payload) {
        payload.setId(user.getId());
        payload.setEmail(user.getEmail());
        payload.setPassword(user.getPassword());
        payload.setName(user.getName());
        payload.setGender(user.getGender());
        payload.setCountry(user.getCountry());
        payload.setVerificationToken(user.getVerificationToken());
        payload.setEnabled(user.isEnabled());
        payload.setAccountNonLocked(user.isAccountNonLocked());
        payload.setRecipes(user.getRecipes());
        payload.setLoginAttempts(user.getLoginAttempts());
        payload.setFavourites(user.getFavourites());
        
        return payload;
    }

    public User mapToEntity(final UserDTO payload, final User user) {
        user.setId(payload.getId());
        user.setEmail(payload.getEmail());
        user.setPassword(payload.getPassword());
        user.setName(payload.getName());
        user.setGender(payload.getGender());
        user.setCountry(payload.getCountry());
        user.setVerificationToken(payload.getVerificationToken());
        user.setEnabled(payload.isEnabled());
        user.setAccountNonLocked(payload.isAccountNonLocked());
        user.setRecipes(payload.getRecipes());
        user.setLoginAttempts(payload.getLoginAttempts());
        user.setFavourites(payload.getFavourites());
        return user;
    }

    private User mapPayloadToUser (final RegisterPayload payload, final User user) {
        user.setEmail(payload.getEmail());
        user.setPassword(payload.getPassword());
        user.setName(payload.getName());
        user.setGender(Gender.valueOf(payload.getGender()));
        user.setCountry(payload.getCountry());
        return user;
    }

    public boolean emailExists(final String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    } 

    public User findThruEmail (final String email) { 
        return userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found")); 
    }

    @Override
    @Transactional
    public UserDTO loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(findThruEmail(username));
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with email: " + username + " not found"));
        // System.out.println(user);
        
        return mapToDTO(user, new UserDTO());
    }

    public User loadUserAndCheckAcctLocked (String email) throws AccountLockedException {
        User user = findThruEmail(email);
        boolean isAccountNonLocked = loginAttemptsService.incrementAttemptsAndCheckAcctLocked(user);

        saveUpdated(user);

        if (!isAccountNonLocked) {
            throw new AccountLockedException("Account locked due to multiple failed login attempts.");
        }

        return user;
    }

    public User saveUpdated(User user) {
        return userRepository.save(user);
    }

    public void updatePassword (UpdateUserPasswordDTO userDetails, String newPassword, UserDTO user) {
        boolean isValidPwd = passwordEncoder.matches(userDetails.getNewPassword(), user.getPassword());

        if(!isValidPwd) throw new InvalidPasswordException("Incorrect old password provided!");
        
        user.setPassword(userDetails.getNewPassword());
        User updatedUser = mapToEntity(user, new User());

        userRepository.save(updatedUser);
    }

    @Override
    public UserDTO updatePassword(UserDetails user, String newPassword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePassword'");
    }

    @Override
    public void createUser(UserDetails userDTO) {
        final User user = new User();
        mapToEntity((UserDTO) userDTO, user);
        userRepository.save(user);
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public void deleteUser(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    @Override
    public boolean userExists(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'userExists'");
    }

    public AuthenticationResult authenticateUser(LoginPayload loginPayload) {
        try {
            User user = loadUserAndCheckAcctLocked(loginPayload.getEmail());

            // Check if the password matches
            if (!passwordEncoder.matches(loginPayload.getPassword(), user.getPassword())) {
                return new AuthenticationResult(false, "Invalid password", null);
            }

            // Handle the case where the email is not verified
            if (!user.isEnabled()) {
                VerificationToken verificationToken = user.getVerificationToken();
                if (verificationToken.getExpiryDate().after(new Date())) {
                    delete(user.getId());
                    return new AuthenticationResult(false, "Email verification token expired. Please recreate your account.", null);
                }
                return new AuthenticationResult(false, "Email not verified yet. Please verify your email address to continue.", null);
            }

            // Reset login attempts
            loginAttemptsService.resetAttempts(user);

            // Generate a JWT token
            String token = tokenManager.generateJwtToken((UserDTO) user);

            JwtResponse response = new JwtResponse();
            response.setToken(token);
            response.setEmail(user.getEmail());
            response.setID(user.getId().toString());

            return new AuthenticationResult(true, "Authentication successful", response);
        } catch (InvalidPasswordException e) {
            return new AuthenticationResult(false, "Invalid password", null);
        } catch (AccountLockedException e) {
            return new AuthenticationResult(false, "Account locked due to multiple failed login attempts.", null);
        } catch (UsernameNotFoundException e) {
            return new AuthenticationResult(false, "User not found with the provided email.", null);
        }
    }
}
