package com.busora.oops.polymorphism;

import com.busora.model.Bus;
import com.busora.oops.abstraction.FareCalculator;
import org.springframework.stereotype.Component;

/**
 * POLYMORPHISM: LuxuryFareCalculator
 * Implements luxury tier calculation with 8% luxury travel GST and exclusive offers.
 */
@Component("luxuryFareCalculator")
public class LuxuryFareCalculator implements FareCalculator {

    private static final double LUXURY_GST_RATE = 0.08;

    @Override
    public double calculateGrossFare(Bus bus, int seatCount) {
        if (bus == null || seatCount <= 0) {
            return 0.0;
        }
        // Luxury tier applies an additional 10% premium hospitality markup
        return (bus.calculateDynamicFare(seatCount) * 1.10);
    }

    @Override
    public double calculateTaxes(double grossAmount) {
        return Math.round((grossAmount * LUXURY_GST_RATE) * 100.0) / 100.0;
    }

    @Override
    public double calculateDiscount(double grossAmount, String couponCode) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return 0.0;
        }
        String normalizedCode = couponCode.trim().toUpperCase();
        if ("FREEALLA20".equals(normalizedCode)) {
            return Math.round((grossAmount * 0.20) * 100.0) / 100.0;
        } else if ("LUXURY25".equals(normalizedCode)) {
            return Math.round((grossAmount * 0.25) * 100.0) / 100.0;
        } else if ("SAVE100".equals(normalizedCode)) {
            return 100.0;
        }
        return 0.0;
    }

    @Override
    public String getPolicyName() {
        return "Premium Multi-Axle Luxury Hospitality Policy";
    }
}

