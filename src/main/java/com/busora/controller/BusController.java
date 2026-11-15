package com.busora.controller;

import com.busora.dto.BusDTO;
import com.busora.model.Seat;
import com.busora.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/buses")
@CrossOrigin(origins = "*")
public class BusController {

    private final BusService busService;

    @Autowired
    public BusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<BusDTO>> searchBuses(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "ALL") String type,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean acOnly) {

        List<BusDTO> buses = busService.searchBuses(from, to, date, type, maxPrice, acOnly);
        return ResponseEntity.ok(buses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusDTO> getBusById(@PathVariable Long id) {
        return ResponseEntity.ok(busService.convertToDTO(busService.getBusById(id)));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<Seat>> getBusSeats(@PathVariable Long id) {
        return ResponseEntity.ok(busService.getSeatsForBus(id));
    }
}
