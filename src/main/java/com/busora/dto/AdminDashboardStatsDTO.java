package com.busora.dto;

import java.util.List;

public class AdminDashboardStatsDTO {

    private long totalUsers;
    private long totalBuses;
    private long totalBookings;
    private double totalRevenue;
    private long activeRoutesCount;
    private List<BookingResponseDTO> recentBookings;

    public AdminDashboardStatsDTO() {
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalBuses() {
        return totalBuses;
    }

    public void setTotalBuses(long totalBuses) {
        this.totalBuses = totalBuses;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getActiveRoutesCount() {
        return activeRoutesCount;
    }

    public void setActiveRoutesCount(long activeRoutesCount) {
        this.activeRoutesCount = activeRoutesCount;
    }

    public List<BookingResponseDTO> getRecentBookings() {
        return recentBookings;
    }

    public void setRecentBookings(List<BookingResponseDTO> recentBookings) {
        this.recentBookings = recentBookings;
    }
}
