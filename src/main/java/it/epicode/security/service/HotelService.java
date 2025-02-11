package it.epicode.security.service;

import it.epicode.security.dto.HotelDTO;
import it.epicode.security.exceptions.ResourceNotFoundException;
import it.epicode.security.model.Hotel;
import it.epicode.security.model.User;
import it.epicode.security.repository.HotelRepository;
import it.epicode.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService; // ✅ Usa il nuovo FileService

    public Hotel findById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with ID " + id + " not found"));
    }

    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    public Hotel createHotel(HotelDTO hotelDTO, MultipartFile image) {
        User owner = userRepository.findById(hotelDTO.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner with ID " + hotelDTO.getOwnerId() + " not found"));

        Hotel hotel = new Hotel();
        hotel.setName(hotelDTO.getName());
        hotel.setLocation(hotelDTO.getLocation());
        hotel.setOwner(owner);

        // ✅ Salvataggio immagine
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileService.saveImage(image);
            hotel.setImageUrl(imageUrl);
        } else {
            hotel.setImageUrl("default-hotel.jpg"); // ✅ Ora solo il nome del file
        }

        return hotelRepository.save(hotel);
    }

    public Hotel updateHotel(Long id, HotelDTO hotelDTO, MultipartFile image) {
        System.out.println("🛠️ [DEBUG] updateHotel() chiamato con ID: " + id);

        Hotel hotel = findById(id);
        System.out.println("✅ [DEBUG] Hotel trovato: " + hotel.getName());
        System.out.println("🔄 [DEBUG] Valori attuali - Name: " + hotel.getName() + ", Location: " + hotel.getLocation() + ", ImageURL: " + hotel.getImageUrl());

        hotel.setName(hotelDTO.getName());
        hotel.setLocation(hotelDTO.getLocation());

        if (image != null && !image.isEmpty()) {
            // ✅ Salva il file e aggiorna `imageUrl`
            String imageUrl = fileService.saveImage(image);
            hotel.setImageUrl(imageUrl);
            System.out.println("✅ [DEBUG] Nuova immagine salvata: " + imageUrl);
        } else {
            System.out.println("⚠️ [DEBUG] Nessuna nuova immagine ricevuta, manteniamo: " + hotel.getImageUrl());
        }

        System.out.println("🛠️ [DEBUG] Salvando hotel nel database con ImageURL: " + hotel.getImageUrl());
        Hotel updatedHotel = hotelRepository.save(hotel);
        System.out.println("✅ [DEBUG] Hotel salvato con successo. ImageURL dopo il salvataggio: " + updatedHotel.getImageUrl());

        System.out.println("✅ [DEBUG] Hotel aggiornato e salvato nel DB: " + updatedHotel.getImageUrl());

        return updatedHotel;
    }





    public void deleteHotel(Long id) {
        Hotel hotel = findById(id);
        hotelRepository.delete(hotel);
    }

    public List<Hotel> findHotelsByOwner(Long ownerId) {
        if (ownerId == null) {
            throw new IllegalArgumentException("L'ID del proprietario non può essere nullo");
        }
        return hotelRepository.findByOwnerId(ownerId);
    }

}
