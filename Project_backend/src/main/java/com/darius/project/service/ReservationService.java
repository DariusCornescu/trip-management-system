package com.darius.project.service;

import com.darius.project.domain.Reservation;
import com.darius.project.repository.GenericRepos.ReservationRepository;
import org.slf4j.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) { this.reservationRepository = reservationRepository; }

    public List<Reservation> findByTripId(Integer tripId) {
        LOGGER.info("Service: Finding reservations for trip ID: {}", tripId);
        List<Reservation> reservations = reservationRepository.findByTripId(tripId);
        LOGGER.info("Service: Found {} reservations for trip ID: {}", reservations.size(), tripId);
        return reservations;
    }

    public Reservation findById(Integer id) {
        LOGGER.info("Service: Finding reservation by ID: {}", id);

        Reservation reservation = reservationRepository.findById(id);
        if (reservation != null) {
            LOGGER.info("Service: Found reservation with ID: {}, trip ID: {}, customer ID: {}",
                    id, reservation.getTrip(), reservation.getCustomer());
        } else {
            LOGGER.info("Service: No reservation found with ID: {}", id);
        }
        return reservation;
    }

    public void save(Integer id, Reservation reservation) {
        if (reservation == null) {
            LOGGER.error("Service: Cannot save null reservation");
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        LOGGER.info("Service: Saving reservation with ID: {}, trip ID: {}, customer ID: {}, tickets: {}",
                id, reservation.getTrip(), reservation.getCustomer(), reservation.getNumberOfTickets());

        try {
            reservationRepository.save(id, reservation);
            LOGGER.info("Service: Successfully saved reservation with ID: {}", id);
        } catch (Exception e) {
            LOGGER.error("Service: Error saving reservation with ID: {}, error: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void delete(Integer id) {
        LOGGER.info("Service: Deleting reservation with ID: {}", id);

        try {
            Reservation existingReservation = findById(id);
            if (existingReservation == null) {
                LOGGER.warn("Service: Attempting to delete non-existent reservation with ID: {}", id);
            }

            reservationRepository.delete(id);
            LOGGER.info("Service: Successfully deleted reservation with ID: {}", id);
        } catch (Exception e) {
            LOGGER.error("Service: Error deleting reservation with ID: {}, error: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}