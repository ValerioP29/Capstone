package it.epicode.security.auth;

import it.epicode.security.dto.RegisterRequestDTO;
import it.epicode.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    // 🔹 REGISTRAZIONE
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        try {
            userService.registerUser(registerRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","Registrazione avvenuta con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Errore durante la registrazione: " + e.getMessage()));
        }
    }

    // 🔹 LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("📩 Richiesta di login ricevuta per username: " + loginRequest.getUsername());
            System.out.println("🔑 Password inserita: " + loginRequest.getPassword());

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

            // ✅ DEBUG: Stampa il token nel terminale di IntelliJ
            System.out.println("🔑 Token generato per " + userDetails.getUsername() + ": " + token);

            return ResponseEntity.ok(new AuthResponse(token));

        } catch (Exception e) {
            System.out.println("❌ ERRORE LOGIN: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Invalid credentials"));
        }
    }
}
