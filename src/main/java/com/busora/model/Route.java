package com.busora.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

/**
 * Route Entity representing source, destination, distance and journey duration.
 */
@Entity
@Table(name = "routes", uniqueConstraints = {
    @UniqueConstraint(name = "uq_route", columnNames = {"source_city", "destination_city"})
})
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Source city is required")
    @Column(name = "source_city", nullable = false, length = 100)
    private String sourceCity;

    @NotBlank(message = "Destination city is required")
    @Column(name = "destination_city", nullable = false, length = 100)
    private String destinationCity;

    @Positive(message = "Distance must be greater than zero")
    @Column(name = "distance_km", nullable = false)
    private double distanceKm;

    @Positive(message = "Duration must be greater than zero")
    @Column(name = "estimated_duration_hours", nullable = false)
    private double estimatedDurationHours;

    @Column(name = "is_active")
    private boolean active = true;

    // Association: One Route has many Buses
    @JsonIgnore
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bus> buses = new ArrayList<>();

    public Route() {
    }

    public Route(String sourceCity, String destinationCity, double distanceKm, double estimatedDurationHours) {
        setSourceCity(sourceCity);
        setDestinationCity(destinationCity);
        this.distanceKm = distanceKm;
        this.estimatedDurationHours = estimatedDurationHours;
        this.active = true;
    }

    public String getRouteSummary() {
        return sourceCity + " ➔ " + destinationCity + " (" + distanceKm + " km, ~" + estimatedDurationHours + " hrs)";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceCity() {
        return sourceCity;
    }

    public void setSourceCity(String sourceCity) {
        if (sourceCity == null || sourceCity.trim().isEmpty()) {
            throw new IllegalArgumentException("Source city cannot be blank");
        }
        this.sourceCity = sourceCity.trim();
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        if (destinationCity == null || destinationCity.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination city cannot be blank");
        }
        this.destinationCity = destinationCity.trim();
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getEstimatedDurationHours() {
        return estimatedDurationHours;
    }

    public void setEstimatedDurationHours(double estimatedDurationHours) {
        this.estimatedDurationHours = estimatedDurationHours;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Bus> getBuses() {
        return buses;
    }

    public void setBuses(List<Bus> buses) {
        this.buses = buses;
    }

    @Override
    public String toString() {
        return "Route[" + sourceCity + " -> " + destinationCity + " (" + distanceKm + " km)]";
    }
}
