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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

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

    public Hotel updateHotel(Long id, HotelDTO hotelDTO, MultipartFile imageFile) throws IOException {
        Hotel hotel = findById(id); // Trova l'hotel nel DB

        hotel.setName(hotelDTO.getName());
        hotel.setLocation(hotelDTO.getLocation());

        if (imageFile != null && !imageFile.isEmpty()) {
            // Salviamo il file nella cartella uploads
            String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get("uploads").resolve(filename);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // ✅ Salviamo il percorso RELATIVO nel database
            hotel.setImageUrl( filename);
        }

        return hotelRepository.save(hotel);
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
