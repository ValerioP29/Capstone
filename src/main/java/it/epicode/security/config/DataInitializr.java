package it.epicode.security.config;

import it.epicode.security.auth.Role;
import it.epicode.security.model.*;
import it.epicode.security.repository.*;
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

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public void run(String... args) throws Exception {
        // Creazione utenti
        for (int i = 1; i <= 100; i++) { // Creiamo 100 utenti clienti
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

                // Crea punteggio iniziale per il cliente
                Score score = new Score();
                score.setClient(user);
                score.setTotalScore(0);  // Punteggio iniziale a 0
                score.setTier("BRONZE"); // Tutti i clienti partono con il tier "BRONZE"
                scoreRepository.save(score);
            }
        }

        // Creazione hotel e relativi proprietari (3 hotel per utente)
        for (int i = 1; i <= 30; i++) { // Creiamo 30 proprietari di hotel
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

                // Creazione 3 hotel per ogni proprietario
                for (int j = 1; j <= 3; j++) {
                    Hotel hotel = new Hotel();
                    hotel.setName("Hotel " + ((i - 1) * 3 + j)); // Nome unico per ogni hotel
                    hotel.setOwner(owner);
                    hotel.setLocation("Location " + ((i - 1) * 3 + j));
                    hotel.setImageUrl("default-hotel-image.jpg");
                    hotelRepository.save(hotel);
                }
            }
        }

        // Creazione di 500 feedback
        for (int i = 1; i <= 500; i++) {
            Feedback feedback = new Feedback();
            // Assegna un hotel a caso
            Hotel hotel = hotelRepository.findById((long) ((i % 30) + 1)).orElseThrow();  // Esempio: ciclo sugli hotel

            // Assegna un cliente a caso
            User client = userRepository.findById((long) ((i % 100) + 1)).orElseThrow();  // Esempio: ciclo sui clienti
            feedback.setHotel(hotel);
            feedback.setClient(client);

            feedback.setCleanlinessScore(1 + (int) (Math.random() * 5)); // Punteggio random tra 1 e 5
            feedback.setRuleComplianceScore(1 + (int) (Math.random() * 5));
            feedback.setBehaviorScore(1 + (int) (Math.random() * 5));
            feedback.setRespectedCheckInOut(Math.random() < 0.8); // 80% probabilità che il check-in/out venga rispettato
            feedback.setComments(generateRealisticComment());  // Commento realistico
            feedback.setCreatedAt(LocalDateTime.now().minusDays((int) (Math.random() * 30)));  // Feedback creato negli ultimi 30 giorni

            feedbackRepository.save(feedback);

            // Aggiorna il punteggio del cliente
            Score score = scoreRepository.findByClientId(feedback.getClient().getId()).orElseThrow();
            score.setTotalScore(score.getTotalScore() +
                    feedback.getCleanlinessScore() +
                    feedback.getRuleComplianceScore() +
                    feedback.getBehaviorScore());
            scoreRepository.save(score);
        }

        // Creazione premi di default
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

    private String generateRealisticComment() {
        String[] comments = {
                "Cliente molto educato, ma potrebbe migliorare la puntualità nel check-in.",
                "Il cliente è stato gentile, ma ha lasciato la stanza in condizioni non ottimali.",
                "Abbiamo avuto un ottimo soggiorno con questo cliente, ma non ha rispettato tutte le regole.",
                "Il cliente è stato cordiale, ma ha fatto troppo rumore durante il soggiorno.",
                "Molto soddisfatti del comportamento del cliente, sempre rispettoso delle regole.",
                "Cliente tranquillo, ma il check-out è stato un po' più lento del previsto.",
                "Ottima esperienza con il cliente, lo raccomandiamo senza riserve.",
                "Non ha rispettato alcune regole dell'hotel, ma ha comunque comportamenti positivi.",
                "Cliente affidabile e rispettoso delle politiche dell'hotel.",
                "Il soggiorno del cliente è stato piacevole, ma ha avuto qualche piccolo inconveniente."
        };
        int index = (int) (Math.random() * comments.length);
        return comments[index];
    }
}
