package com.darius.project.api;
import com.darius.project.domain.*;
import com.darius.project.service.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;
    private final CustomerService customerService;
    private final TripService tripService;

    public ReservationController(ReservationService reservationService, CustomerService customerService, TripService tripService) {
        this.reservationService = reservationService;
        this.customerService = customerService;
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        LOGGER.info("Creating reservation for customer: {}, trip: {}, tickets: {}",
                request.getCustomerName(), request.getTripId(), request.getTickets());

        try {
            Trip trip = tripService.findById(request.getTripId());
            if (trip == null) {
                return ResponseEntity.badRequest().body(new ReservationResponse(false, "Trip not found", null));
            }

            if (trip.getAvailableSeats() < request.getTickets()) {
                return ResponseEntity.badRequest().body(new ReservationResponse(false, "Not enough seats available", null));
            }

            Customer customer = customerService.findByCustomerName(request.getCustomerName());
            if (customer == null) {
                int customerId = generateCustomerId();
                customer = new Customer(customerId, request.getCustomerName(), "", request.getCustomerPhone());
                customerService.save(customer);
            }

            int reservationId = generateReservationId();
            Reservation reservation = new Reservation(reservationId, request.getTripId(), customer.getId(), request.getTickets());
            reservationService.save(reservationId, reservation);
            trip.setAvailableSeats(trip.getAvailableSeats() - request.getTickets());
            tripService.update(trip.getId(), trip);

            ReservationDto dto = new ReservationDto(reservationId, request.getTripId(), customer.getId(), request.getTickets(), customer.getCustomerName(), trip.getAttractionName());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ReservationResponse(true, "Reservation created successfully", dto));

        } catch (Exception e) {
            LOGGER.error("Error creating reservation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ReservationResponse(false, "Failed to create reservation", null));
        }
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<ReservationDto>> getReservationsByTrip(@PathVariable Integer tripId) {
        try {
            List<Reservation> reservations = reservationService.findByTripId(tripId);
            List<ReservationDto> dto = reservations.stream().map(this::convertToDto).collect(Collectors.toList());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            LOGGER.error("Error fetching reservations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> cancelReservation(@PathVariable Integer id) {
        try {
            Reservation reservation = reservationService.findById(id);
            if (reservation == null) {return ResponseEntity.notFound().build();}

            Trip trip = tripService.findById(reservation.getTrip());
            if (trip != null) {
                trip.setAvailableSeats(trip.getAvailableSeats() + reservation.getNumberOfTickets());
                tripService.update(trip.getId(), trip);
            }

            reservationService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Reservation cancelled successfully"));
        } catch (Exception e) {
            LOGGER.error("Error cancelling reservation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error cancelling reservation"));
        }
    }

    private ReservationDto convertToDto(Reservation reservation) {
        Customer customer = customerService.findById(reservation.getCustomer());
        Trip trip = tripService.findById(reservation.getTrip());

        return new ReservationDto(
                reservation.getId(),
                reservation.getTrip(),
                reservation.getCustomer(),
                reservation.getNumberOfTickets(),
                customer != null ? customer.getCustomerName() : "Unknown",
                trip != null ? trip.getAttractionName() : "Unknown"
        );
    }

    private int generateReservationId() { return (int) (System.currentTimeMillis() % Integer.MAX_VALUE); }
    private int generateCustomerId() { return (int) (System.currentTimeMillis() % Integer.MAX_VALUE); }

    public static class ReservationRequest {
        private Integer tripId;
        private String customerName;
        private String customerPhone;
        private Integer tickets;

        public Integer getTripId() { return tripId; }
        public void setTripId(Integer tripId) { this.tripId = tripId; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
        public Integer getTickets() { return tickets; }
        public void setTickets(Integer tickets) { this.tickets = tickets; }
    }

    public static class ReservationResponse {
        private boolean success;
        private String message;
        private ReservationDto reservation;

        public ReservationResponse(boolean success, String message, ReservationDto reservation) {
            this.success = success;
            this.message = message;
            this.reservation = reservation;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public ReservationDto getReservation() { return reservation; }
        public void setReservation(ReservationDto reservation) { this.reservation = reservation; }
    }

    public static class ReservationDto {
        private Integer id;
        private Integer tripId;
        private Integer customerId;
        private Integer tickets;
        private String customerName;
        private String tripName;

        public ReservationDto(Integer id, Integer tripId, Integer customerId, Integer tickets, String customerName, String tripName) {
            this.id = id;
            this.tripId = tripId;
            this.customerId = customerId;
            this.tickets = tickets;
            this.customerName = customerName;
            this.tripName = tripName;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public Integer getTripId() { return tripId; }
        public void setTripId(Integer tripId) { this.tripId = tripId; }
        public Integer getCustomerId() { return customerId; }
        public void setCustomerId(Integer customerId) { this.customerId = customerId; }
        public Integer getTickets() { return tickets; }
        public void setTickets(Integer tickets) { this.tickets = tickets; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getTripName() { return tripName; }
        public void setTripName(String tripName) { this.tripName = tripName; }
    }
}