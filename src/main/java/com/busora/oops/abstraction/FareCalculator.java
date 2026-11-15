package com.busora.oops.abstraction;

import com.busora.model.Bus;

/**
 * ABSTRACTION: FareCalculator interface
 * Defines the contract for dynamic pricing, GST calculation, and coupon discount evaluation.
 */
public interface FareCalculator {

    /**
     * Calculates the gross fare before discounts and taxes.
     */
    double calculateGrossFare(Bus bus, int seatCount);

    /**
     * Computes the mandatory government transport GST (e.g. 5%).
     */
    double calculateTaxes(double grossAmount);

    /**
     * Evaluates coupon codes and calculates discount amount.
     */
    double calculateDiscount(double grossAmount, String couponCode);

    /**
     * Calculates the final payable net fare: (Gross + Taxes) - Discount.
     */
    default double calculateFinalPayable(Bus bus, int seatCount, String couponCode) {
        double gross = calculateGrossFare(bus, seatCount);
        double taxes = calculateTaxes(gross);
        double discount = calculateDiscount(gross, couponCode);
        return Math.max(0.0, (gross + taxes) - discount);
    }

    /**
     * Returns the name of the fare pricing policy.
     */
    String getPolicyName();
}
