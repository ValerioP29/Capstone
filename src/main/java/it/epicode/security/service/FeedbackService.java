package it.epicode.security.service;

import it.epicode.security.exceptions.ResourceNotFoundException;
import it.epicode.security.model.Feedback;
import it.epicode.security.model.User;
import it.epicode.security.repository.FeedbackRepository;
import it.epicode.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    // Creazione Feedback
    public Feedback createFeedback(Long hotelId, Long clientId, Feedback feedback) {
        User client = userRepository.findById(clientId).orElseThrow(() -> new ResourceNotFoundException("Client with ID " + clientId + "not found"));

        feedback.setClient(client);
        return feedbackRepository.save(feedback);
    }

    // Recupero dei feedback
     public List<Feedback> getFeedbackByClient (Long clientId) {
        if (!userRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client with ID " + clientId + "not found");
        }
        return feedbackRepository.findByClientId(clientId);
     }

}
