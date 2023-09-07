package io.alviss.recipe_api.user;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import io.alviss.recipe_api.auth.payload.RegisterPayload;
import io.alviss.recipe_api.config.exception.InvalidPasswordException;
import io.alviss.recipe_api.model.Gender;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

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

    public User findThruEmail (final String email) { return userRepository.findByEmailIgnoreCase(email); }

    @Override
    @Transactional
    public UserDTO loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with email: " + username + " not found"));

        return mapToDTO(user, new UserDTO());
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
}
