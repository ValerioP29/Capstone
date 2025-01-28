package it.epicode.security.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false, unique = true, name = "client_id", referencedColumnName = "id")
    private User client; // Cliente a cui appartiene il punteggio

    @Column(nullable = false)
    private int totalScore;// Punteggio totale calcolato dai feedback

    @Column(nullable = false)
    @NotBlank(message = "Tier is required")
    private String tier; // Livello (es. BRONZE, SILVER, GOLD, PLATINUM)

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
