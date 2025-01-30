package it.epicode.security.controller;

import it.epicode.security.dto.ScoreDTO;
import it.epicode.security.model.Score;
import it.epicode.security.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/{clientId}")
    @PreAuthorize("#clientId == authentication.principal.id")
    public ResponseEntity<Score> getScoreByClientId(@PathVariable Long clientId) {
        Score score = scoreService.findByClientId(clientId);
        return ResponseEntity.ok(score);
    }

    @GetMapping
    public ResponseEntity<List<Score>> getAllScores() {
        List<Score> scores = scoreService.findAllScores();
        return ResponseEntity.ok(scores);
    }

    @PutMapping("/{clientId}")
    @PreAuthorize("hasRole('ROLE_HOTEL')") // Solo gli hotel possono aggiornare il punteggio
    public ResponseEntity<Score> updateClientScore(@PathVariable Long clientId) {
        Score updatedScore = scoreService.updateScoreForClient(clientId);
        return ResponseEntity.ok(updatedScore);

    }
    @DeleteMapping("/{clientId}")
    @PreAuthorize("hasRole('ROLE_HOTEL') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteScore(@PathVariable Long clientId) {
        scoreService.deleteScore(clientId);
        return ResponseEntity.noContent().build();
    }

}
