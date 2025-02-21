package it.epicode.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.epicode.security.dto.ScoreDTO;
import it.epicode.security.model.Score;
import it.epicode.security.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;



    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @GetMapping("/{clientId}")
    public ResponseEntity<?> getScoreByClientId(@PathVariable Long clientId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("📌 Richiesta Score per clientId: " + clientId + " da user: " + username);

        Score score = scoreService.findByClientId(clientId);

        if (score == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Score non trovato"));

        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonScore = objectMapper.writeValueAsString(score);
            System.out.println("✅ Score API JSON: " + jsonScore); // 🔎 Debug in console
            return ResponseEntity.ok().body(score);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Errore nella serializzazione JSON\"}");
        }
    }


    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @GetMapping
    public ResponseEntity<List<Score>> getAllScores() {
        List<Score> scores = scoreService.findAllScores();
        return ResponseEntity.ok(scores);
    }

    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @PutMapping("/{clientId}")
    public ResponseEntity<Score> updateClientScore(@PathVariable Long clientId) {
        Score updatedScore = scoreService.updateScoreForClient(clientId);
        return ResponseEntity.ok(updatedScore);
    }

    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteScore(@PathVariable Long clientId) {
        scoreService.deleteScore(clientId);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ScoreDTO>> getScoresByHotel(
            @PathVariable Long hotelId) {
        List<ScoreDTO> scores = scoreService.getScoresByHotel(hotelId);
        return ResponseEntity.ok(scores);
    }

}
