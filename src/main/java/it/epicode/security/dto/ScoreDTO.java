package it.epicode.security.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScoreDTO {

    @NotNull(message = "User ID is required")
    private Long clientId;

    @NotNull(message = "Username required")
    private String username;

    @Min(value = 0, message = "Total Score cannot be negative")
    @NotNull(message = "Total score is required")
    private Integer totalScore;

    @NotNull(message = "Tier is required")
    private String tier;
}
