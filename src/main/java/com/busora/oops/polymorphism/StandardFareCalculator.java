package com.busora.oops.polymorphism;

import com.busora.model.Bus;
import com.busora.oops.abstraction.FareCalculator;
import org.springframework.stereotype.Component;

/**
 * POLYMORPHISM: StandardFareCalculator
 * Implements FareCalculator for standard economy and express seater buses.
 */
@Component("standardFareCalculator")
public class StandardFareCalculator implements FareCalculator {

    private static final double STANDARD_GST_RATE = 0.05; // 5% GST

    @Override
    public double calculateGrossFare(Bus bus, int seatCount) {
        if (bus == null || seatCount <= 0) {
            return 0.0;
        }
        return bus.calculateDynamicFare(seatCount);
    }

    @Override
    public double calculateTaxes(double grossAmount) {
        return Math.round((grossAmount * STANDARD_GST_RATE) * 100.0) / 100.0;
    }

    @Override
    public double calculateDiscount(double grossAmount, String couponCode) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return 0.0;
        }
        String normalizedCode = couponCode.trim().toUpperCase();
        switch (normalizedCode) {
            case "FREEALLA20":
                return Math.round((grossAmount * 0.20) * 100.0) / 100.0; // 20% discount
            case "FIRST50":
                return Math.min(grossAmount * 0.50, 200.0); // 50% discount capped at ₹200
            case "SAVE100":
                return Math.min(grossAmount, 100.0); // Flat ₹100 discount
            default:
                return 0.0;
        }
    }

    @Override
    public String getPolicyName() {
        return "Standard Intercity Economy Fare Policy";
    }
}

