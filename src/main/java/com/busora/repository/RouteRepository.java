package com.busora.repository;

import com.busora.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findBySourceCityIgnoreCaseAndDestinationCityIgnoreCase(String sourceCity, String destinationCity);

    @Query("SELECT DISTINCT r.sourceCity FROM Route r WHERE r.active = true ORDER BY r.sourceCity")
    List<String> findDistinctSourceCities();

    @Query("SELECT DISTINCT r.destinationCity FROM Route r WHERE r.active = true ORDER BY r.destinationCity")
    List<String> findDistinctDestinationCities();

    List<Route> findByActiveTrue();
}
