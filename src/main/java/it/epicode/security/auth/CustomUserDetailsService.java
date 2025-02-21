package it.epicode.security.auth;

import it.epicode.security.model.Hotel;
import it.epicode.security.model.User;
import it.epicode.security.repository.HotelRepository;
import it.epicode.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con username: " + username));

        System.out.println("🔍 Utente trovato: " + user.getUsername());
        System.out.println("🔍 Password salvata nel database: " + user.getPassword());

        // ✅ Recuperiamo l'hotelId solo se l'utente ha il ruolo ROLE_HOTEL
        Long hotelId = null;
        if (user.getRoles().stream().anyMatch(role -> role.name().equals("ROLE_HOTEL"))) {
            List<Hotel> hotels = hotelRepository.findByOwnerId(user.getId()); // 🔍 Ottieni tutti gli hotel dell'utente
            if (!hotels.isEmpty()) {
                hotelId = hotels.get(0).getId(); // 🔥 Prendiamo il primo hotel se esiste
            }
        }

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toList()),
                hotelId // ✅ Aggiunto `hotelId`
        );
    }
}
