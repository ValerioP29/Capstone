package it.epicode.security.dto;

import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequestDTO {
    private String username;
    private String password;
    private String email;
    private Set<String> roles;

    public Set<String> getRoles() {
        return roles;
    }
}