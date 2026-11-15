package com.busora.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Booking Entity demonstrating:
 * 1. ENCAPSULATION: private fields, invariants, state transitions (CONFIRMED -> CANCELLED).
 * 2. COMPOSITION: Owns Passengers and Payment entities. If a booking is deleted, its passengers are deleted.
 * 3. ASSOCIATION: Associated with User and Bus.
 */
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pnr_number", nullable = false, unique = true, length = 30)
    private String pnrNumber;

    // Association: Many Bookings belong to One User
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "bookings"})
    private User user;

    // Association: Many Bookings belong to One Bus
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bus_id", nullable = false)
    @JsonIgnoreProperties({"seats", "bookings"})
    private Bus bus;

    @Column(name = "journey_date", nullable = false)
    private LocalDate journeyDate;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate = LocalDateTime.now();

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "discount_amount")
    private double discountAmount = 0.0;

    @Column(name = "final_amount", nullable = false)
    private double finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column(name = "coupon_applied", length = 50)
    private String couponApplied;

    // COMPOSITION: One Booking contains Many Passengers (Lifecycle tied to Booking)
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Passenger> passengers = new ArrayList<>();

    // COMPOSITION: One Booking has One Payment
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Payment payment;

    public Booking() {
    }

    public Booking(String pnrNumber, User user, Bus bus, LocalDate journeyDate,
                   double totalAmount, double discountAmount, double finalAmount,
                   String couponApplied) {
        this.pnrNumber = pnrNumber;
        this.user = user;
        this.bus = bus;
        this.journeyDate = journeyDate;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.couponApplied = couponApplied;
        this.status = BookingStatus.CONFIRMED;
        this.bookingDate = LocalDateTime.now();
    }

    // =========================================================================
    // Encapsulated Business Rules & State Management
    // =========================================================================

    public void addPassenger(Passenger passenger) {
        if (passenger != null) {
            passengers.add(passenger);
            passenger.setBooking(this);
        }
    }

    public void cancelBooking() {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking " + pnrNumber + " is already cancelled.");
        }
        this.status = BookingStatus.CANCELLED;
        // Release all booked seats
        if (passengers != null) {
            for (Passenger p : passengers) {
                if (p.getSeat() != null) {
                    p.getSeat().releaseSeat();
                }
            }
        }
        // Mark payment as refunded if successful
        if (payment != null && payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            payment.setPaymentStatus(PaymentStatus.REFUNDED);
        }
    }

    public boolean isCancellable() {
        return this.status == BookingStatus.CONFIRMED &&
               (journeyDate == null || !journeyDate.isBefore(LocalDate.now()));
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

    public String getPnrNumber() {
        return pnrNumber;
    }

    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getCouponApplied() {
        return couponApplied;
    }

    public void setCouponApplied(String couponApplied) {
        this.couponApplied = couponApplied;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        if (payment != null) {
            payment.setBooking(this);
        }
    }

    @Override
    public String toString() {
        return "Booking[PNR=" + pnrNumber + ", Bus=" + (bus != null ? bus.getBusNumber() : "N/A") +
               ", Date=" + journeyDate + ", Status=" + status + ", Total=₹" + finalAmount + "]";
    }
}
