package it.epicode.security.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "hotel_id", referencedColumnName = "id")
    private Hotel hotel; // Hotel che lascia il feedback

    @ManyToOne
    @JoinColumn(nullable = false, name = "client_id", referencedColumnName = "id")
    private User client; // Cliente che riceve il feedback

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int cleanlinessScore;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int ruleComplianceScore;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int behaviorScore;

    @Column(nullable = false)
    private boolean respectedCheckInOut;

    @NotBlank(message = "Comments are required")
    private String comments;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
