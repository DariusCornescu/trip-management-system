package com.darius.project.networking;
import com.darius.project.domain.*;
import com.darius.project.repository.Database.*;
import com.darius.project.repository.GenericRepos.*;
import com.darius.project.repository.ORM.*;
import com.darius.project.service.*;
import org.hibernate.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private BufferedReader in;
    private PrintWriter out;

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final TripRepository tripRepository = new HibernateTripRepository(sessionFactory);
    private final TripService tripService = new TripService(tripRepository);
    private final UserService userService = new UserService(new UserDB());
    private final ReservationService reservationService = new ReservationService(new ReservationDB());
    private final CustomerService customerService = new CustomerService(new CustomerDB());

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("❌ Error setting up streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        String request;
        try {
            while ((request = in.readLine()) != null) {
                String[] parts = request.split("#");
                String command = parts[0];
                switch (command) {
                    case "LOGIN":
                        handleLogin(parts[1], parts[2]);
                        break;
                    case "GET_TRIPS":
                        sendTrips();
                        break;
                    case "MAKE_RESERVATION":
                        makeReservation(parts[1], parts[2], parts[3], parts[4]);
                        break;
                    // User management commands
                    case "ADD_USER":
                        handleUserUpdate("ADD", parts[1]);
                        break;
                    case "UPDATE_USER":
                        handleUserUpdate("UPDATE", parts[1]);
                        break;
                    case "DELETE_USER":
                        handleUserUpdate("DELETE", parts[1]);
                        break;
                    case "GET_USERS":
                        sendUsers();
                        break;
                    // Trip management commands
                    case "ADD_TRIP":
                        handleTripUpdate("ADD", parts[1]);
                        break;
                    case "UPDATE_TRIP":
                        handleTripUpdate("UPDATE", parts[1]);
                        break;
                    case "DELETE_TRIP":
                        handleTripUpdate("DELETE", parts[1]);
                        break;
                    case "SEARCH_TRIPS":
                        searchTrips(parts[1], parts.length > 2 ? parts[2] : "", parts.length > 3 ? parts[3] : "");
                        break;
                    default:
                        out.println("UNKNOWN_COMMAND");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("🔌 Client disconnected: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void handleLogin(String username, String password) {
        boolean valid = userService.checkLogin(username, password);
        out.println(valid ? "LOGIN_SUCCESS" : "LOGIN_FAILED");
    }

    private void sendTrips() {
        StringBuilder sb = new StringBuilder();
        tripService.findAll().forEach(trip -> sb.append(formatTrip(trip)).append(";"));
        out.println("TRIPS#" + sb);
    }

    private void sendUsers() {
        StringBuilder sb = new StringBuilder();
        userService.findAll().forEach(user -> sb.append(formatUser(user)).append(";"));
        out.println("USERS#" + sb);
    }

    private String formatTrip(Trip trip) { return trip.getId() + "," + trip.getAttractionName() + "," + trip.getTransportCompany() + "," + trip.getDepartureTime() + "," + trip.getPrice() + "," + trip.getAvailableSeats(); }

    private String formatUser(User user) { return user.getId() + "," + user.getUsername() + "," + user.getPassword(); }

    private void makeReservation(String customerName, String phone, String tripIdStr, String ticketsStr) {
        try {
            int tripId = Integer.parseInt(tripIdStr);
            int tickets = Integer.parseInt(ticketsStr);
            Trip trip = tripService.findById(tripId);
            if (trip == null || trip.getAvailableSeats() < tickets) {
                out.println("RESERVATION_FAILED");
                return;
            }

            Customer customer = customerService.findByCustomerName(customerName);
            if (customer == null) {
                int newCustomerId = generateCustomerId();
                customer = new Customer(newCustomerId, customerName, "", phone);
                customerService.save(customer);
            }

            int reservationId = generateReservationId();
            Reservation reservation = new Reservation(reservationId, tripId, customer.getId(), tickets);
            reservationService.save(reservationId, reservation);

            trip.setAvailableSeats(trip.getAvailableSeats() - tickets);
            tripService.update(trip.getId(), trip);

            out.println("RESERVATION_SUCCESS");
            server.broadcastUpdate("UPDATE_TRIPS");
        } catch (NumberFormatException e) {
            out.println("RESERVATION_FAILED#Invalid number format");
            System.err.println("Error in reservation: " + e.getMessage());
        } catch (Exception e) {
            out.println("RESERVATION_FAILED#" + e.getMessage());
            System.err.println("Error in reservation: " + e.getMessage());
        }
    }

    private void handleTripUpdate(String operation, String tripData) {
        try {
            String[] fields = tripData.split(",");
            int id = Integer.parseInt(fields[0]);

            switch (operation) {
                case "ADD":
                    Trip newTrip = parseTripFromString(tripData);
                    tripService.save(id, newTrip);
                    break;
                case "UPDATE":
                    Trip updatedTrip = parseTripFromString(tripData);
                    if (fields.length > 6) {
                        int oldId = Integer.parseInt(fields[6]);
                        if (oldId != id) {
                            tripService.delete(oldId);
                        }
                    }

                    tripService.update(id, updatedTrip);
                    break;
                case "DELETE":
                    tripService.delete(id);
                    break;
            }

            out.println("TRIP_" + operation + "_SUCCESS");
            server.broadcastUpdate("UPDATE_TRIPS");
        } catch (NumberFormatException e) {
            out.println("TRIP_" + operation + "_FAILED#Invalid number format");
            System.err.println("Error in trip " + operation + ": " + e.getMessage());
        } catch (Exception e) {
            out.println("TRIP_" + operation + "_FAILED#" + e.getMessage());
            System.err.println("Error in trip " + operation + ": " + e.getMessage());
        }
    }

    private void handleUserUpdate(String operation, String userData) {
        try {
            String[] fields = userData.split(",");
            int id = Integer.parseInt(fields[0]);
            switch (operation) {
                case "ADD":
                    User newUser = parseUserFromString(userData);
                    userService.save(id, newUser);
                    break;
                case "UPDATE":
                    User updatedUser = parseUserFromString(userData);
                    if (fields.length > 3) {
                        int oldId = Integer.parseInt(fields[3]);
                        if (oldId != id) {
                            userService.delete(oldId);
                        }
                    }

                    userService.update(id, updatedUser);
                    break;
                case "DELETE":
                    userService.delete(id);
                    break;
            }

            out.println("USER_" + operation + "_SUCCESS");
            server.broadcastUpdate("UPDATE_USERS");
        } catch (NumberFormatException e) {
            out.println("USER_" + operation + "_FAILED#Invalid number format");
            System.err.println("Error in user " + operation + ": " + e.getMessage());
        } catch (Exception e) {
            out.println("USER_" + operation + "_FAILED#" + e.getMessage());
            System.err.println("Error in user " + operation + ": " + e.getMessage());
        }
    }

    private void searchTrips(String attraction, String startTime, String endTime) {
        try {
            List<Trip> trips;
            if (!startTime.isEmpty() && !endTime.isEmpty()) {
                trips = tripService.findByAttractionAndTime(attraction, startTime, endTime);
            } else if (!attraction.isEmpty()) {
                trips = tripService.findAllByAttractionContaining(attraction);
            } else {
                trips = tripService.findAll();
            }

            StringBuilder sb = new StringBuilder();
            trips.forEach(trip -> sb.append(formatTrip(trip)).append(";"));
            out.println("SEARCH_RESULTS#" + sb);
        } catch (Exception e) {
            out.println("SEARCH_FAILED#" + e.getMessage());
            System.err.println("Error in trip search: " + e.getMessage());
        }
    }

    private Trip parseTripFromString(String tripData) {
        String[] fields = tripData.split(",");
        int id = Integer.parseInt(fields[0]);
        String attraction = fields[1];
        String transport = fields[2];
        String departureTime = fields[3];
        double price = Double.parseDouble(fields[4]);
        int seats = Integer.parseInt(fields[5]);

        return new Trip(id, attraction, transport, departureTime, price, seats);
    }

    private User parseUserFromString(String userData) {
        String[] fields = userData.split(",");
        int id = Integer.parseInt(fields[0]);
        String username = fields[1];
        String password = fields[2];

        return new User(id, username, password);
    }

    private int generateReservationId() {
        int max = 0;
        List<Reservation> reservations = reservationService.findByTripId(null);
        for (Reservation r : reservations) {
            if (r.getId() > max) {
                max = r.getId();
            }
        }
        return max + 1;
    }

    private int generateCustomerId() {
        int max = 0;
        List<Customer> customers = customerService.findAll();
        for (Customer c : customers) {
            if (c.getId() > max) {
                max = c.getId();
            }
        }
        return max + 1;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void closeConnection() {
        try {
            socket.close();
            server.removeClient(this);
        } catch (IOException e) {
            System.err.println("❌ Error closing socket: " + e.getMessage());
        }
    }
}