package com.voyageai.voyage_ai_backend.service;

import com.voyageai.voyage_ai_backend.entity.Trip;
import com.voyageai.voyage_ai_backend.repository.TripRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TripService {

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void saveTrip(String email, Object request, String plan) {
        Trip trip = new Trip();
        trip.setUserEmail(email);
        trip.setRequest(request);
        trip.setPlan(plan);
        trip.setCreatedAt(LocalDateTime.now());

        tripRepository.save(trip);
    }

    public List<Trip> getUserTrips(String email) {
        return tripRepository.findByUserEmail(email);
    }
}