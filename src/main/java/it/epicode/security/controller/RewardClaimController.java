package it.epicode.security.controller;

import it.epicode.security.model.RewardClaim;
import it.epicode.security.service.RewardClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reward-claims")
public class RewardClaimController {

    @Autowired
    private RewardClaimService rewardClaimService;

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @GetMapping("/{clientId}")
    public ResponseEntity<List<RewardClaim>> getClaimsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(rewardClaimService.getClaimsByClient(clientId));
    }

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @PostMapping("/{clientId}/{rewardId}")
    public ResponseEntity<RewardClaim> claimReward(@PathVariable Long clientId, @PathVariable Long rewardId) {
        return ResponseEntity.ok(rewardClaimService.claimReward(clientId, rewardId));
    }
}

