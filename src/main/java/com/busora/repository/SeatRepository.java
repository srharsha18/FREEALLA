package com.busora.repository;

import com.busora.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByBusIdOrderBySeatNumberAsc(Long busId);

    int countByBusIdAndBookedFalse(Long busId);

    List<Seat> findByIdIn(List<Long> ids);
}
