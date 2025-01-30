package it.epicode.security.config;

import it.epicode.security.auth.Role;
import it.epicode.security.model.Hotel;
import it.epicode.security.model.Score;
import it.epicode.security.model.User;
import it.epicode.security.repository.HotelRepository;
import it.epicode.security.repository.ScoreRepository;
import it.epicode.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializr implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ScoreRepository scoreRepository;

    @Override
    public void run(String... args) throws Exception {
        // Creazione utenti
        for (int i = 1; i <= 20; i++) {
            String username = "client" + i;
            String email = "client" + i + "@example.com";
            String password = passwordEncoder.encode("password" + i);
            Set<Role> roles = new HashSet<>();
            roles.add(Role.ROLE_CLIENT);

            if (userRepository.findByUsername(username).isEmpty()) {
                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setRoles(roles);
                userRepository.save(user);

                Score score = new Score();
                score.setClient(user);
                score.setTotalScore(0);  // Il punteggio iniziale è 0
                score.setTier("BRONZE"); // Tutti partono come BRONZE
                scoreRepository.save(score);
            }
        }

        // Creazione hotel e relativi proprietari
        for (int i = 1; i <= 10; i++) {
            String username = "hotelOwner" + i;
            String email = "hotelOwner" + i + "@example.com";
            String password = passwordEncoder.encode("password" + i);
            Set<Role> roles = new HashSet<>();
            roles.add(Role.ROLE_HOTEL);

            if (userRepository.findByUsername(username).isEmpty()) {
                User owner = new User();
                owner.setUsername(username);
                owner.setEmail(email);
                owner.setPassword(password);
                owner.setRoles(roles);
                userRepository.save(owner);

                Hotel hotel = new Hotel();
                hotel.setName("Hotel " + i);
                hotel.setOwner(owner);
                hotel.setLocation("Location" + i);
                hotelRepository.save(hotel);
            }
        }
    }
}
