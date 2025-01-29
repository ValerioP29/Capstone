package it.epicode.security.service;

import it.epicode.security.exceptions.ResourceNotFoundException;
import it.epicode.security.model.User;
import it.epicode.security.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.hibernate.mapping.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registrazione
    public User registerUser(User user) throws BadRequestException {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new BadRequestException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
       return userRepository.save(user);
}
// Recupero utente
public User getUserById(Long userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
}
}