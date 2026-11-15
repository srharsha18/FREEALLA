package com.busora.oops.polymorphism;

import com.busora.model.Booking;
import com.busora.model.Payment;
import com.busora.model.PaymentStatus;
import com.busora.oops.abstraction.PaymentProcessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * POLYMORPHISM: NetBankingPaymentProcessor
 * Processes direct bank integrations (HDFC, SBI, ICICI, Axis).
 */
@Component("netBankingPaymentProcessor")
public class NetBankingPaymentProcessor implements PaymentProcessor {

    @Override
    public Payment processPayment(Booking booking, String bankCode) {
        String txnId = "NB-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        Payment payment = new Payment(txnId, "NET_BANKING", booking.getFinalAmount(), PaymentStatus.SUCCESS);
        payment.setBooking(booking);
        return payment;
    }

    @Override
    public boolean refundPayment(String transactionId, double amount) {
        return true;
    }

    @Override
    public String getChannelName() {
        return "Direct Corporate Net Banking Gateway";
    }
}
