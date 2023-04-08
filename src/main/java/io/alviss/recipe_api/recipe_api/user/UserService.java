package io.alviss.recipe_api.recipe_api.user;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import io.alviss.recipe_api.recipe_api.model.Gender;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

    public User create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user);
    }

    public void update(final UUID id, final UserDTO userDTO) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final UUID id) {
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setName(user.getName());
        userDTO.setGender(user.getGender().name());
        userDTO.setCountry(user.getCountry());
        userDTO.setVerificationToken(user.getVerificationToken());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setAccountNonLocked(user.isAccountNonLocked());
        return userDTO;
    }

    public User mapToEntity(final UserDTO userDTO, final User user) {
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setName(userDTO.getName());
        user.setGender(Gender.valueOf(userDTO.getGender()));
        user.setCountry(userDTO.getCountry());
        user.setVerificationToken(userDTO.getVerificationToken());
        user.setEnabled(userDTO.isEnabled());
        user.setAccountNonLocked(userDTO.isAccountNonLocked());
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
}
