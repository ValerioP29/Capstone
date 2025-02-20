package it.epicode.security.controller;

import it.epicode.security.dto.UserDTO;
import it.epicode.security.dto.PasswordUpdateRequest;
import it.epicode.security.model.User;
import it.epicode.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ Ottiene un utente per ID e include `totalScore`
    @PreAuthorize("hasRole('ROLE_HOTEL') or hasRole('ROLE_CLIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        UserDTO userDTO = new UserDTO(user);
        userDTO.setTotalScore(user.getScore() != null ? user.getScore().getTotalScore() : 0); // Se score è null, mostra 0
        return ResponseEntity.ok(userDTO);
    }
    // ✅ Permette all'utente di modificare il profilo
    @PreAuthorize("#id == authentication.principal.id")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            // Cerca l'utente nel database
            User updatedUser = userService.updateUser(id, userDTO);

            // Restituisce l'utente aggiornato come DTO
            UserDTO updatedUserDTO = new UserDTO(updatedUser);
            updatedUserDTO.setTotalScore(updatedUser.getScore() != null ? updatedUser.getScore().getTotalScore() : 0);
            return ResponseEntity.ok(updatedUserDTO);
        } catch (Exception e) {
            // Gestisci errori generali
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // ✅ Permette all'utente di modificare la password
    @PreAuthorize("#id == authentication.principal.id")
    @PutMapping("/{id}/password")
    public ResponseEntity<String> updatePassword(@PathVariable Long id, @RequestBody PasswordUpdateRequest request) {
        boolean success = userService.updateUserPassword(id, request);
        if (success) {
            return ResponseEntity.ok("Password aggiornata con successo!");
        } else {
            return ResponseEntity.badRequest().body("Errore: la password attuale non è corretta!");
        }
    }
}
