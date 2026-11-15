package com.busora.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalTime;

/**
 * INHERITANCE: NonACBus extends Bus
 * Economical intercity transit with standard ventilation.
 */
@Entity
@DiscriminatorValue("NON_AC_BUS")
public class NonACBus extends Bus {

    public NonACBus() {
        super();
    }

    public NonACBus(String busNumber, String operatorName, BusType busType, int totalSeats,
                    double baseFare, LocalTime departureTime, LocalTime arrivalTime,
                    Route route, String amenities) {
        super(busNumber, operatorName, busType, totalSeats, baseFare, departureTime, arrivalTime, route, amenities);
    }

    @Override
    public double calculateDynamicFare(int seatCount) {
        // Flat economical pricing without AC surcharges
        return getBaseFare() * seatCount;
    }

    @Override
    public String getComfortLevel() {
        return "Budget Economical Seating";
    }

    @Override
    public String getCategoryBadge() {
        return "Non-AC Express";
    }
}
