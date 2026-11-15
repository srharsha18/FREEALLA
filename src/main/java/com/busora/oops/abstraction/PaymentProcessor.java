package com.busora.oops.abstraction;

import com.busora.model.Booking;
import com.busora.model.Payment;

/**
 * ABSTRACTION: PaymentProcessor interface
 * Defines the contract for processing simulated bookings payments and refunds.
 */
public interface PaymentProcessor {

    /**
     * Processes transaction for a given booking.
     */
    Payment processPayment(Booking booking, String channelDetails);

    /**
     * Processes refunds on booking cancellation.
     */
    boolean refundPayment(String transactionId, double amount);

    /**
     * Identification of the payment gateway channel.
     */
    String getChannelName();
}
