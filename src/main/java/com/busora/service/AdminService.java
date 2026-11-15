package com.busora.service;

import com.busora.dto.AdminDashboardStatsDTO;
import com.busora.dto.BookingResponseDTO;
import com.busora.dto.BusDTO;
import com.busora.exception.ResourceNotFoundException;
import com.busora.model.*;
import com.busora.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final BookingService bookingService;

    @Autowired
    public AdminService(UserRepository userRepository,
                        BusRepository busRepository,
                        RouteRepository routeRepository,
                        BookingRepository bookingRepository,
                        SeatRepository seatRepository,
                        BookingService bookingService) {
        this.userRepository = userRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.bookingService = bookingService;
    }

    public AdminDashboardStatsDTO getDashboardStats() {
        AdminDashboardStatsDTO stats = new AdminDashboardStatsDTO();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalBuses(busRepository.count());
        stats.setTotalBookings(bookingRepository.count());
        stats.setTotalRevenue(bookingRepository.calculateTotalRevenue());
        stats.setActiveRoutesCount(routeRepository.count());

        List<BookingResponseDTO> recent = bookingRepository.findAllByOrderByBookingDateDesc().stream()
            .limit(10)
            .map(bookingService::convertToResponseDTO)
            .collect(Collectors.toList());
        stats.setRecentBookings(recent);

        return stats;
    }

    @Transactional
    public Bus createBus(BusDTO dto) {
        Route route = routeRepository.findById(dto.getRouteId())
            .orElseThrow(() -> new ResourceNotFoundException("Route not found with ID: " + dto.getRouteId()));

        Bus bus;
        BusType type = (dto.getBusType() != null) ? dto.getBusType() : BusType.AC_SEATER;

        // INHERITANCE: Instantiating specialized concrete classes based on type
        switch (type) {
            case AC_SLEEPER:
                bus = new SleeperBus(dto.getBusNumber(), dto.getOperatorName(), type,
                                    dto.getTotalSeats(), dto.getBaseFare(),
                                    dto.getDepartureTime(), dto.getArrivalTime(), route,
                                    (dto.getAmenities() != null) ? String.join(", ", dto.getAmenities()) : "WiFi, Blankets");
                break;
            case NON_AC_SEATER:
            case NON_AC_SLEEPER:
                bus = new NonACBus(dto.getBusNumber(), dto.getOperatorName(), type,
                                  dto.getTotalSeats(), dto.getBaseFare(),
                                  dto.getDepartureTime(), dto.getArrivalTime(), route,
                                  (dto.getAmenities() != null) ? String.join(", ", dto.getAmenities()) : "Standard Seating");
                break;
            case AC_SEATER:
            default:
                bus = new ACBus(dto.getBusNumber(), dto.getOperatorName(), type,
                               dto.getTotalSeats(), dto.getBaseFare(),
                               dto.getDepartureTime(), dto.getArrivalTime(), route,
                               (dto.getAmenities() != null) ? String.join(", ", dto.getAmenities()) : "AC, Charging Port");
                break;
        }

        bus.setRating(dto.getRating() > 0 ? dto.getRating() : 4.5);
        Bus savedBus = busRepository.save(bus);

        // Auto-generate Seats for the newly created bus
        List<Seat> seats = new ArrayList<>();
        int total = dto.getTotalSeats();
        boolean isSleeper = type == BusType.AC_SLEEPER || type == BusType.NON_AC_SLEEPER;

        for (int i = 1; i <= total; i++) {
            String seatNum;
            String deck = "LOWER";
            String seatClassification = isSleeper ? "SLEEPER" : "SEATER";

            if (isSleeper) {
                if (i <= total / 2) {
                    seatNum = "L" + i;
                    deck = "LOWER";
                } else {
                    seatNum = "U" + (i - (total / 2));
                    deck = "UPPER";
                }
            } else {
                seatNum = "S" + i;
            }

            Seat seat = new Seat(seatNum, deck, seatClassification, savedBus);
            // Reserve couple seats for ladies demonstration
            if (i == 3 || i == 4) {
                seat.setLadiesReserved(true);
            }
            seats.add(seat);
        }
        seatRepository.saveAll(seats);

        return savedBus;
    }

    @Transactional
    public void deleteBus(Long id) {
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + id));
        busRepository.delete(bus);
    }

    @Transactional
    public Bus toggleBusStatus(Long id) {
        Bus bus = busRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + id));
        bus.setActive(!bus.isActive());
        return busRepository.save(bus);
    }

    @Transactional
    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }

    public List<Seat> getBusSeats(Long busId) {
        return seatRepository.findByBusIdOrderBySeatNumberAsc(busId);
    }

    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAllByOrderByBookingDateDesc().stream()
            .map(bookingService::convertToResponseDTO)
            .collect(Collectors.toList());
    }
}
