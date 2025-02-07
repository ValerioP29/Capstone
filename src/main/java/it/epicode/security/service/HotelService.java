package it.epicode.security.service;

import it.epicode.security.dto.HotelDTO;
import it.epicode.security.exceptions.ResourceNotFoundException;
import it.epicode.security.model.Hotel;
import it.epicode.security.model.User;
import it.epicode.security.repository.HotelRepository;
import it.epicode.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    public Hotel findById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel with ID " + id + " not found"));
    }

    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    public Hotel createHotel(HotelDTO hotelDTO) {
        User owner = userRepository.findById(hotelDTO.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner with ID " + hotelDTO.getOwnerId() + " not found"));

        Hotel hotel = new Hotel();
        hotel.setName(hotelDTO.getName());
        hotel.setLocation(hotelDTO.getLocation());
        hotel.setOwner(owner);

        return hotelRepository.save(hotel);
    }

    public Hotel updateHotel(Long id, HotelDTO hotelDTO) {
        Hotel hotel = findById(id);

        hotel.setName(hotelDTO.getName());
        hotel.setLocation(hotelDTO.getLocation());

        return hotelRepository.save(hotel);
    }

    public void deleteHotel(Long id) {
        Hotel hotel = findById(id);
        hotelRepository.delete(hotel);
    }

    public List<Hotel> findHotelsByOwner(Long ownerId) {
        return hotelRepository.findByOwnerId(ownerId);
    }

}
