package com.darius.project.repository.Database;

import com.darius.project.domain.Reservation;
import com.darius.project.repository.GenericRepos.ReservationRepository;
import org.slf4j.*;
import java.sql.*;
import java.util.*;

public class ReservationDB extends RepoDB<Integer, Reservation> implements ReservationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationDB.class);
    public ReservationDB() {
        openConnection();
        LOGGER.info("ReservationDB initialized, connection opened");
    }

    @Override
    public List<Reservation> findByTripId(Integer tripId) {
        LOGGER.info("Finding reservations for trip ID: {}", tripId);
        List<Reservation> reservations = new ArrayList<>();
        try {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM reservations where tripId =? ")) {
                statement.setInt(1, tripId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    reservations.add(new Reservation(
                            resultSet.getInt("id"),
                            resultSet.getInt("tripId"),
                            resultSet.getInt("customerId"),
                            resultSet.getInt("numberOfTickets"))
                    );
                }
                LOGGER.info("Found {} reservations for trip ID: {}", reservations.size(), tripId);
            }
        } catch (Exception e) {
            LOGGER.error("Error finding reservations for trip ID {}, error: {}", tripId, e.getMessage(), e);
        }
        return reservations;
    }

    @Override
    public Reservation findById(Integer reservationId) {
        LOGGER.info("Finding reservation by ID: {}", reservationId);

        Reservation reservation = null;
        try {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM reservations where id =? ")) {
                statement.setInt(1, reservationId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    reservation = new Reservation(
                            resultSet.getInt("id"),
                            resultSet.getInt("tripId"),
                            resultSet.getInt("customerId"),
                            resultSet.getInt("numberOfTickets")
                    );
                    LOGGER.info("Found reservation with ID: {}", reservationId);
                } else {
                    LOGGER.info("No reservation found with ID: {}", reservationId);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error finding reservation by ID {}, error: {}", reservationId, e.getMessage(), e);
        }
        return reservation;
    }

    @Override
    public Iterator<Reservation> findAll() {
        LOGGER.info("Retrieving all reservations");

        ArrayList<Reservation> reservations = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * from reservations")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                reservations.add(new Reservation(
                        resultSet.getInt("id"),
                        resultSet.getInt("tripId"),
                        resultSet.getInt("customerId"),
                        resultSet.getInt("numberOfTickets")
                ));
            }
            LOGGER.info("Retrieved {} reservations", reservations.size());
        } catch (Exception e) {
            LOGGER.error("Error retrieving all reservations, error: {}", e.getMessage(), e);
        }
        return reservations.iterator();
    }

    @Override
    public void save(Integer id, Reservation reservation) {
        LOGGER.info("Saving new reservation with ID: {}", id);

        super.save(id, reservation);
        try {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO reservations VALUES(?,?,?,?)")) {
                statement.setInt(1, id);
                statement.setInt(2, reservation.getTrip());
                statement.setInt(3, reservation.getCustomer());
                statement.setInt(4, reservation.getNumberOfTickets());
                int rowsAffected = statement.executeUpdate();
                LOGGER.info("Successfully saved reservation with ID: {}, rows affected: {}", id, rowsAffected);
            }
        } catch (Exception e) {
            LOGGER.error("Error saving reservation with ID {}, error: {}", id, e.getMessage(), e);
        }
    }

    @Override
    public void update(Integer id, Reservation reservation) {
        LOGGER.info("Updating reservation with ID: {}", id);

        super.update(id, reservation);
        try {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE reservations SET tripId = ?, customerId = ?, numberOfTickets = ? WHERE id = ?")) {
                statement.setInt(1, reservation.getTrip());
                statement.setInt(2, reservation.getCustomer());
                statement.setInt(3, reservation.getNumberOfTickets());
                statement.setInt(4, id);
                int rowsAffected = statement.executeUpdate();
                LOGGER.info("Successfully updated reservation with ID: {}, rows affected: {}", id, rowsAffected);
            }
        } catch (Exception e) {
            LOGGER.error("Error updating reservation with ID {}, error: {}", id, e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        LOGGER.info("Deleting reservation with ID: {}", id);

        super.delete(id);
        try {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM reservations WHERE id=?")) {
                statement.setInt(1, id);
                int rowsAffected = statement.executeUpdate();
                LOGGER.info("Successfully deleted reservation with ID: {}, rows affected: {}", id, rowsAffected);
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting reservation with ID {}, error: {}", id, e.getMessage(), e);
        }
    }
}