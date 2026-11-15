package com.busora.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Seat Entity demonstrating ENCAPSULATION & COMPOSITION:
 * - A Seat cannot exist independently of a Bus.
 * - Manages its own availability state safely with state transition guards.
 */
@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(name = "uq_bus_seat", columnNames = {"bus_id", "seat_number"})
})
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Seat number is required")
    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

    @Column(name = "deck_level", length = 10)
    private String deckLevel = "LOWER"; // LOWER or UPPER

    @Column(name = "seat_type", length = 20)
    private String seatType = "SEATER"; // SEATER or SLEEPER

    @Column(name = "is_booked", nullable = false)
    private boolean booked = false;

    @Column(name = "is_ladies_reserved")
    private boolean ladiesReserved = false;

    // Composition: Seat belongs to one Bus
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    @JsonIgnore
    private Bus bus;

    public Seat() {
    }

    public Seat(String seatNumber, String deckLevel, String seatType, Bus bus) {
        this.seatNumber = seatNumber;
        this.deckLevel = (deckLevel != null) ? deckLevel : "LOWER";
        this.seatType = (seatType != null) ? seatType : "SEATER";
        this.bus = bus;
        this.booked = false;
        this.ladiesReserved = false;
    }

    // =========================================================================
    // Encapsulated Business State Guards
    // =========================================================================

    public synchronized void reserveSeat() {
        if (this.booked) {
            throw new IllegalStateException("Seat " + seatNumber + " is already booked by another passenger!");
        }
        this.booked = true;
    }

    public synchronized void releaseSeat() {
        this.booked = false;
    }

    // =========================================================================
    // Getters and Setters
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getDeckLevel() {
        return deckLevel;
    }

    public void setDeckLevel(String deckLevel) {
        this.deckLevel = deckLevel;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public boolean isLadiesReserved() {
        return ladiesReserved;
    }

    public void setLadiesReserved(boolean ladiesReserved) {
        this.ladiesReserved = ladiesReserved;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    @Override
    public String toString() {
        return "Seat[" + seatNumber + " (" + deckLevel + "), booked=" + booked + "]";
    }
}
