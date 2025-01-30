package it.epicode.security.service;

import it.epicode.security.dto.ScoreDTO;
import it.epicode.security.exceptions.ResourceNotFoundException;
import it.epicode.security.model.Feedback;
import it.epicode.security.model.Score;
import it.epicode.security.model.User;
import it.epicode.security.repository.FeedbackRepository;
import it.epicode.security.repository.ScoreRepository;
import it.epicode.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    FeedbackRepository feedbackRepository;

    public Score findByClientId(Long clientId) {
        return scoreRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Score not found for client with ID " + clientId));
    }

    public List<Score> findAllScores() {
        return scoreRepository.findAll();
    }

    public Score createScore(ScoreDTO scoreDTO) {
        User client = userRepository.findById(scoreDTO.getClientId())
                .orElseThrow(()-> new ResourceNotFoundException("User not found with ID: " + scoreDTO.getClientId() ));


        Score score = new Score();
        score.setClient(client);
        score.setTotalScore(scoreDTO.getTotalScore());
        score.setTier(scoreDTO.getTier());

        return scoreRepository.save(score);
    }

    public Score updateClientScore(Long clientId, ScoreDTO scoreDTO) {
        Score score = scoreRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Score not found for client with ID " + clientId));

        score.setTotalScore(score.getTotalScore());
        score.setTier(score.getTier());

        return scoreRepository.save(score);
    }

    public void deleteScore(Long clientId) {
        Score score = scoreRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Score not found for client with ID " + clientId));
        scoreRepository.delete(score);

    }
    public Score updateScoreForClient(Long clientId) {
        List<Feedback> feedbacks = feedbackRepository.findByClientId(clientId);

        // ✅ Se il cliente non ha feedback, assegniamo un punteggio di default (0)
        if (feedbacks.isEmpty()) {
            Score score = scoreRepository.findByClientId(clientId)
                    .orElse(new Score());

            score.setTotalScore(0);
            score.setTier("BRONZE");

            return scoreRepository.save(score);
        }

        //   calcoliamo il punteggio
        int totalScore = feedbacks.stream()
                .mapToInt(f -> f.getCleanlinessScore() + f.getRuleComplianceScore() + f.getBehaviorScore())
                .sum();

        String tier = calculateTier(totalScore);

        Score score = scoreRepository.findByClientId(clientId)
                .orElse(new Score());

        score.setTotalScore(totalScore);
        score.setTier(tier);

        return scoreRepository.save(score);
    }


    private String calculateTier(int totalScore) {
        if (totalScore >= 50) return "GOLD";
        if (totalScore >= 30) return "SILVER";
        return "BRONZE";
    }


}
