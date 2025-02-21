package it.epicode.security.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
    public class HotelDTO {

        @NotBlank(message = "Hotel name is required")
        private String name;

        @NotBlank(message = "Location is required")
        private String location;

        @NotNull(message = "Owner ID is required")
        private Long ownerId;

        @Column
        private int stars;

        private String imageUrl;
    }


