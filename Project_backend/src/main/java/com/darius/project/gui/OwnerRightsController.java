package com.darius.project.gui;
import com.darius.project.domain.Trip;
import com.darius.project.domain.User;
import com.darius.project.networking.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.*;

import java.util.ArrayList;
import java.util.List;

public class OwnerRightsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerRightsController.class);

    @FXML private Button ExitButton;
    @FXML private Button addUserBtn;
    @FXML private Button searchUserBtn;
    @FXML private Button deleteUserBtn;
    @FXML private Button updateUserBtn;
    @FXML private Button UndoUser;
    @FXML private Button RedoUser;
    @FXML private Button addTripBtn;
    @FXML private Button searchTripBtn;
    @FXML private Button deleteTripBtn;
    @FXML private Button updateTripBtn;
    @FXML private Button UndoTrip;
    @FXML private Button RedoTrip;

    @FXML private ListView<User> UserListCollection;
    @FXML private ListView<Trip> TripListCollection;

    @FXML private TextField UserIDField;
    @FXML private TextField UserNameField;
    @FXML private TextField UserPasswordField;

    @FXML private TextField TripIDField;
    @FXML private TextField TripAttractionField;
    @FXML private TextField TripTransportField;
    @FXML private TextField TripDepartureField;
    @FXML private TextField TripPriceField;
    @FXML private TextField TripSeatsField;

    public void initialize() {
        setupClientMessageListener();
        Client.getInstance().sendMessage("GET_USERS");
        Client.getInstance().sendMessage("GET_TRIPS");

        ExitButton.setOnAction(e -> handleExit());
        addUserBtn.setOnAction(e -> handleAddUser());
        deleteUserBtn.setOnAction(e -> handleDeleteUser());
        searchUserBtn.setOnAction(e -> handleSearchUser());
        updateUserBtn.setOnAction(e -> handleUpdateUser());
        addTripBtn.setOnAction(e -> handleAddTrip());
        deleteTripBtn.setOnAction(e -> handleDeleteTrip());
        searchTripBtn.setOnAction(e -> handleSearchTrip());
        updateTripBtn.setOnAction(e -> handleUpdateTrip());
        UndoUser.setOnAction(e -> handleUndoUser());
        RedoUser.setOnAction(e -> handleRedoUser());
        UndoTrip.setOnAction(e -> handleUndoTrip());
        RedoTrip.setOnAction(e -> handleRedoTrip());

        UserListCollection.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateFieldsUser(newVal);
            }
        });

        TripListCollection.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateFieldsTrip(newVal);
            }
        });

        UserNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                Client.getInstance().sendMessage("SEARCH_USERS#" + newVal);
            } else {
                Client.getInstance().sendMessage("GET_USERS");
            }
        });

        TripAttractionField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                Client.getInstance().sendMessage("SEARCH_TRIPS#" + newVal);
            } else {
                Client.getInstance().sendMessage("GET_TRIPS");
            }
        });
    }

    private void setupClientMessageListener() {
        Client.getInstance().setOnMessageReceivedListener(message -> {
            if (message.equals("UPDATE_USERS")) {
                Platform.runLater(() -> Client.getInstance().sendMessage("GET_USERS"));

            } else if (message.equals("UPDATE_TRIPS")) {
                Platform.runLater(() -> Client.getInstance().sendMessage("GET_TRIPS"));

            } else if (message.startsWith("USERS#")) {
                String userData = message.substring("USERS#".length());
                Platform.runLater(() -> updateUserList(userData));

            } else if (message.startsWith("TRIPS#")) {
                String tripData = message.substring("TRIPS#".length());
                Platform.runLater(() -> updateTripList(tripData));

            } else if (message.startsWith("USER_") && message.contains("SUCCESS")) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "User operation successful");
                    clearFields();
                });
            } else if (message.startsWith("TRIP_") && message.contains("SUCCESS")) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Trip operation successful");
                    clearFields();
                });
            } else if (message.startsWith("USER_") && message.contains("FAILED")) {
                String reason = message.contains("#") ? message.substring(message.indexOf('#') + 1) : "Unknown error";
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "User operation failed: " + reason));

            } else if (message.startsWith("TRIP_") && message.contains("FAILED")) {
                String reason = message.contains("#") ? message.substring(message.indexOf('#') + 1) : "Unknown error";
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Trip operation failed: " + reason));

            } else if (message.startsWith("SEARCH_RESULTS#")) {
                String results = message.substring("SEARCH_RESULTS#".length());
                Platform.runLater(() -> updateTripList(results));
            }
        });
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }

    private void updateUserList(String userData) {
        List<User> users = parseUsersFromString(userData);
        UserListCollection.getItems().setAll(users);
    }

    private void updateTripList(String tripData) {
        List<Trip> trips = parseTripsFromString(tripData);
        TripListCollection.getItems().setAll(trips);
    }

    private List<User> parseUsersFromString(String userData) {
        List<User> users = new ArrayList<>();
        if (userData == null || userData.isEmpty()) return users;
        String[] userStrings = userData.split(";");
        for (String userStr : userStrings) {
            if (!userStr.isEmpty()) {
                try {
                    String[] fields = userStr.split(",");
                    if (fields.length >= 3) {
                        int id = Integer.parseInt(fields[0]);
                        String username = fields[1];
                        String password = fields[2];
                        users.add(new User(id, username, password));
                    }
                } catch (Exception e) {
                    LOGGER.error("Error parsing user: {}, error: {}", userStr, e.getMessage(), e);
                }
            }
        }
        return users;
    }

    private List<Trip> parseTripsFromString(String tripData) {
        List<Trip> trips = new ArrayList<>();
        if (tripData == null || tripData.isEmpty()) return trips;
        String[] tripStrings = tripData.split(";");
        for (String tripStr : tripStrings) {
            if (!tripStr.isEmpty()) {
                try {
                    String[] fields = tripStr.split(",");
                    if (fields.length >= 6) {
                        int id = Integer.parseInt(fields[0]);
                        String attraction = fields[1];
                        String transport = fields[2];
                        String departureTime = fields[3];
                        double price = Double.parseDouble(fields[4]);
                        int seats = Integer.parseInt(fields[5]);
                        trips.add(new Trip(id, attraction, transport, departureTime, price, seats));
                    }
                } catch (Exception e) {
                    LOGGER.error("Error parsing trip: {}, error: {}", tripStr, e.getMessage(), e);
                }
            }
        }
        return trips;
    }

    private void populateFieldsUser(User user) {
        UserIDField.setText(String.valueOf(user.getId()));
        UserNameField.setText(user.getUsername());
        UserPasswordField.setText(user.getPassword());
    }

    private void populateFieldsTrip(Trip trip) {
        TripIDField.setText(String.valueOf(trip.getId()));
        TripAttractionField.setText(trip.getAttractionName());
        TripTransportField.setText(trip.getTransportCompany());
        TripDepartureField.setText(trip.getDepartureTime());
        TripPriceField.setText(String.valueOf(trip.getPrice()));
        TripSeatsField.setText(String.valueOf(trip.getAvailableSeats()));
    }

    private void handleExit() {
        Stage stage = (Stage) ExitButton.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        TripIDField.clear();
        TripAttractionField.clear();
        TripTransportField.clear();
        TripDepartureField.clear();
        TripPriceField.clear();
        TripSeatsField.clear();
        UserIDField.clear();
        UserNameField.clear();
        UserPasswordField.clear();
    }

    private void handleAddUser() {
        try {
            validateUserFields();
            int id = Integer.parseInt(UserIDField.getText());
            String username = UserNameField.getText();
            String password = UserPasswordField.getText();
            String userData = String.format("%d,%s,%s", id, username, password);
            Client.getInstance().sendMessage("ADD_USER#" + userData);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID format for user");
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }

    private void validateUserFields() {
        if (UserIDField.getText().trim().isEmpty()) throw new IllegalArgumentException("User ID is required");
        if (UserNameField.getText().trim().isEmpty()) throw new IllegalArgumentException("Username is required");
        if (UserPasswordField.getText().trim().isEmpty()) throw new IllegalArgumentException("Password is required");
    }

    private void handleDeleteUser() {
        try {
            User selectedUser = UserListCollection.getSelectionModel().getSelectedItem();
            int id;
            if (selectedUser != null) {
                id = selectedUser.getId();
            } else if (!UserIDField.getText().trim().isEmpty()) {
                id = Integer.parseInt(UserIDField.getText());
            } else {
                showAlert(Alert.AlertType.WARNING, "Please select a user or enter an ID");
                return;
            }
            Client.getInstance().sendMessage("DELETE_USER#" + id);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID format for user");
        }
    }

    private void handleSearchUser() {
        try {
            String username = UserNameField.getText().trim();
            if (username.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please enter a username to search");
                return;
            }
            Client.getInstance().sendMessage("SEARCH_USERS#" + username);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error searching for user: " + ex.getMessage());
        }
    }

    private void handleUpdateUser() {
        try {
            validateUserFields();
            User selectedUser = UserListCollection.getSelectionModel().getSelectedItem();
            if (selectedUser == null) {
                showAlert(Alert.AlertType.WARNING, "Please select a user to update");
                return;
            }
            int previousID = selectedUser.getId();
            int newID = Integer.parseInt(UserIDField.getText());
            String username = UserNameField.getText();
            String password = UserPasswordField.getText();
            String userData;
            if (previousID != newID) {
                userData = String.format("%d,%s,%s,%d", newID, username, password, previousID);
            } else {
                userData = String.format("%d,%s,%s", newID, username, password);
            }
            Client.getInstance().sendMessage("UPDATE_USER#" + userData);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID format for user");
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }

    private void handleAddTrip() {
        try {
            validateTripFields();
            int id = Integer.parseInt(TripIDField.getText());
            String attraction = TripAttractionField.getText();
            String transport = TripTransportField.getText();
            String departureTime = TripDepartureField.getText();
            double price = Double.parseDouble(TripPriceField.getText());
            int seats = Integer.parseInt(TripSeatsField.getText());
            String tripData = String.format("%d,%s,%s,%s,%.2f,%d", id, attraction, transport, departureTime, price, seats);
            Client.getInstance().sendMessage("ADD_TRIP#" + tripData);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format in fields");
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error adding trip: " + ex.getMessage());
        }
    }

    private void validateTripFields() {
        if (TripIDField.getText().trim().isEmpty()) throw new IllegalArgumentException("Trip ID is required");
        if (TripAttractionField.getText().trim().isEmpty()) throw new IllegalArgumentException("Attraction name is required");
        if (TripTransportField.getText().trim().isEmpty()) throw new IllegalArgumentException("Transport company is required");
        if (TripDepartureField.getText().trim().isEmpty()) throw new IllegalArgumentException("Departure time is required");
        if (TripPriceField.getText().trim().isEmpty()) throw new IllegalArgumentException("Price is required");
        if (TripSeatsField.getText().trim().isEmpty()) throw new IllegalArgumentException("Available seats is required");
    }

    private void handleDeleteTrip() {
        try {
            Trip selectedTrip = TripListCollection.getSelectionModel().getSelectedItem();
            int id;
            if (selectedTrip != null) {
                id = selectedTrip.getId();
            } else if (!TripIDField.getText().trim().isEmpty()) {
                id = Integer.parseInt(TripIDField.getText());
            } else {
                showAlert(Alert.AlertType.WARNING, "Please select a trip or enter an ID");
                return;
            }
            Client.getInstance().sendMessage("DELETE_TRIP#" + id);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID format for trip");
        }
    }

    private void handleSearchTrip() {
        try {
            String attraction = TripAttractionField.getText().trim();
            if (attraction.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please enter an attraction name to search");
                return;
            }
            Client.getInstance().sendMessage("SEARCH_TRIPS#" + attraction);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error searching for trip: " + ex.getMessage());
        }
    }

    private void handleUpdateTrip() {
        try {
            validateTripFields();
            Trip selectedTrip = TripListCollection.getSelectionModel().getSelectedItem();
            if (selectedTrip == null) {
                showAlert(Alert.AlertType.WARNING, "Please select a trip to update");
                return;
            }
            int previousID = selectedTrip.getId();
            int newID = Integer.parseInt(TripIDField.getText());
            String attraction = TripAttractionField.getText();
            String transport = TripTransportField.getText();
            String departureTime = TripDepartureField.getText();
            double price = Double.parseDouble(TripPriceField.getText());
            int seats = Integer.parseInt(TripSeatsField.getText());
            String tripData;
            if (previousID != newID) {
                tripData = String.format("%d,%s,%s,%s,%.2f,%d,%d", newID, attraction, transport, departureTime, price, seats, previousID);
            } else {
                tripData = String.format("%d,%s,%s,%s,%.2f,%d", newID, attraction, transport, departureTime, price, seats);
            }
            Client.getInstance().sendMessage("UPDATE_TRIP#" + tripData);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format in fields");
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error updating trip: " + ex.getMessage());
        }
    }

    private void handleUndoUser() {
        LOGGER.info("Undo user action not implemented");
    }

    private void handleRedoUser() {
        LOGGER.info("Redo user action not implemented");
    }

    private void handleUndoTrip() {
        LOGGER.info("Undo trip action not implemented");
    }

    private void handleRedoTrip() {
        LOGGER.info("Redo trip action not implemented");
    }
}
