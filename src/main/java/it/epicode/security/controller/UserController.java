package it.epicode.security.controller;

import it.epicode.security.dto.UserDTO;
import it.epicode.security.model.User;
import it.epicode.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ Ottiene un utente per ID (visibile solo a se stesso o agli hotel)
    @PreAuthorize("hasRole('ROLE_HOTEL') or hasRole('ROLE_CLIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(new UserDTO(user));
    }

    // ✅ Recupera tutti gli utenti (solo per gli hotel)
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_HOTEL')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAll()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // ✅ Recupera gli utenti con punteggio più alto (solo per gli hotel)
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_HOTEL')")
    @GetMapping("/top")
    public ResponseEntity<List<UserDTO>> getTopUsers() {
        List<UserDTO> topUsers = userService.findTopUsers()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(topUsers);
    }

    // ✅ Aggiorna il proprio profilo (solo l'utente stesso può farlo)
    @PreAuthorize("#id == authentication.principal.id")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }
}
