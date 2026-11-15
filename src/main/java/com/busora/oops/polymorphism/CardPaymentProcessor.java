package com.busora.oops.polymorphism;

import com.busora.model.Booking;
import com.busora.model.Payment;
import com.busora.model.PaymentStatus;
import com.busora.oops.abstraction.PaymentProcessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * POLYMORPHISM: CardPaymentProcessor
 * Processes Visa/Mastercard/RuPay credit and debit cards.
 */
@Component("cardPaymentProcessor")
public class CardPaymentProcessor implements PaymentProcessor {

    @Override
    public Payment processPayment(Booking booking, String cardToken) {
        String txnId = "CARD-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        Payment payment = new Payment(txnId, "CREDIT_DEBIT_CARD", booking.getFinalAmount(), PaymentStatus.SUCCESS);
        payment.setBooking(booking);
        return payment;
    }

    @Override
    public boolean refundPayment(String transactionId, double amount) {
        // Simulated standard card refund (3-5 banking days)
        return true;
    }

    @Override
    public String getChannelName() {
        return "Credit / Debit Card Gateway";
    }
}
