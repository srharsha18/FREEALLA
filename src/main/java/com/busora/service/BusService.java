package com.busora.service;

import com.busora.dto.BusDTO;
import com.busora.exception.ResourceNotFoundException;
import com.busora.model.Bus;
import com.busora.model.Seat;
import com.busora.repository.BusRepository;
import com.busora.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusService {

    private final BusRepository busRepository;
    private final SeatRepository seatRepository;

    @Autowired
    public BusService(BusRepository busRepository, SeatRepository seatRepository) {
        this.busRepository = busRepository;
        this.seatRepository = seatRepository;
    }

    public List<BusDTO> searchBuses(String fromCity, String toCity, LocalDate date,
                                    String busTypeFilter, Double maxPrice, Boolean acOnly) {
        List<Bus> buses;

        if (fromCity != null && !fromCity.trim().isEmpty() &&
            toCity != null && !toCity.trim().isEmpty()) {
            buses = busRepository.searchBusesByCities(fromCity.trim(), toCity.trim());
        } else {
            buses = busRepository.findByActiveTrue();
        }

        return buses.stream()
            .filter(b -> {
                if (busTypeFilter != null && !busTypeFilter.equalsIgnoreCase("ALL")) {
                    if (!b.getBusType().name().equalsIgnoreCase(busTypeFilter)) {
                        return false;
                    }
                }
                if (acOnly != null && acOnly) {
                    if (!b.getBusType().name().contains("AC")) {
                        return false;
                    }
                }
                if (maxPrice != null && maxPrice > 0) {
                    if (b.getBaseFare() > maxPrice) {
                        return false;
                    }
                }
                return true;
            })
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public Bus getBusById(Long id) {
        return busRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + id));
    }

    public List<Seat> getSeatsForBus(Long busId) {
        // Verify bus exists
        getBusById(busId);
        return seatRepository.findByBusIdOrderBySeatNumberAsc(busId);
    }

    public BusDTO convertToDTO(Bus bus) {
        BusDTO dto = new BusDTO();
        dto.setId(bus.getId());
        dto.setBusNumber(bus.getBusNumber());
        dto.setOperatorName(bus.getOperatorName());
        dto.setBusType(bus.getBusType());
        dto.setCategoryBadge(bus.getCategoryBadge());
        dto.setComfortLevel(bus.getComfortLevel());
        dto.setTotalSeats(bus.getTotalSeats());
        dto.setAvailableSeats(bus.getAvailableSeatsCount());
        dto.setBaseFare(bus.getBaseFare());
        dto.setDynamicFare(bus.calculateDynamicFare(1));
        dto.setDepartureTime(bus.getDepartureTime());
        dto.setArrivalTime(bus.getArrivalTime());
        dto.setRating(bus.getRating());
        dto.setAmenities(bus.getAmenitiesList());

        if (bus.getRoute() != null) {
            dto.setRouteId(bus.getRoute().getId());
            dto.setSourceCity(bus.getRoute().getSourceCity());
            dto.setDestinationCity(bus.getRoute().getDestinationCity());
            dto.setDistanceKm(bus.getRoute().getDistanceKm());
            dto.setDurationHours(bus.getRoute().getEstimatedDurationHours());
        }

        return dto;
    }
}
