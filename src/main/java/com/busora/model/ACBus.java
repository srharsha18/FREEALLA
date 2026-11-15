package com.busora.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalTime;

/**
 * INHERITANCE: ACBus extends Bus
 * Represents Air-Conditioned buses with specialized climate-control pricing and amenities.
 */
@Entity
@DiscriminatorValue("AC_BUS")
public class ACBus extends Bus {

    private static final double AC_MAINTENANCE_SURCHARGE = 50.0; // Fixed comfort surcharge

    public ACBus() {
        super();
    }

    public ACBus(String busNumber, String operatorName, BusType busType, int totalSeats,
                 double baseFare, LocalTime departureTime, LocalTime arrivalTime,
                 Route route, String amenities) {
        super(busNumber, operatorName, busType, totalSeats, baseFare, departureTime, arrivalTime, route, amenities);
    }

    /**
     * POLYMORPHISM - Method Overriding:
     * Overrides base fare calculation by including AC climate-control surcharge.
     */
    @Override
    public double calculateDynamicFare(int seatCount) {
        double standard = super.calculateDynamicFare(seatCount);
        return standard + (AC_MAINTENANCE_SURCHARGE * seatCount);
    }

    @Override
    public String getComfortLevel() {
        return "Air-Conditioned Premium Comfort";
    }

    @Override
    public String getCategoryBadge() {
        return "AC Deluxe";
    }
}
