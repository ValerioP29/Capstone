package it.epicode.security.service;

import it.epicode.security.auth.Role;
import it.epicode.security.dto.PasswordUpdateRequest;
import it.epicode.security.dto.RegisterRequestDTO;
import it.epicode.security.dto.UserDTO;
import it.epicode.security.model.Score;
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

    public User findById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    if (user.getScore() == null) {
                        user.setScore(new Score(0, "BRONZE")); // Default se non ha punteggio
                    }
                    return user;
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ AGGIORNA UTENTE (ora permette di aggiornare username e password)
    public User updateUser(Long id, UserDTO userDTO) {
        User existingUser = findById(id);

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail()); // Se decidi di mantenere anche l'email modificabile

        userRepository.save(existingUser);
        return existingUser;
    }

    // ✅ MODIFICA PASSWORD UTENTE (richiede la password attuale per sicurezza)
    public boolean updateUserPassword(Long id, PasswordUpdateRequest request) {
        User existingUser = findById(id);

        // Controllo se la password attuale è corretta
        if (!passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())) {
            return false; // Password errata
        }

        // Aggiorna la password
        existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(existingUser);
        return true;
    }


    // ✅ TROVA TUTTI GLI UTENTI
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // ✅ TROVA GLI UTENTI CON IL PUNTEGGIO PIÙ ALTO
    public List<User> findTopUsers() {
        return userRepository.findTopUsers();
    }



    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

