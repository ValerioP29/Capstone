package it.epicode.security.auth;

import it.epicode.security.model.Hotel;
import it.epicode.security.model.User;
import it.epicode.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con username: " + username));

        System.out.println("🔍 Utente trovato: " + user.getUsername());
        System.out.println("🔍 Password salvata nel database: " + user.getPassword());

        // ✅ Recuperiamo l'hotelId solo se l'utente ha il ruolo ROLE_HOTEL
        Long hotelId = null;
        if (user.getRoles().stream().anyMatch(role -> role.name().equals("ROLE_HOTEL"))) {
            Hotel hotel = userRepository.findHotelByOwnerId(user.getId());
            if (hotel != null) {
                hotelId = hotel.getId();
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

