package com.busora.controller;

import com.busora.dto.AdminDashboardStatsDTO;
import com.busora.dto.BookingResponseDTO;
import com.busora.dto.BusDTO;
import com.busora.model.Bus;
import com.busora.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminDashboardStatsDTO> getStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @PostMapping("/buses")
    public ResponseEntity<Bus> createBus(@RequestBody BusDTO dto) {
        Bus created = adminService.createBus(dto);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/buses/{id}")
    public ResponseEntity<Map<String, String>> deleteBus(@PathVariable Long id) {
        adminService.deleteBus(id);
        return ResponseEntity.ok(Map.of("message", "Bus with ID " + id + " has been successfully removed."));
    }

    @PutMapping("/buses/{id}/toggle-status")
    public ResponseEntity<Bus> toggleBusStatus(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleBusStatus(id));
    }

    @PostMapping("/routes")
    public ResponseEntity<com.busora.model.Route> createRoute(@RequestBody com.busora.model.Route route) {
        return ResponseEntity.ok(adminService.createRoute(route));
    }

    @GetMapping("/buses/{id}/seats")
    public ResponseEntity<List<com.busora.model.Seat>> getBusSeats(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getBusSeats(id));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(adminService.getAllBookings());
    }
}
