package it.epicode.security.service;

import it.epicode.security.auth.Role;
import it.epicode.security.dto.RegisterRequestDTO;
import it.epicode.security.dto.UserDTO;
import it.epicode.security.exceptions.ResourceNotFoundException;
import it.epicode.security.model.Score;
import it.epicode.security.model.User;
import it.epicode.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequestDTO registerDTO) {
        Optional<User> existingUser = userRepository.findByUsername(registerDTO.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        // Mappare i ruoli
        Set<Role> roles = registerDTO.getRoles().stream()
                .map(role -> Role.valueOf(role.toUpperCase()))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        // Creazione punteggio iniziale per il nuovo utente
        Score score = new Score();
        score.setTotalScore(0);
        score.setTier("BRONZE");
        score.setClient(user);
        user.setScore(score);

        return userRepository.save(user);
    }


    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findTopUsers() {
        return userRepository.findAll().stream()
                .sorted((u1, u2) -> Integer.compare(u2.getScore().getTotalScore(), u1.getScore().getTotalScore()))
                .limit(10)
                .collect(Collectors.toList());
    }

    public User updateUser(Long id, UserDTO userDTO) {
        User user = findById(id);

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        return userRepository.save(user);
    }
}
