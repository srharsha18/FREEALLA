package com.busora.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Payment Entity demonstrating ENCAPSULATION & 1-to-1 COMPOSITION with Booking.
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Composition: Exactly one payment per booking
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    @JsonIgnore
    private Booking booking;

    @Column(name = "transaction_id", nullable = false, unique = true, length = 60)
    private String transactionId;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod; // UPI, CARD, NET_BANKING, WALLET

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus = PaymentStatus.SUCCESS;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();

    public Payment() {
    }

    public Payment(String transactionId, String paymentMethod, double amount, PaymentStatus paymentStatus) {
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentStatus = (paymentStatus != null) ? paymentStatus : PaymentStatus.SUCCESS;
        this.paymentDate = LocalDateTime.now();
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString() {
        return "Payment[Txn=" + transactionId + ", Method=" + paymentMethod + ", ₹" + amount + ", Status=" + paymentStatus + "]";
    }
}
