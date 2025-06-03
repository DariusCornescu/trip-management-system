package com.darius.project.gui;

import com.darius.project.domain.Trip;
import com.darius.project.networking.Client;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import org.slf4j.*;
import java.util.*;

public class UserInterfaceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserInterfaceController.class);

    @FXML private ListView<Trip> AttractionListCollection;
    @FXML private ListView<Trip> FilteredAttractions;
    @FXML private TextField AttaractionField, TimeStartField, TimeEndField;
    @FXML private TextField CustomerNameField, CustomerPhoneField, CustomerTicketsField;
    @FXML private Button MakeReservationBtn, UserExitBtn;

    private int requestedTickets = 0;

    public void initialize() {
        LOGGER.info("Setting up client message listener");
        setupClientMessageListener();

        LOGGER.info("Loading initial attraction data");
        loadAllAttractions();

        UserExitBtn.setOnAction(e -> handleExit());

        AttaractionField.textProperty().addListener((obs, oldText, newText) -> filterAttractions());
        TimeStartField.textProperty().addListener((obs, oldText, newText) -> filterAttractions());
        TimeEndField.textProperty().addListener((obs, oldText, newText) -> filterAttractions());

        CustomerTicketsField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                requestedTickets = Integer.parseInt(newVal);
                LOGGER.debug("Requested tickets changed to {}", requestedTickets);
            } catch (NumberFormatException e) {
                requestedTickets = 0;
            }
            FilteredAttractions.refresh();
        });

        setupFilteredAttractionsCellFactory();
        MakeReservationBtn.setOnAction(e -> handleMakeReservation());

        LOGGER.info("Requesting initial trip data from server");
        Client.getInstance().sendMessage("GET_TRIPS");
    }

    private void setupClientMessageListener() {
        Client.getInstance().setOnMessageReceivedListener(message -> {
            LOGGER.debug("Received message from server: {}", message);

            if (message.equals("UPDATE_TRIPS")) {
                Platform.runLater(() -> Client.getInstance().sendMessage("GET_TRIPS"));
            } else if (message.startsWith("TRIPS#")) {
                String tripData = message.substring("TRIPS#".length());
                Platform.runLater(() -> updateTripList(tripData));
            } else if (message.startsWith("SEARCH_RESULTS#")) {
                String results = message.substring("SEARCH_RESULTS#".length());
                Platform.runLater(() -> updateFilteredAttractions(results));
            } else if (message.equals("RESERVATION_SUCCESS")) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Reservation successful!");
                    clearReservationFields();
                });
            } else if (message.startsWith("RESERVATION_FAILED")) {
                String reason = message.contains("#")
                        ? message.substring(message.indexOf('#') + 1)
                        : "Not enough seats available or invalid trip.";
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Reservation failed: " + reason));
            }
        });
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }

    private void updateTripList(String tripData) {
        List<Trip> trips = parseTripsFromString(tripData);
        AttractionListCollection.getItems().setAll(trips);
        LOGGER.info("Trip list updated with {} trips", trips.size());
        filterAttractions();
    }

    private void updateFilteredAttractions(String tripData) {
        List<Trip> trips = parseTripsFromString(tripData);
        FilteredAttractions.getItems().setAll(trips);
        LOGGER.info("Filtered attractions list updated with {} trips", trips.size());
    }

    private List<Trip> parseTripsFromString(String tripData) {
        List<Trip> trips = new ArrayList<>();
        if (tripData == null || tripData.isEmpty()) {
            return trips;
        }
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

    private void setupFilteredAttractionsCellFactory() {
        FilteredAttractions.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Trip trip, boolean empty) {
                super.updateItem(trip, empty);
                if (empty || trip == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(trip.toString());
                    if (trip.getAvailableSeats() == 0) {
                        setStyle("-fx-text-fill: red;");
                    } else if (requestedTickets > 0) {
                        if (trip.getAvailableSeats() < requestedTickets) {
                            setStyle("-fx-text-fill: red;");
                        } else {
                            setStyle("-fx-text-fill: green;");
                        }
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });
    }

    private void loadAllAttractions() {
        Client.getInstance().sendMessage("GET_TRIPS");
    }

    private void filterAttractions() {
        String attraction = AttaractionField.getText().trim();
        String startTime = TimeStartField.getText().trim();
        String endTime = TimeEndField.getText().trim();

        if (!attraction.isEmpty() && (startTime.isEmpty() || endTime.isEmpty())) {
            Client.getInstance().sendMessage("SEARCH_TRIPS#" + attraction);
            return;
        }
        if (!attraction.isEmpty()) {
            Client.getInstance().sendMessage("SEARCH_TRIPS#" + attraction + "#" + startTime + "#" + endTime);
            return;
        }
        FilteredAttractions.getItems().clear();
    }

    private void handleMakeReservation() {
        Trip selectedTrip = FilteredAttractions.getSelectionModel().getSelectedItem();
        if (selectedTrip == null) {
            showAlert(Alert.AlertType.WARNING, "No trip selected!");
            return;
        }

        String customerName = CustomerNameField.getText().trim();
        String customerPhone = CustomerPhoneField.getText().trim();
        if (customerName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter customer name");
            return;
        }
        if (customerPhone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter customer phone");
            return;
        }

        int requestedTickets;
        try {
            requestedTickets = Integer.parseInt(CustomerTicketsField.getText());
            if (requestedTickets <= 0) {
                CustomerTicketsField.setStyle("-fx-border-color: red;");
                showAlert(Alert.AlertType.WARNING, "Please enter a valid number of tickets");
                return;
            }
        } catch (NumberFormatException e) {
            CustomerTicketsField.setStyle("-fx-border-color: red;");
            showAlert(Alert.AlertType.WARNING, "Please enter a valid number of tickets");
            return;
        }

        if (requestedTickets > selectedTrip.getAvailableSeats()) {
            CustomerTicketsField.setStyle("-fx-border-color: red;");
            showAlert(Alert.AlertType.WARNING, "Not enough seats available");
            return;
        } else {
            CustomerTicketsField.setStyle("-fx-border-color: green;");
        }

        Client.getInstance().sendMessage("MAKE_RESERVATION#" +
                customerName + "#" +
                customerPhone + "#" +
                selectedTrip.getId() + "#" +
                requestedTickets
        );
    }

    private void clearReservationFields() {
        CustomerNameField.clear();
        CustomerPhoneField.clear();
        CustomerTicketsField.clear();
        CustomerTicketsField.setStyle(null);
    }

    private void handleExit() {
        Stage stage = (Stage) UserExitBtn.getScene().getWindow();
        stage.close();
    }
}
