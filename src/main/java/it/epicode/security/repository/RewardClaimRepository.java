package it.epicode.security.repository;

import it.epicode.security.model.RewardClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardClaimRepository extends JpaRepository<RewardClaim, Long> {
    List<RewardClaim> findByClientId(Long clientId);
}
