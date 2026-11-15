package com.busora.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class BookingRequestDTO {

    @NotNull(message = "Bus ID is required")
    private Long busId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Journey date is required")
    private LocalDate journeyDate;

    private String couponCode;

    private String paymentMethod = "UPI"; // UPI, CARD, NET_BANKING, WALLET

    private String paymentAccountDetails = "default-gateway";

    @NotEmpty(message = "At least one passenger must be provided")
    @Valid
    private List<PassengerDTO> passengers;

    public BookingRequestDTO() {
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentAccountDetails() {
        return paymentAccountDetails;
    }

    public void setPaymentAccountDetails(String paymentAccountDetails) {
        this.paymentAccountDetails = paymentAccountDetails;
    }

    public List<PassengerDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerDTO> passengers) {
        this.passengers = passengers;
    }
}
