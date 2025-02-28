package it.epicode.security.controller;

import it.epicode.security.auth.JwtTokenUtil;
import it.epicode.security.dto.HotelDTO;
import it.epicode.security.exceptions.ResourceNotFoundException;
import it.epicode.security.model.Hotel;
import it.epicode.security.model.User;
import it.epicode.security.repository.UserRepository;
import it.epicode.security.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Hotel> createHotel(
            @RequestParam("name") String name,
            @RequestParam("location") String location,
            @RequestHeader ("Authorization") String token,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        token = token.replace("Bearer ", "").trim();
        Long ownerId = jwtTokenUtil.getUserIdFromToken(token);

        // Log per verificare i dati ricevuti
        System.out.println("📥 Ricevuta richiesta per creare un hotel");
        System.out.println("Nome: " + name);
        System.out.println("Posizione: " + location);


        if (image != null) {
            System.out.println("📸 Immagine ricevuta: " + image.getOriginalFilename());
            System.out.println("Tipo di contenuto immagine: " + image.getContentType());
            System.out.println("Dimensione immagine: " + image.getSize() + " byte");
        } else {
            System.out.println("❌ Nessuna immagine caricata.");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found for ID: " + ownerId));

        System.out.println("📥 Creazione hotel richiesta da: " + owner.getUsername() + " (ID: " + owner.getId() + ")");


        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setName(name);
        hotelDTO.setLocation(location);
        hotelDTO.setOwnerId(owner.getId());

        try {
            // ✅ Passa l'immagine al service
            Hotel hotel = hotelService.createHotel(hotelDTO, image);
            System.out.println("✅ Hotel creato con successo: " + hotel.getName());
            return ResponseEntity.ok(hotel);
        } catch (Exception e) {
            System.err.println("❌ Errore durante la creazione dell'hotel: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_HOTEL')")
    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.findById(id));
    }

    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        return ResponseEntity.ok(hotelService.findAll());
    }
    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Hotel> updateHotel(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("location") String location,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException { // ✅ Aggiunto throws IOException

        System.out.println("🛠️ Aggiornamento hotel ID: " + id);
        System.out.println("📌 Nome: " + name);
        System.out.println("📌 Location: " + location);

        if (image != null) {
            System.out.println("📸 Nuova immagine ricevuta: " + image.getOriginalFilename());
        }

        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setName(name);
        hotelDTO.setLocation(location);

        Hotel updatedHotel = hotelService.updateHotel(id, hotelDTO, image);
        return ResponseEntity.ok(updatedHotel);
    }

    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.ok("Hotel deleted successfully");
    }

    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @GetMapping("/my-hotel")
    public ResponseEntity<List<Hotel>> getMyHotels(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "").trim();
        String username = jwtTokenUtil.getUsernameFromToken(token);

        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }

        User owner = userRepository.findByUsername(username).orElse(null);
        if (owner == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        List<Hotel> hotels = hotelService.findHotelsByOwnerId(owner.getId());

        hotels.forEach(hotel -> {
            if (hotel.getImageUrl() != null && !hotel.getImageUrl().startsWith("http")) {
                hotel.setImageUrl("http://localhost:8080/uploads/" + hotel.getImageUrl());
            }
        });

        return ResponseEntity.ok(hotels);
    }


}
