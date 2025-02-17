package it.epicode.security.service;

import it.epicode.security.exceptions.ResourceNotFoundException;
import it.epicode.security.model.Reward;
import it.epicode.security.model.RewardClaim;
import it.epicode.security.model.Score;
import it.epicode.security.model.User;
import it.epicode.security.repository.RewardClaimRepository;
import it.epicode.security.repository.RewardRepository;
import it.epicode.security.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RewardClaimService {

    @Autowired
    private RewardClaimRepository rewardClaimRepository;

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    public List<RewardClaim> getClaimsByClient(Long clientId) {
        return rewardClaimRepository.findByClientId(clientId);
    }

    public RewardClaim claimReward(Long clientId, Long rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found"));

        Score score = scoreRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Score not found"));

        if (score.getTotalScore() < reward.getPointsRequired()) {
            throw new IllegalArgumentException("Not enough points to claim this reward");
        }

        // Sottraiamo i punti dal punteggio totale
        score.setTotalScore(score.getTotalScore() - reward.getPointsRequired());
        scoreRepository.save(score);

        RewardClaim rewardClaim = new RewardClaim();
        rewardClaim.setClient(score.getClient());
        rewardClaim.setReward(reward);
        rewardClaim.setStatus("PENDING");

        return rewardClaimRepository.save(rewardClaim);
    }
}
