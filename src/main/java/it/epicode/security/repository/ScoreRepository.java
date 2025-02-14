package it.epicode.security.repository;

import it.epicode.security.dto.ScoreDTO;
import it.epicode.security.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    @Query("SELECT new it.epicode.security.dto.ScoreDTO(c.id, c.username, s.totalScore, s.tier) " +
            "FROM Score s JOIN s.client c " +
            "WHERE s.client.id IN (SELECT f.client.id FROM Feedback f WHERE f.hotel.id = :hotelId) " +
            "ORDER BY s.totalScore DESC")
    List<ScoreDTO> findScoresByHotelId(@Param("hotelId") Long hotelId);






    Optional<Score> findByClientId(Long clientId);
}
