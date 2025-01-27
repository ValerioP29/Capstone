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
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Hotel hotel; // Hotel che lascia il feedback

    @ManyToOne
    private User client; // Cliente che riceve il feedback

    private int cleanlinessScore;
    private int ruleComplianceScore;
    private int behaviorScore;
    private boolean respectedCheckInOut;
    private String comments;

    private LocalDateTime createdAt = LocalDateTime.now();
}
