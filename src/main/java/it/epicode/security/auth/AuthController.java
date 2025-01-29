package it.epicode.security.auth;
import it.epicode.security.auth.Role;
import it.epicode.security.dto.RegisterRequestDTO;
import it.epicode.security.auth.AuthResponse;
import it.epicode.security.auth.LoginRequest;
import it.epicode.security.model.User;
import it.epicode.security.service.UserService;
import it.epicode.security.auth.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    // 🔹 REGISTRAZIONE
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO registerRequestDTO) throws BadRequestException {

        // Mappiamo i ruoli
        Set<Role> roles = registerRequestDTO.getRoles().stream()
                .map(role -> Role.valueOf(role.toUpperCase()))
                .collect(Collectors.toSet());

        // Creiamo l'utente
        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword(registerRequestDTO.getPassword());  // Verrà criptata nel service
        user.setEmail(registerRequestDTO.getEmail());
        user.setRoles(roles); // Se ci sono più ruoli, prendi il primo

        userService.registerUser(user);

        return ResponseEntity.ok("Registrazione avvenuta con successo");
    }

    // 🔹 LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Ottenere UserDetails
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generare Token JWT
        String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token));
    }
}
