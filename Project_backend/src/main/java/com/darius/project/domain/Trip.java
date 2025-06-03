package com.darius.project.domain;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "trips")
public class Trip implements Identifiable<Integer>{
    @Id
    private Integer id;
    private String attractionName;
    private String transportCompany;
    private String departureTime;
    private double price;
    private int availableSeats;


    public Trip() {}

    public Trip(Integer id, String attractionName, String transportCompany, String departureTime, double price, int availableSeats) {
        this.id = id;
        this.attractionName = attractionName;
        this.transportCompany = transportCompany;
        this.departureTime = departureTime;
        this.price = price;
        this.availableSeats = availableSeats;
    }


    public String getAttractionName() { return attractionName; }
    public void setAttractionName(String attractionName) { this.attractionName = attractionName; }

    public String getTransportCompany() { return transportCompany; }
    public void setTransportCompany(String transportCompany) { this.transportCompany = transportCompany; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    @Override
    public Integer getId() { return id; }

    @Override
    public void setId(Integer id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return Double.compare(price, trip.price) == 0 && availableSeats == trip.availableSeats && Objects.equals(id, trip.id) && Objects.equals(attractionName, trip.attractionName) && Objects.equals(transportCompany, trip.transportCompany) && Objects.equals(departureTime, trip.departureTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, attractionName, transportCompany, departureTime, price, availableSeats);
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Attraction: %s, Transport: %s, Departure: %s, Price: $%.2f, Seats: %d",
                id, attractionName, transportCompany, departureTime, price, availableSeats);
    }

}
