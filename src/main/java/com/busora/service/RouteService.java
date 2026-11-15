package com.busora.service;

import com.busora.exception.ResourceNotFoundException;
import com.busora.model.Route;
import com.busora.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    @Autowired
    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findByActiveTrue();
    }

    public List<String> getSourceCities() {
        return routeRepository.findDistinctSourceCities();
    }

    public List<String> getDestinationCities() {
        return routeRepository.findDistinctDestinationCities();
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Route not found with ID: " + id));
    }

    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }
}
