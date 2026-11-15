package com.busora.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalTime;

/**
 * INHERITANCE: SeaterBus extends Bus
 * Push-back 2x2 ergonomic seating layout.
 */
@Entity
@DiscriminatorValue("SEATER_BUS")
public class SeaterBus extends Bus {

    public SeaterBus() {
        super();
    }

    public SeaterBus(String busNumber, String operatorName, BusType busType, int totalSeats,
                     double baseFare, LocalTime departureTime, LocalTime arrivalTime,
                     Route route, String amenities) {
        super(busNumber, operatorName, busType, totalSeats, baseFare, departureTime, arrivalTime, route, amenities);
    }

    @Override
    public double calculateDynamicFare(int seatCount) {
        return getBaseFare() * seatCount * 1.10;
    }

    @Override
    public String getComfortLevel() {
        return "Ergonomic 2x2 Pushback Seater";
    }

    @Override
    public String getCategoryBadge() {
        return "Executive Seater";
    }
}
