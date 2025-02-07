package it.epicode.security.controller;

import it.epicode.security.dto.FeedbackDTO;
import it.epicode.security.model.Feedback;
import it.epicode.security.service.FeedbackService;
import it.epicode.security.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    FeedbackService feedbackService;

    @Autowired
    ScoreService scoreService;

    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @PostMapping
    public ResponseEntity<Feedback> createFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        Feedback createdFeedback = feedbackService.createFeedback(feedbackDTO);

        // ✅ Aggiorniamo il punteggio dell'utente automaticamente dopo la creazione del feedback
        scoreService.updateScoreForClient(feedbackDTO.getClientId());

        return ResponseEntity.ok(createdFeedback);
    }

    @PreAuthorize("hasRole('ROLE_CLIENT') and #clientId == authentication.principal.id")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Feedback>> getFeedbackByClient(@PathVariable Long clientId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackByClient(clientId);
        return ResponseEntity.ok(feedbacks);
    }

    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Feedback>> getFeedbackByHotel(@PathVariable Long ownerId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackByHotelOwner(ownerId);
        return ResponseEntity.ok(feedbacks);
    }
}
