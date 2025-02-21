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
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    FeedbackService feedbackService;

    @Autowired
    ScoreService scoreService;

    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @PostMapping
    public ResponseEntity<Feedback> createFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        System.out.println("📝 Feedback ricevuto: " + feedbackDTO.toString()); // ✅ Debug

        if (feedbackDTO.getHotelId() == null || feedbackDTO.getHotelId() == 0) {
            System.out.println("❌ ERRORE: HotelId mancante!");
            return ResponseEntity.badRequest().body(null);
        }

        if (feedbackDTO.getClientId() == null || feedbackDTO.getClientId() == 0) {
            System.out.println("❌ ERRORE: ClientId mancante!");
            return ResponseEntity.badRequest().body(null);
        }

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
    @GetMapping("/hotel/{ownerId}")
    public ResponseEntity<List<Feedback>> getFeedbackByHotel(@PathVariable Long ownerId) {
        System.out.println("📌 DEBUG: GET feedback per ownerId -> " + ownerId);
        List<Feedback> feedbacks = feedbackService.getFeedbackByHotelOwner(ownerId);
        System.out.println("✅ Feedback trovati: " + feedbacks.size());

        return ResponseEntity.ok(feedbacks);
    }
}
