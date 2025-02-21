package it.epicode.security.service;

import it.epicode.security.model.Reward;
import it.epicode.security.repository.RewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RewardService {

    @Autowired
    private RewardRepository rewardRepository;

    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    public Optional<Reward> getRewardById(Long id) {
        return rewardRepository.findById(id);
    }

    public Reward createReward(Reward reward) {
        return rewardRepository.save(reward);
    }

    public Reward updateReward(Long id, Reward updatedReward) {
        return rewardRepository.findById(id)
                .map(existingReward -> {
                    existingReward.setName(updatedReward.getName());
                    existingReward.setDescription(updatedReward.getDescription());
                    existingReward.setPointsRequired(updatedReward.getPointsRequired());
                    return rewardRepository.save(existingReward);
                })
                .orElseThrow(() -> new RuntimeException("Reward not found"));
    }

    public void deleteReward(Long id) {
        rewardRepository.deleteById(id);
    }
}
