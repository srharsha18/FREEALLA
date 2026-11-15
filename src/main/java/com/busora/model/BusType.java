package com.busora.model;

/**
 * Enumeration representing different classifications of buses.
 * Used for dynamic pricing, layout rendering, and polymorphic dispatch.
 */
public enum BusType {
    AC_SEATER("AC Seater", 1.15, "Air Conditioned push-back seats"),
    AC_SLEEPER("AC Sleeper", 1.40, "Fully air-conditioned luxury sleeper berths"),
    NON_AC_SEATER("Non-AC Seater", 1.00, "Economical 2x2 seating"),
    NON_AC_SLEEPER("Non-AC Sleeper", 1.20, "Spacious non-AC sleeper berths");

    private final String displayName;
    private final double fareMultiplier;
    private final String description;

    BusType(String displayName, double fareMultiplier, String description) {
        this.displayName = displayName;
        this.fareMultiplier = fareMultiplier;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getFareMultiplier() {
        return fareMultiplier;
    }

    public String getDescription() {
        return description;
    }
}
