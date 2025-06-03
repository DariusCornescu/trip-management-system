package com.darius.project.service;
import com.darius.project.domain.Trip;
import com.darius.project.repository.GenericRepos.TripRepository;
import org.slf4j.*;
import java.util.*;

public class TripService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TripService.class);
    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) { this.tripRepository = tripRepository; }

    public List<Trip> findByAttractionAndTime(String attraction, String startTime, String endTime) {
        LOGGER.info("Service: Finding trips by attraction: '{}' between {} and {}", attraction, startTime, endTime);

        List<Trip> trips = tripRepository.findByAttractionAndTime(attraction, startTime, endTime);
        LOGGER.info("Service: Found {} trips matching attraction: '{}' between {} and {}",
                trips.size(), attraction, startTime, endTime);
        return trips;
    }

    public List<Trip> findAllByAttractionContaining(String partialAttraction) {
        LOGGER.info("Service: Finding trips containing attraction name: '{}'", partialAttraction);

        List<Trip> trips = tripRepository.findAllByAttractionContaining(partialAttraction);
        LOGGER.info("Service: Found {} trips containing attraction name: '{}'",
                trips.size(), partialAttraction);
        return trips;
    }

    public Trip findById(Integer id) {
        LOGGER.info("Service: Finding trip by ID: {}", id);

        Trip trip = tripRepository.findById(id);
        if (trip != null) {
            LOGGER.info("Service: Found trip with ID: {}, attraction: {}, transport company: {}",
                    id, trip.getAttractionName(), trip.getTransportCompany());
        } else {
            LOGGER.info("Service: No trip found with ID: {}", id);
        }
        return trip;
    }

    public List<Trip> findAll() {
        LOGGER.info("Service: Retrieving all trips");

        List<Trip> trips = new ArrayList<>();
        Iterator<Trip> tripIterator = tripRepository.findAll();

        int count = 0;
        while (tripIterator.hasNext()) {
            trips.add(tripIterator.next());
            count++;
        }

        LOGGER.info("Service: Retrieved {} trips total", count);
        return trips;
    }

    public void save(Integer id, Trip trip) {
        if (trip == null) {
            LOGGER.error("Service: Cannot save null trip");
            throw new IllegalArgumentException("Trip cannot be null");
        }
        LOGGER.info("Service: Saving trip with ID: attraction: {}, departure: {}", trip.getAttractionName(), trip.getDepartureTime());

        try {
            tripRepository.save(id, trip);
            LOGGER.info("Service: Successfully saved trip with ID: {}", id);
        } catch (Exception e) {
            LOGGER.error("Service: Error saving trip with ID: {}, attraction: {}, error: {}",
                    id, trip.getAttractionName(), e.getMessage(), e);
            throw e;
        }
    }

    public void update(Integer id, Trip trip) {
        if (trip == null) {
            LOGGER.error("Service: Cannot update null trip");
            throw new IllegalArgumentException("Trip cannot be null");
        }

        LOGGER.info("Service: Updating trip with ID: {}, attraction: {}, departure: {}",
                id, trip.getAttractionName(), trip.getDepartureTime());

        try {
            Trip existingTrip = findById(id);
            if (existingTrip == null) {
                LOGGER.error("Service: Cannot update - trip with ID {} not found", id);
                throw new IllegalArgumentException("Trip not found with ID: " + id);
            }

            tripRepository.update(id, trip);
            LOGGER.info("Service: Successfully updated trip with ID: {}", id);
        } catch (Exception e) {
            LOGGER.error("Service: Error updating trip with ID: {}, attraction: {}, error: {}",
                    id, trip.getAttractionName(), e.getMessage(), e);
            throw e;
        }
    }

    public void delete(Integer id) {
        LOGGER.info("Service: Deleting trip with ID: {}", id);

        try {
            Trip existingTrip = findById(id);
            if (existingTrip == null) {
                LOGGER.warn("Service: Attempting to delete non-existent trip with ID: {}", id);
            }

            tripRepository.delete(id);
            LOGGER.info("Service: Successfully deleted trip with ID: {}", id);
        } catch (Exception e) {
            LOGGER.error("Service: Error deleting trip with ID: {}, error: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}