package it.epicode.security.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackDTO {

    private Long hotelId;
    private Long clientId;

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

}
