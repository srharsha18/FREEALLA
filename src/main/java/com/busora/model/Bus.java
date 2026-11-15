package com.busora.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base Bus Entity demonstrating:
 * 1. ENCAPSULATION: private fields, validated accessors, domain business rules.
 * 2. INHERITANCE: Root of the Bus specialization hierarchy.
 * 3. POLYMORPHISM: Virtual methods for fare calculation and category attributes.
 */
@Entity
@Table(name = "buses")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "bus_category", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("GENERIC_BUS")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bus registration number is required")
    @Column(name = "bus_number", nullable = false, unique = true, length = 30)
    private String busNumber;

    @NotBlank(message = "Operator name is required")
    @Column(name = "operator_name", nullable = false, length = 100)
    private String operatorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "bus_type", nullable = false, length = 50)
    private BusType busType;

    @Positive(message = "Total seats must be positive")
    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @Positive(message = "Base fare must be positive")
    @Column(name = "base_fare", nullable = false)
    private double baseFare;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @Column(name = "rating")
    private double rating = 4.5;

    @Column(name = "amenities", length = 255)
    private String amenities;

    @Column(name = "is_active")
    private boolean active = true;

    // Association: Route
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    // Composition: Seats belong exclusively to this bus
    @JsonIgnore
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Seat> seats = new ArrayList<>();

    // Association: Bookings made on this bus
    @JsonIgnore
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    public Bus() {
    }

    public Bus(String busNumber, String operatorName, BusType busType, int totalSeats,
               double baseFare, LocalTime departureTime, LocalTime arrivalTime,
               Route route, String amenities) {
        setBusNumber(busNumber);
        setOperatorName(operatorName);
        this.busType = busType;
        this.totalSeats = totalSeats;
        this.baseFare = baseFare;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.route = route;
        this.amenities = amenities;
        this.active = true;
    }

    // =========================================================================
    // POLYMORPHISM: Virtual methods meant to be overridden by subclasses
    // =========================================================================

    /**
     * Polymorphic fare calculation method.
     * Subclasses apply their own premium surcharge multiplier.
     */
    public double calculateDynamicFare(int seatCount) {
        double multiplier = (busType != null) ? busType.getFareMultiplier() : 1.0;
        return baseFare * seatCount * multiplier;
    }

    /**
     * Polymorphic method to obtain comfort rating and classification.
     */
    public String getComfortLevel() {
        return "Standard Comfort";
    }

    /**
     * Polymorphic badge displayed on website card.
     */
    public String getCategoryBadge() {
        return (busType != null) ? busType.getDisplayName() : "Standard";
    }

    // =========================================================================
    // POLYMORPHISM: METHOD OVERLOADING (Compile-time Polymorphism)
    // =========================================================================

    /**
     * Overloaded method 1: Calculate total fare for N seats with standard fare.
     */
    public double calculateFare(int seats) {
        return calculateDynamicFare(seats);
    }

    /**
     * Overloaded method 2: Calculate total fare with seasonal/festival surge rate.
     */
    public double calculateFare(int seats, double surgeMultiplier) {
        return calculateDynamicFare(seats) * surgeMultiplier;
    }

    /**
     * Overloaded method 3: Calculate total fare with surge multiplier and promotional discount percentage.
     */
    public double calculateFare(int seats, double surgeMultiplier, double discountPercentage) {
        double gross = calculateFare(seats, surgeMultiplier);
        double discount = (gross * discountPercentage) / 100.0;
        return Math.max(0.0, gross - discount);
    }

    // =========================================================================
    // Helper & Domain Methods
    // =========================================================================

    public int getAvailableSeatsCount() {
        if (seats == null || seats.isEmpty()) {
            return totalSeats;
        }
        int available = 0;
        for (Seat s : seats) {
            if (!s.isBooked()) {
                available++;
            }
        }
        return available;
    }

    public List<String> getAmenitiesList() {
        if (amenities == null || amenities.trim().isEmpty()) {
            return Arrays.asList("Basic Seating", "First Aid");
        }
        return Arrays.stream(amenities.split(","))
                     .map(String::trim)
                     .toList();
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
        seat.setBus(this);
    }

    // =========================================================================
    // Getters and Setters (Encapsulation)
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        if (busNumber == null || busNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Bus number cannot be blank");
        }
        this.busNumber = busNumber.trim().toUpperCase();
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        if (operatorName == null || operatorName.trim().isEmpty()) {
            throw new IllegalArgumentException("Operator name cannot be blank");
        }
        this.operatorName = operatorName.trim();
    }

    public BusType getBusType() {
        return busType;
    }

    public void setBusType(BusType busType) {
        this.busType = busType;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public String toString() {
        return "Bus[" + busNumber + " (" + operatorName + "), Type=" + busType + ", Fare=" + baseFare + "]";
    }
}
