package it.epicode.security.repository;

import it.epicode.security.model.Hotel;
import it.epicode.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN u.score s ORDER BY s.totalScore DESC")
    List<User> findTopUsers();




    // ✅ Aggiunti metodi mancanti per verificare l'esistenza di username ed email
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
