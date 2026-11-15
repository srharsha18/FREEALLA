package com.busora.repository;

import com.busora.model.Booking;
import com.busora.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByBookingDateDesc(Long userId);

    Optional<Booking> findByPnrNumber(String pnrNumber);

    List<Booking> findAllByOrderByBookingDateDesc();

    long countByStatus(BookingStatus status);

    @Query("SELECT COALESCE(SUM(b.finalAmount), 0.0) FROM Booking b WHERE b.status = 'CONFIRMED'")
    double calculateTotalRevenue();
}
