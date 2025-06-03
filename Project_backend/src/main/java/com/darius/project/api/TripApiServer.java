package com.darius.project.api;
import com.darius.project.domain.Trip;
import com.darius.project.repository.Database.CustomerDB;
import com.darius.project.repository.Database.ReservationDB;
import com.darius.project.repository.Database.UserDB;
import com.darius.project.repository.ORM.HibernateTripRepository;
import com.darius.project.repository.ORM.HibernateUtil;
import com.darius.project.service.CustomerService;
import com.darius.project.service.ReservationService;
import com.darius.project.service.TripService;
import com.darius.project.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Map;


@SpringBootApplication
@ComponentScan("com.darius.project")
public class TripApiServer {
    public static void main(String[] args) {
        SpringApplication.run(TripApiServer.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }
        };
    }

    @Bean
    public TripService tripService() {
        return new TripService(new HibernateTripRepository(HibernateUtil.getSessionFactory()));
    }

    @Bean
    public UserService userService() {
        return new UserService(new UserDB());
    }

    @Bean
    public ReservationService reservationService() {
        return new ReservationService(new ReservationDB());
    }

    @Bean
    public CustomerService customerService() {
        return new CustomerService(new CustomerDB());
    }

}

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/trips")
class TripController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TripController.class);
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
        LOGGER.info("TripController initialized");
    }

    // Retrieve a single Trip by ID
    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Integer id) {
        LOGGER.info("Retrieving trip with ID: {}", id);
        Trip trip = tripService.findById(id);
        if (trip == null) {
            LOGGER.info("Trip with ID: {} not found", id);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(trip, HttpStatus.OK);
    }

    // Retrieve all Trips
    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips() {
        List<Trip> trips = tripService.findAll();
        return new ResponseEntity<>(trips, HttpStatus.OK);
    }

    // Create a new Trip
    @PostMapping("/{id}")
    public ResponseEntity<Trip> createTrip(@PathVariable Integer id, @RequestBody Trip trip) {
        LOGGER.info("REST request to save Trip with id: {}", id);
        try {
            trip.setId(id);
            tripService.save(id, trip);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(trip);
        } catch (Exception e) {
            LOGGER.error("Error creating trip", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Update an existing Trip
    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable Integer id, @RequestBody Trip trip) {
        LOGGER.info("REST request to update Trip with id: {}", id);
        try {
            trip.setId(id);
            tripService.update(id, trip);
            return ResponseEntity.ok(trip);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Trip not found", e);
            return  ResponseEntity.notFound().build();
        } catch (Exception e) {
            LOGGER.error("Error updating trip", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete a Trip
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,String>> deleteTrip(@PathVariable Integer id) {
        LOGGER.info("REST request to delete Trip with id: {}", id);
        try {
            tripService.delete(id);
            Map<String,String> response = Map.of("message", "Trip deleted successfully with id: " + id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Error deleting trip", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error deleting trip with id: " + id));
        }
    }

    @GetMapping("/search/attraction")
    public ResponseEntity<List<Trip>> searchTripsByAttraction(@RequestParam String attraction) {
        LOGGER.info("REST request to find trips containing attraction: {}", attraction);
        List<Trip> trips = tripService.findAllByAttractionContaining(attraction);
        return new ResponseEntity<>(trips, HttpStatus.OK);
    }

    // Search Trips by attraction and time range
    @GetMapping("/search/time")
    public ResponseEntity<List<Trip>> searchTripsByAttractionAndTime(
            @RequestParam String attraction,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        LOGGER.info("REST request to find trips by attraction and time range");
        List<Trip> trips = tripService.findByAttractionAndTime(attraction, startTime, endTime);
        return new ResponseEntity<>(trips, HttpStatus.OK);
    }

}
