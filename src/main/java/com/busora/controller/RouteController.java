package com.busora.controller;

import com.busora.model.Route;
import com.busora.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "*")
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/cities/source")
    public ResponseEntity<List<String>> getSourceCities() {
        return ResponseEntity.ok(routeService.getSourceCities());
    }

    @GetMapping("/cities/destination")
    public ResponseEntity<List<String>> getDestinationCities() {
        return ResponseEntity.ok(routeService.getDestinationCities());
    }
}
