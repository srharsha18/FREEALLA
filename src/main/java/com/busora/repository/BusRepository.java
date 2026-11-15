package com.busora.repository;

import com.busora.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    List<Bus> findByRouteIdAndActiveTrue(Long routeId);

    @Query("SELECT b FROM Bus b WHERE b.active = true AND " +
           "LOWER(b.route.sourceCity) = LOWER(:source) AND " +
           "LOWER(b.route.destinationCity) = LOWER(:destination)")
    List<Bus> searchBusesByCities(@Param("source") String source, @Param("destination") String destination);

    List<Bus> findByActiveTrue();

    boolean existsByBusNumber(String busNumber);
}
