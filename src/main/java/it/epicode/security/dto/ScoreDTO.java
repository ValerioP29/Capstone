package it.epicode.security.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScoreDTO {

    @NotNull(message = "User ID is required")
    private Long clientId;

    @Min(value = 0, message = "Total Score cannot be negative")
    @NotNull(message = "Total score is required")
    private Integer totalScore;

    @NotNull(message = "Tier is required")
    private String tier;
}
