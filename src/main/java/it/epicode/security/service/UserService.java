package it.epicode.security.service;

import it.epicode.security.auth.Role;
import it.epicode.security.dto.RegisterRequestDTO;
import it.epicode.security.dto.UserDTO;
import it.epicode.security.model.User;
import it.epicode.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ REGISTRAZIONE UTENTE
    public void registerUser(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByUsername(registerRequestDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));

        // ✅ Conversione String -> Enum Role
        Set<Role> userRoles = registerRequestDTO.getRoles().stream()
                .map(roleName -> {
                    try {
                        return Role.valueOf(roleName); // Converte la stringa in enum
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Invalid role: " + roleName);
                    }
                })
                .collect(Collectors.toSet());

        user.setRoles(userRoles);
        userRepository.save(user);
    }

    // ✅ TROVA UTENTE PER ID
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ TROVA TUTTI GLI UTENTI
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // ✅ TROVA GLI UTENTI CON IL PUNTEGGIO PIÙ ALTO
    public List<User> findTopUsers() {
        return userRepository.findTopUsers();
    }

    // ✅ AGGIORNA UTENTE (solo se è lo stesso utente autenticato)
    public User updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());

        userRepository.save(existingUser);
        return existingUser;
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
