package it.epicode.security.service;

import it.epicode.security.dto.FeedbackDTO;
import it.epicode.security.exceptions.ResourceNotFoundException;
import it.epicode.security.model.Feedback;
import it.epicode.security.model.Hotel;
import it.epicode.security.model.User;
import it.epicode.security.repository.FeedbackRepository;
import it.epicode.security.repository.HotelRepository;
import it.epicode.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    // Creazione Feedback
    public Feedback createFeedback(FeedbackDTO feedbackDTO) {
        User client = userRepository.findById(feedbackDTO.getClientId()).orElseThrow(() -> new ResourceNotFoundException("Client with ID " + feedbackDTO.getClientId() + "not found"));

        Hotel hotel = hotelRepository.findById(feedbackDTO.getHotelId()).orElseThrow(() -> new ResourceNotFoundException("Hotel with ID " + feedbackDTO.getHotelId() + "not found"));

        Feedback feedback = new Feedback();
        feedback.setClient(client);
        feedback.setHotel(hotel);
        feedback.setCleanlinessScore(feedbackDTO.getCleanlinessScore());
        feedback.setBehaviorScore(feedbackDTO.getBehaviorScore());
        feedback.setRuleComplianceScore(feedbackDTO.getRuleComplianceScore());
        feedback.setRespectedCheckInOut(feedbackDTO.isRespectedCheckInOut());
        feedback.setComments(feedbackDTO.getComments());

        return feedbackRepository.save(feedback);

    }



    // Recupero dei feedback
     public List<Feedback> getFeedbackByClient (Long clientId) {
        if (!userRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client with ID " + clientId + "not found");
        }
        return feedbackRepository.findByClientId(clientId);
     }

    public List<Feedback> getFeedbackByHotelOwner(Long ownerId) {
        // Controlla se l'utente esiste ed è un hotel
        if (!userRepository.existsById(ownerId)) {
            throw new ResourceNotFoundException("User with ID " + ownerId + " not found");
        }

        // Ottieni tutti gli hotel posseduti dall'utente
        List<Hotel> ownedHotels = hotelRepository.findByOwnerId(ownerId);

        if (ownedHotels.isEmpty()) {
            return List.of(); // Se non ha hotel, ritorna lista vuota
        }

        // Estrai gli ID degli hotel
        List<Long> hotelIds = ownedHotels.stream().map(Hotel::getId).toList();

        // Cerca tutti i feedback relativi a questi hotel
        return feedbackRepository.findByHotelIdIn(hotelIds);
    }


}
