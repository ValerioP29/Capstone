package it.epicode.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.epicode.security.model.User;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @JsonIgnore
      private Long id;

      @NotBlank(message = "Username is required")
      private String username;

      @Email(message = "Invalid email format")
      @NotBlank(message = "Email is required")
      private String email;

      @NotNull(message = "Role is required")
      private String role;

      private int totalScore;

      public UserDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.role = user.getRoles().toString();
            this.totalScore = user.getScore() != null ? user.getScore().getTotalScore() : 0;
      }
}
