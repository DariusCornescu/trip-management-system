package com.darius.project.repository.Database;
import com.darius.project.domain.Trip;
import com.darius.project.repository.GenericRepos.TripRepository;
import org.slf4j.*;
import java.sql.*;
import java.util.*;

public class TripDB extends RepoDB<Integer, Trip> implements TripRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TripDB.class);
    public TripDB() {
        openConnection();
        LOGGER.info("TripDB initialized, connection opened");
    }

    @Override
    public List<Trip> findByAttractionAndTime(String attraction, String startDate, String endDate) {
        LOGGER.info("Finding trips by attraction: '{}' between {} and {}", attraction, startDate, endDate);

        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM trips WHERE attractionName LIKE ? " + "AND SUBSTRING(departureTime, 1, 5) >= ? " + "AND SUBSTRING(departureTime, 1, 5) <= ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + attraction + "%");
            statement.setString(2, startDate);
            statement.setString(3, endDate);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                trips.add(new Trip(
                        resultSet.getInt("id"),
                        resultSet.getString("attractionName"),
                        resultSet.getString("transportCompany"),
                        resultSet.getString("departureTime"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("availableSeats")
                ));
            }
            LOGGER.info("Found {} trips matching attraction: '{}' between {} and {}", trips.size(), attraction, startDate, endDate);
        } catch (SQLException e) {
            LOGGER.error("Error finding trips by attraction and time. Attraction: {}, Start: {}, End: {}, Error: {}",
                    attraction, startDate, endDate, e.getMessage(), e);
        }
        return trips;
    }

    public List<Trip> findAllByAttractionContaining(String partialAttraction) {
        LOGGER.info("Finding trips containing attraction name: '{}'", partialAttraction);

        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT * FROM trips WHERE attractionName LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + partialAttraction + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                trips.add(new Trip(
                        resultSet.getInt("id"),
                        resultSet.getString("attractionName"),
                        resultSet.getString("transportCompany"),
                        resultSet.getString("departureTime"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("availableSeats")
                ));
            }
            LOGGER.info("Found {} trips containing attraction name: '{}'", trips.size(), partialAttraction);
        } catch (Exception e) {
            LOGGER.error("Error finding trips by partial attraction name: {}, Error: {}", partialAttraction, e.getMessage(), e);
        }
        return trips;
    }

    @Override
    public Iterator<Trip> findAll(){
        LOGGER.info("Retrieving all trips");

        ArrayList<Trip> trips = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement("SELECT * from trips")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                trips.add(new Trip(
                        resultSet.getInt("id"),
                        resultSet.getString("attractionName"),
                        resultSet.getString("transportCompany"),
                        resultSet.getString("departureTime"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("availableSeats")
                ));
            }
            LOGGER.info("Retrieved {} trips total", trips.size());
        } catch (Exception e) {
            LOGGER.error("Error retrieving all trips, Error: {}", e.getMessage(), e);
        }
        return trips.iterator();
    }

    @Override
    public Trip findById(Integer id) {
        LOGGER.info("Finding trip by ID: {}", id);

        Trip trip = null;
        try {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM trips where id =? ")) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    trip = new Trip(
                            resultSet.getInt("id"),
                            resultSet.getString("attractionName"),
                            resultSet.getString("transportCompany"),
                            resultSet.getString("departureTime"),
                            resultSet.getDouble("price"),
                            resultSet.getInt("availableSeats")
                    );
                    LOGGER.info("Found trip with ID: {}, attraction: {}", id, trip.getAttractionName());
                } else {
                    LOGGER.info("No trip found with ID: {}", id);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error finding trip by ID: {}, Error: {}", id, e.getMessage(), e);
        }
        return trip;
    }

    @Override
    public void save(Integer id, Trip trip) {
        LOGGER.info("Saving new trip with ID: {}, attraction: {}", id, trip.getAttractionName());

        super.save(id, trip);
        try {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO trips VALUES(?,?,?,?,?,?)")) {
                statement.setInt(1, id);
                statement.setString(2, trip.getAttractionName());
                statement.setString(3, trip.getTransportCompany());
                statement.setString(4, trip.getDepartureTime());
                statement.setDouble(5, trip.getPrice());
                statement.setInt(6, trip.getAvailableSeats());
                int rowsAffected = statement.executeUpdate();
                LOGGER.info("Successfully saved trip with ID: {}, rows affected: {}", id, rowsAffected);
            }
        } catch (Exception e) {
            LOGGER.error("Error saving trip with ID: {}, attraction: {}, Error: {}",
                    id, trip.getAttractionName(), e.getMessage(), e);
        }
    }

    @Override
    public void update(Integer id, Trip trip) {
        LOGGER.info("Updating trip with ID: {}, attraction: {}", id, trip.getAttractionName());

        super.update(id, trip);
        try {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE trips SET attractionName = ?, transportCompany = ?, departureTime = ?, price = ?, availableSeats = ? WHERE id = ?")) {
                statement.setString(1, trip.getAttractionName());
                statement.setString(2, trip.getTransportCompany());
                statement.setString(3, trip.getDepartureTime());
                statement.setDouble(4, trip.getPrice());
                statement.setInt(5, trip.getAvailableSeats());
                statement.setInt(6, id);
                int rowsAffected = statement.executeUpdate();
                LOGGER.info("Successfully updated trip with ID: {}, rows affected: {}", id, rowsAffected);
            }
        } catch (Exception e) {
            LOGGER.error("Error updating trip with ID: {}, attraction: {}, Error: {}",
                    id, trip.getAttractionName(), e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) {
        LOGGER.info("Deleting trip with ID: {}", id);

        super.delete(id);
        try {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM trips WHERE id = ?")) {
                statement.setInt(1, id);
                int rowsAffected = statement.executeUpdate();
                LOGGER.info("Successfully deleted trip with ID: {}, rows affected: {}", id, rowsAffected);
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting trip with ID: {}, Error: {}", id, e.getMessage(), e);
        }
    }
}