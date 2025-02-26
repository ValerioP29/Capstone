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
import java.util.List;
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
        // Creazione utenti clienti (100 utenti)
        for (int i = 1; i <= 100; i++) {
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
                score.setTotalScore(0);
                score.setTier("BRONZE");
                scoreRepository.save(score);
            }
        }

        // Creazione hotel e relativi proprietari (30 proprietari, 3 hotel per ciascuno)
        for (int i = 1; i <= 30; i++) {
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
                String[] images = {"hotel1.jpg", "hotel2.jpg", "hotel3.jpg"};

                for (int j = 1; j <= 3; j++) {
                    Hotel hotel = new Hotel();
                    hotel.setName("Hotel " + ((i - 1) * 3 + j)); // Nome unico per ogni hotel
                    hotel.setOwner(owner);
                    hotel.setLocation("Location " + ((i - 1) * 3 + j));

                    // Assegna ciclicamente un'immagine dalla lista
                    hotel.setImageUrl(images[j - 1]);

                    hotelRepository.save(hotel);
                }
            }
        }

        // Creazione di 500 feedback
        for (int i = 1; i <= 500; i++) {
            Feedback feedback = new Feedback();
            // Assegna un hotel a caso (ciclico, da 1 a 30)
            Hotel hotel = hotelRepository.findById((long) ((i % 30) + 1)).orElseThrow();
            // Assegna un cliente a caso (ciclico, da 1 a 100)
            User client = userRepository.findById((long) ((i % 100) + 1)).orElseThrow();
            feedback.setHotel(hotel);
            feedback.setClient(client);

            feedback.setCleanlinessScore(1 + (int) (Math.random() * 5));
            feedback.setRuleComplianceScore(1 + (int) (Math.random() * 5));
            feedback.setBehaviorScore(1 + (int) (Math.random() * 5));
            feedback.setRespectedCheckInOut(Math.random() < 0.8);
            feedback.setComments(generateRealisticComment());
            feedback.setCreatedAt(LocalDateTime.now().minusDays((int) (Math.random() * 30)));

            feedbackRepository.save(feedback);

            // Aggiorna il punteggio del cliente
            Score score = scoreRepository.findByClientId(feedback.getClient().getId()).orElseThrow();
            score.setTotalScore(score.getTotalScore() +
                    feedback.getCleanlinessScore() +
                    feedback.getRuleComplianceScore() +
                    feedback.getBehaviorScore());
            scoreRepository.save(score);
        }

        // Creazione premi di default (10 premi)
        rewardRepository.save(new Reward(null, "Soggiorno gratuito", "Una notte gratis in un hotel a scelta", 100));
        rewardRepository.save(new Reward(null, "Colazione gratuita", "Colazione gratuita in hotel", 20));
        rewardRepository.save(new Reward(null, "Upgrade camera", "Passaggio gratuito a una camera superiore", 30));
        rewardRepository.save(new Reward(null, "Sconto 10%", "10% di sconto sulla prenotazione", 20));
        rewardRepository.save(new Reward(null, "Late Checkout", "Check-out posticipato fino alle 15:00", 15));
        rewardRepository.save(new Reward(null, "Cena per due", "Cena romantica gratuita in hotel a scelta", 40));
        rewardRepository.save(new Reward(null, "Massaggio SPA", "Massaggio gratuito di 30 minuti", 35));
        rewardRepository.save(new Reward(null, "Parcheggio gratuito", "Parcheggio gratuito per tutto il soggiorno", 18));
        rewardRepository.save(new Reward(null, "WiFi premium", "WiFi ad alta velocità gratuito", 20));
        rewardRepository.save(new Reward(null, "Bevanda di benvenuto", "Cocktail o bevanda di benvenuto gratuita", 10));

        // --- DEMO: Cliente con nome reale ---
        if (userRepository.findByUsername("giovanni.rossi").isEmpty()) {
            User demoClient = new User();
            demoClient.setUsername("giovanni.rossi");
            demoClient.setEmail("giovanni.rossi@example.com");
            demoClient.setPassword(passwordEncoder.encode("demo123"));
            Set<Role> clientRoles = new HashSet<>();
            clientRoles.add(Role.ROLE_CLIENT);
            demoClient.setRoles(clientRoles);
            userRepository.save(demoClient);

            // Crea punteggio iniziale per il demo client
            Score demoScore = new Score();
            demoScore.setClient(demoClient);
            demoScore.setTotalScore(0);
            demoScore.setTier("BRONZE");
            scoreRepository.save(demoScore);
        }

        // --- DEMO: Owner e 3 Hotel reali con nomi, location e immagini diverse ---
        if (userRepository.findByUsername("alessandro.rossi").isEmpty()) {
            // Crea owner demo
            User demoOwner = new User();
            demoOwner.setUsername("alessandro.rossi");
            demoOwner.setEmail("alessandro.rossi@example.com");
            demoOwner.setPassword(passwordEncoder.encode("demo123"));
            Set<Role> ownerRoles = new HashSet<>();
            ownerRoles.add(Role.ROLE_HOTEL);
            demoOwner.setRoles(ownerRoles);
            userRepository.save(demoOwner);

            // Hotel 1
            Hotel demoHotel1 = new Hotel();
            demoHotel1.setName("Hotel Splendido");
            demoHotel1.setOwner(demoOwner);
            demoHotel1.setLocation("Roma, Via del Corso 12");
            demoHotel1.setImageUrl("https://example.com/images/hotel_splendido.jpg");
            hotelRepository.save(demoHotel1);

            // Hotel 2
            Hotel demoHotel2 = new Hotel();
            demoHotel2.setName("Hotel Mediterraneo");
            demoHotel2.setOwner(demoOwner);
            demoHotel2.setLocation("Napoli, Via Toledo 5");
            demoHotel2.setImageUrl("https://example.com/images/hotel_mediterraneo.jpg");
            hotelRepository.save(demoHotel2);

            // Hotel 3
            Hotel demoHotel3 = new Hotel();
            demoHotel3.setName("Hotel Paradiso");
            demoHotel3.setOwner(demoOwner);
            demoHotel3.setLocation("Firenze, Piazza della Signoria 7");
            demoHotel3.setImageUrl("https://example.com/images/hotel_paradiso.jpg");
            hotelRepository.save(demoHotel3);
        }

        // --- AGGIUNGI FEEDBACK DEMO per aggiornare i punteggi dei demo account ---
// Recupera il demo client e il demo owner
        User demoClient = userRepository.findByUsername("giovanni.rossi").orElse(null);
        User demoOwner = userRepository.findByUsername("alessandro.rossi").orElse(null);
        if (demoClient != null && demoOwner != null) {
            // Recupera gli hotel del demo owner usando findByOwnerId che accetta un Long
            List<Hotel> demoHotels = hotelRepository.findByOwnerId(demoOwner.getId());
            // Per ogni hotel demo, crea un feedback da parte del demo client
            for (Hotel demoHotel : demoHotels) {
                Feedback demoFeedback = new Feedback();
                demoFeedback.setHotel(demoHotel);
                demoFeedback.setClient(demoClient);
                demoFeedback.setCleanlinessScore(5);
                demoFeedback.setRuleComplianceScore(4);
                demoFeedback.setBehaviorScore(5);
                demoFeedback.setRespectedCheckInOut(true);
                demoFeedback.setComments("Esperienza eccellente!");
                demoFeedback.setCreatedAt(LocalDateTime.now().minusDays(1));
                feedbackRepository.save(demoFeedback);

                // Aggiorna il punteggio del demo client
                Score demoScore = scoreRepository.findByClientId(demoClient.getId()).orElseThrow();
                demoScore.setTotalScore(demoScore.getTotalScore() + 5 + 4 + 5);
                scoreRepository.save(demoScore);
            }
        }

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
