package com.busora.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Passenger Entity demonstrating:
 * 1. ENCAPSULATION: Validated passenger details (name, age, gender).
 * 2. COMPOSITION: Life-cycle dependent on Booking.
 * 3. ASSOCIATION: Directly linked to a specific Seat.
 */
@Entity
@Table(name = "passengers")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Composition: Part of a Booking
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    private Booking booking;

    // Association: Linked to a Seat
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @NotBlank(message = "Passenger name is required")
    @Column(name = "passenger_name", nullable = false, length = 100)
    private String passengerName;

    @Min(value = 1, message = "Age must be at least 1 year")
    @Column(nullable = false)
    private int age;

    @NotBlank(message = "Gender is required")
    @Column(nullable = false, length = 10)
    private String gender; // MALE, FEMALE, OTHER

    public Passenger() {
    }

    public Passenger(String passengerName, int age, String gender, Seat seat) {
        setPassengerName(passengerName);
        setAge(age);
        setGender(gender);
        this.seat = seat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        if (passengerName == null || passengerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name cannot be empty");
        }
        this.passengerName = passengerName.trim();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 1 || age > 120) {
            throw new IllegalArgumentException("Invalid passenger age: " + age);
        }
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be blank");
        }
        this.gender = gender.trim().toUpperCase();
    }

    @Override
    public String toString() {
        return "Passenger[" + passengerName + " (" + age + " y/o, " + gender + "), Seat=" +
               (seat != null ? seat.getSeatNumber() : "N/A") + "]";
    }
}
