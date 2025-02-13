package it.epicode.security.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.epicode.security.auth.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles;


    @Getter
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Score score;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Hotel> hotels;

    // Metodo per ottenere il primo hotel associato all'utente
    public Hotel getHotel() {
        return hotels != null && !hotels.isEmpty() ? hotels.iterator().next() : null;
    }
    public void setScore(Score score) {
        this.score = score;
        score.setClient(this);
    }
}
