package it.epicode.security.config;

import it.epicode.security.auth.Role;
import it.epicode.security.model.Hotel;
import it.epicode.security.model.Reward;
import it.epicode.security.model.Score;
import it.epicode.security.model.User;
import it.epicode.security.repository.HotelRepository;
import it.epicode.security.repository.RewardRepository;
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

    @Autowired
    private RewardRepository rewardRepository;

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



                    // Creazione 10 premi di default
                    if (rewardRepository.count() == 0) {
                        rewardRepository.save(new Reward(null, "Soggiorno gratuito", "Una notte gratis in un hotel a scelta", 500));
                        rewardRepository.save(new Reward(null, "Colazione gratuita", "Colazione gratuita in hotel", 200));
                        rewardRepository.save(new Reward(null, "Upgrade camera", "Passaggio gratuito a una camera superiore", 300));
                        rewardRepository.save(new Reward(null, "Sconto 10%", "10% di sconto sulla prenotazione", 100));
                        rewardRepository.save(new Reward(null, "Late Checkout", "Check-out posticipato fino alle 15:00", 150));
                        rewardRepository.save(new Reward(null, "Cena per due", "Cena romantica gratuita in hotel", 400));
                        rewardRepository.save(new Reward(null, "Massaggio SPA", "Massaggio gratuito di 30 minuti", 350));
                        rewardRepository.save(new Reward(null, "Parcheggio gratuito", "Parcheggio gratuito per tutta la durata del soggiorno", 180));
                        rewardRepository.save(new Reward(null, "WiFi premium", "WiFi ad alta velocità gratuito", 120));
                        rewardRepository.save(new Reward(null, "Bevanda di benvenuto", "Cocktail o bevanda di benvenuto gratuita", 50));
                    }
                }

            }
        }
    }

