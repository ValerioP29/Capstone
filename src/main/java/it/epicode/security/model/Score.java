package it.epicode.security.model;

import jakarta.persistence.*;
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
    private User client; // Cliente a cui appartiene il punteggio

    private int totalScore; // Punteggio totale calcolato dai feedback
    private String tier; // Livello (es. BRONZE, SILVER, GOLD, PLATINUM)

    private LocalDateTime updatedAt = LocalDateTime.now();
}
