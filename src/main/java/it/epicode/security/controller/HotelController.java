package it.epicode.security.controller;

import it.epicode.security.dto.HotelDTO;
import it.epicode.security.model.Hotel;
import it.epicode.security.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    HotelService hotelService;

    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @PostMapping
    public ResponseEntity<Hotel> createHotel(@Valid @RequestBody HotelDTO hotelDTO) {
        Hotel hotel = hotelService.createHotel(hotelDTO);
        return ResponseEntity.ok(hotel);
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
    @PutMapping("/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @Valid @RequestBody HotelDTO hotelDTO) {
        return ResponseEntity.ok(hotelService.updateHotel(id, hotelDTO));
    }

    @PreAuthorize("hasRole('ROLE_HOTEL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.ok("Hotel deleted successfully");
    }
}
