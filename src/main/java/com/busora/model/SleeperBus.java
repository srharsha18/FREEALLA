package com.busora.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalTime;

/**
 * INHERITANCE: SleeperBus extends ACBus
 * Specializes into overnight luxury sleeper coach with bed berths (Lower & Upper Deck).
 */
@Entity
@DiscriminatorValue("SLEEPER_BUS")
public class SleeperBus extends ACBus {

    private static final double BED_LINEN_FEE = 40.0;

    public SleeperBus() {
        super();
    }

    public SleeperBus(String busNumber, String operatorName, BusType busType, int totalSeats,
                      double baseFare, LocalTime departureTime, LocalTime arrivalTime,
                      Route route, String amenities) {
        super(busNumber, operatorName, busType, totalSeats, baseFare, departureTime, arrivalTime, route, amenities);
    }

    /**
     * POLYMORPHISM - Method Overriding:
     * Sleeper buses include bed linen, privacy curtains, and berth pricing.
     */
    @Override
    public double calculateDynamicFare(int seatCount) {
        return super.calculateDynamicFare(seatCount) + (BED_LINEN_FEE * seatCount);
    }

    @Override
    public String getComfortLevel() {
        return "Ultra-Luxury Overnight Sleeper Berth";
    }

    @Override
    public String getCategoryBadge() {
        return "Royal Sleeper (1+2 Berths)";
    }
}
