package it.epicode.security.service;

import it.epicode.security.exceptions.ResourceNotFoundException;
import it.epicode.security.model.Hotel;
import it.epicode.security.model.User;
import it.epicode.security.repository.HotelRepository;
import it.epicode.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    // Creazione hotel

    public Hotel createHotel(Long ownerId, Hotel hotel) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(()-> new ResourceNotFoundException("Owner with ID " + ownerId + " not found"));
        hotel.setOwner(owner);
        return hotelRepository.save(hotel);
    }
    //Lista hotel di un proprietario
   public List<Hotel> getHotelsByOwner(Long ownerId){
        if (!userRepository.existsById(ownerId)){
            throw new ResourceNotFoundException("Owner with ID " + ownerId + " not found");
        }
        return hotelRepository.findByOwner_Id(ownerId);
   }
}
