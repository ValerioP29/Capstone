package it.epicode.security.controller;

import it.epicode.security.auth.Role;
import it.epicode.security.dto.UserDTO;
import it.epicode.security.model.Feedback;
import it.epicode.security.model.Hotel;
import it.epicode.security.model.Score;
import it.epicode.security.model.User;
import it.epicode.security.service.FeedbackService;
import it.epicode.security.service.HotelService;
import it.epicode.security.service.ScoreService;
import it.epicode.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private ScoreService scoreService;

    // ✅ API per ottenere le informazioni dell'utente autenticato
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserInfo(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(new UserDTO(user));
    }

    // ✅ API per ottenere la lista degli hotel di proprietà di un HOTEL
    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @GetMapping("/my-hotels")
    public ResponseEntity<List<Hotel>> getMyHotels(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        if (!user.getRoles().contains(Role.ROLE_HOTEL)) {
            return ResponseEntity.status(403).build(); // Accesso negato se non è un HOTEL
        }

        return ResponseEntity.ok(hotelService.findHotelsByOwner(user.getId()));
    }

    // ✅ API per ottenere i feedback lasciati da un HOTEL
    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @GetMapping("/my-feedbacks")
    public ResponseEntity<List<Feedback>> getMyFeedbacks(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        if (!user.getRoles().contains(Role.ROLE_HOTEL)) {
            return ResponseEntity.status(403).build(); // Accesso negato se non è un HOTEL
        }

        return ResponseEntity.ok(feedbackService.getFeedbackByHotelOwner(user.getId()));
    }

    // ✅ API per ottenere i feedback ricevuti da un CLIENT
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @GetMapping("/received-feedbacks")
    public ResponseEntity<List<Feedback>> getReceivedFeedbacks(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        if (!user.getRoles().contains(Role.ROLE_CLIENT)) {
            return ResponseEntity.status(403).build(); // Accesso negato se non è un CLIENT
        }

        return ResponseEntity.ok(feedbackService.getFeedbackByClient(user.getId()));
    }

    // ✅ API per ottenere il punteggio di un CLIENT
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @GetMapping("/my-score")
    public ResponseEntity<Score> getMyScore(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        if (!user.getRoles().contains(Role.ROLE_CLIENT)) {
            return ResponseEntity.status(403).build(); // Accesso negato se non è un CLIENT
        }

        return ResponseEntity.ok(scoreService.findByClientId(user.getId()));
    }

    // ✅ API per ottenere la classifica generale dei CLIENT
    @GetMapping("/top-clients")
    public ResponseEntity<List<UserDTO>> getTopClients() {
        List<UserDTO> topClients = userService.findTopUsers()
                .stream()
                .map(UserDTO::new)
                .toList();
        return ResponseEntity.ok(topClients);
    }
}
