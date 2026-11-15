package com.busora.oops.polymorphism;

import com.busora.model.Booking;
import com.busora.model.Payment;
import com.busora.model.PaymentStatus;
import com.busora.oops.abstraction.PaymentProcessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * POLYMORPHISM: UPIPaymentProcessor
 * Processes UPI payments (Google Pay, PhonePe, Paytm).
 */
@Component("upiPaymentProcessor")
public class UPIPaymentProcessor implements PaymentProcessor {

    @Override
    public Payment processPayment(Booking booking, String vpaId) {
        String txnId = "UPI-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        Payment payment = new Payment(txnId, "UPI", booking.getFinalAmount(), PaymentStatus.SUCCESS);
        payment.setBooking(booking);
        return payment;
    }

    @Override
    public boolean refundPayment(String transactionId, double amount) {
        // Simulated instant reverse UPI IMPS payout
        return true;
    }

    @Override
    public String getChannelName() {
        return "Unified Payments Interface (UPI)";
    }
}
