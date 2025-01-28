package it.epicode.security.service;

import it.epicode.security.exceptions.ResourceNotFoundException;
import it.epicode.security.model.Feedback;
import it.epicode.security.model.Score;
import it.epicode.security.repository.FeedbackRepository;
import it.epicode.security.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreService {

    @Autowired
    ScoreRepository scoreRepository;

    @Autowired
    FeedbackRepository feedbackRepository;

    //calcola e agiorna punteggio



    public Score updateClientScore(Long clientId) {
        List<Feedback> feedbacks = feedbackRepository.findByClientId(clientId);

        if (feedbacks.isEmpty()) {
            throw new ResourceNotFoundException("No feedback found for client with ID " + clientId);
        }
        int totalScore = feedbacks.stream().mapToInt(fb -> fb.getCleanlinessScore() + fb.getRuleComplianceScore() +
                fb.getBehaviorScore()).sum();

        String tier = calculateTier(totalScore);

        Score score = scoreRepository.findByClientId(clientId).orElse(new Score());
        score.setTotalScore(totalScore);
        score.setTier(tier);

        return scoreRepository.save(score);

    }
    private String calculateTier(int totalScore) {
        if (totalScore > 100) return "PLATINUM";
        if (totalScore > 70) return "GOLD";
        if (totalScore > 40) return "SILVER";
        return "BRONZE";
    }
}
