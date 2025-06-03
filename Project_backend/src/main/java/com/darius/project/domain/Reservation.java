package com.darius.project.domain;

public class Reservation implements Identifiable<Integer> {

    private Integer id;
    private Integer trip;
    private Integer customer;
    private int numberOfTickets;

    public Reservation() {}
    public Reservation(Integer id, Integer trip, Integer customer, int numberOfTickets) {
        this.id = id;
        this.trip = trip;
        this.customer = customer;
        this.numberOfTickets = numberOfTickets;
    }

    @Override
    public Integer getId() { return id; }
    @Override
    public void setId(Integer id) { this.id = id; }

    public Integer getTrip() { return trip; }
    public void setTrip(Integer trip) { this.trip = trip; }

    public Integer getCustomer() { return customer; }
    public void setCustomer(Integer customer) { this.customer = customer; }

    public int getNumberOfTickets() { return numberOfTickets; }
    public void setNumberOfTickets(int numberOfTickets) { this.numberOfTickets = numberOfTickets; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", trip=" + trip +
                ", customer=" + customer +
                ", numberOfTickets=" + numberOfTickets +
                '}';
    }
}
