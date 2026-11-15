package com.busora.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PassengerDTO {

    @NotNull(message = "Seat ID is required")
    private Long seatId;

    private String seatNumber;

    @NotBlank(message = "Passenger name is required")
    private String passengerName;

    @Min(value = 1, message = "Valid age is required")
    private int age;

    @NotBlank(message = "Gender is required")
    private String gender;

    public PassengerDTO() {
    }

    public PassengerDTO(Long seatId, String seatNumber, String passengerName, int age, String gender) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.passengerName = passengerName;
        this.age = age;
        this.gender = gender;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
