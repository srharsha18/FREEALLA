package com.busora.service;

import com.busora.dto.BookingRequestDTO;
import com.busora.dto.BookingResponseDTO;
import com.busora.dto.PassengerDTO;
import com.busora.exception.InvalidBookingException;
import com.busora.exception.ResourceNotFoundException;
import com.busora.exception.SeatAlreadyBookedException;
import com.busora.model.*;
import com.busora.oops.abstraction.FareCalculator;
import com.busora.oops.abstraction.NotificationService;
import com.busora.oops.abstraction.PaymentProcessor;
import com.busora.repository.BookingRepository;
import com.busora.repository.BusRepository;
import com.busora.repository.SeatRepository;
import com.busora.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BusRepository busRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    // Polymorphic Abstractions Injected
    private final FareCalculator standardFareCalculator;
    private final FareCalculator luxuryFareCalculator;
    private final PaymentProcessor upiPaymentProcessor;
    private final PaymentProcessor cardPaymentProcessor;
    private final PaymentProcessor netBankingPaymentProcessor;
    private final NotificationService emailNotificationService;
    private final NotificationService smsNotificationService;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          BusRepository busRepository,
                          UserRepository userRepository,
                          SeatRepository seatRepository,
                          @Qualifier("standardFareCalculator") FareCalculator standardFareCalculator,
                          @Qualifier("luxuryFareCalculator") FareCalculator luxuryFareCalculator,
                          @Qualifier("upiPaymentProcessor") PaymentProcessor upiPaymentProcessor,
                          @Qualifier("cardPaymentProcessor") PaymentProcessor cardPaymentProcessor,
                          @Qualifier("netBankingPaymentProcessor") PaymentProcessor netBankingPaymentProcessor,
                          @Qualifier("emailNotificationService") NotificationService emailNotificationService,
                          @Qualifier("smsNotificationService") NotificationService smsNotificationService) {
        this.bookingRepository = bookingRepository;
        this.busRepository = busRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
        this.standardFareCalculator = standardFareCalculator;
        this.luxuryFareCalculator = luxuryFareCalculator;
        this.upiPaymentProcessor = upiPaymentProcessor;
        this.cardPaymentProcessor = cardPaymentProcessor;
        this.netBankingPaymentProcessor = netBankingPaymentProcessor;
        this.emailNotificationService = emailNotificationService;
        this.smsNotificationService = smsNotificationService;
    }

    // =========================================================================
    // METHOD OVERLOADING 1: createBooking using DTO
    // =========================================================================
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingResponseDTO createBooking(BookingRequestDTO req) {
        return createBooking(
            req.getBusId(),
            req.getUserId(),
            req.getJourneyDate(),
            req.getPassengers(),
            req.getCouponCode(),
            req.getPaymentMethod(),
            req.getPaymentAccountDetails()
        );
    }

    // =========================================================================
    // METHOD OVERLOADING 2: createBooking with explicit domain parameters
    // Demonstrates Transactional Concurrency Control preventing double booking
    // =========================================================================
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingResponseDTO createBooking(Long busId, Long userId, LocalDate journeyDate,
                                            List<PassengerDTO> passengerDTOs, String couponCode,
                                            String paymentMethod, String accountDetails) {

        if (passengerDTOs == null || passengerDTOs.isEmpty()) {
            throw new InvalidBookingException("No seats selected. Please select at least one seat.");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Bus bus = busRepository.findById(busId)
            .orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID: " + busId));

        // 1. Concurrency Check: Validate and lock each seat
        List<Long> seatIds = passengerDTOs.stream().map(PassengerDTO::getSeatId).collect(Collectors.toList());
        List<Seat> seatsToBook = seatRepository.findByIdIn(seatIds);

        if (seatsToBook.size() != seatIds.size()) {
            throw new InvalidBookingException("One or more selected seat IDs do not exist.");
        }

        for (Seat seat : seatsToBook) {
            if (seat.isBooked()) {
                throw new SeatAlreadyBookedException("Seat " + seat.getSeatNumber() + " is already booked! Please select another seat.");
            }
            // Reserve seat via encapsulated domain method
            seat.reserveSeat();
        }
        seatRepository.saveAll(seatsToBook);

        // 2. POLYMORPHISM: Select FareCalculator dynamically based on Bus Type
        FareCalculator calculator = (bus.getBusType() == BusType.AC_SLEEPER) 
                                    ? luxuryFareCalculator 
                                    : standardFareCalculator;

        int seatCount = passengerDTOs.size();
        double grossFare = calculator.calculateGrossFare(bus, seatCount);
        double taxes = calculator.calculateTaxes(grossFare);
        double totalBeforeDiscount = grossFare + taxes;
        double discount = calculator.calculateDiscount(grossFare, couponCode);
        double finalPayable = Math.max(0.0, totalBeforeDiscount - discount);

        // 3. Generate unique PNR
        String pnr = "FREEALLA-" + System.currentTimeMillis() % 10000000;

        // 4. Create Booking Entity (Composition with Passengers and Payment)
        Booking booking = new Booking(pnr, user, bus, journeyDate, totalBeforeDiscount, discount, finalPayable, couponCode);

        // Attach Passengers (COMPOSITION)
        Map<Long, Seat> seatMap = seatsToBook.stream().collect(Collectors.toMap(Seat::getId, s -> s));
        for (PassengerDTO pDto : passengerDTOs) {
            Seat seat = seatMap.get(pDto.getSeatId());
            Passenger passenger = new Passenger(pDto.getPassengerName(), pDto.getAge(), pDto.getGender(), seat);
            booking.addPassenger(passenger);
        }

        // 5. POLYMORPHISM: Dynamic Payment Processor Resolution
        PaymentProcessor paymentProcessor = resolvePaymentProcessor(paymentMethod);
        Payment payment = paymentProcessor.processPayment(booking, accountDetails);
        booking.setPayment(payment);

        // Persist complete aggregate
        Booking savedBooking = bookingRepository.save(booking);

        // 6. Send Notifications
        try {
            emailNotificationService.sendBookingConfirmation(savedBooking);
            smsNotificationService.sendBookingConfirmation(savedBooking);
        } catch (Exception e) {
            // Notification failure does not rollback confirmed booking
        }

        return convertToResponseDTO(savedBooking);
    }

    private PaymentProcessor resolvePaymentProcessor(String method) {
        if (method == null) return upiPaymentProcessor;
        switch (method.toUpperCase()) {
            case "CARD":
            case "CREDIT_CARD":
            case "DEBIT_CARD":
                return cardPaymentProcessor;
            case "NET_BANKING":
                return netBankingPaymentProcessor;
            case "UPI":
            default:
                return upiPaymentProcessor;
        }
    }

    @Transactional
    public BookingResponseDTO cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            User requestingUser = userRepository.findById(userId).orElse(null);
            if (requestingUser == null || requestingUser.getRole() != Role.ADMIN) {
                throw new InvalidBookingException("You are not authorized to cancel this booking.");
            }
        }

        if (!booking.isCancellable()) {
            throw new InvalidBookingException("This booking cannot be cancelled (already past journey date or cancelled).");
        }

        // Encapsulated state transition: marks status, releases seats, refunds payment
        booking.cancelBooking();
        seatRepository.saveAll(booking.getPassengers().stream().map(Passenger::getSeat).collect(Collectors.toList()));
        Booking updated = bookingRepository.save(booking);

        emailNotificationService.sendCancellationAlert(updated);
        smsNotificationService.sendCancellationAlert(updated);

        return convertToResponseDTO(updated);
    }

    public List<BookingResponseDTO> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingDateDesc(userId)
            .stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    public BookingResponseDTO getBookingByPnr(String pnr) {
        Booking booking = bookingRepository.findByPnrNumber(pnr.trim())
            .orElseThrow(() -> new ResourceNotFoundException("Booking with PNR " + pnr + " not found."));
        return convertToResponseDTO(booking);
    }

    public BookingResponseDTO convertToResponseDTO(Booking b) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(b.getId());
        dto.setPnrNumber(b.getPnrNumber());
        dto.setStatus(b.getStatus());
        dto.setJourneyDate(b.getJourneyDate());
        dto.setBookingDate(b.getBookingDate());
        dto.setTotalAmount(b.getTotalAmount());
        dto.setDiscountAmount(b.getDiscountAmount());
        dto.setFinalAmount(b.getFinalAmount());
        dto.setCouponApplied(b.getCouponApplied());

        if (b.getUser() != null) {
            dto.setUserName(b.getUser().getFullName());
            dto.setUserEmail(b.getUser().getEmail());
            dto.setUserPhone(b.getUser().getPhone());
        }

        if (b.getBus() != null) {
            dto.setBusId(b.getBus().getId());
            dto.setBusNumber(b.getBus().getBusNumber());
            dto.setOperatorName(b.getBus().getOperatorName());
            dto.setBusType(b.getBus().getBusType().getDisplayName());
            dto.setDepartureTime(b.getBus().getDepartureTime());
            dto.setArrivalTime(b.getBus().getArrivalTime());
            if (b.getBus().getRoute() != null) {
                dto.setSourceCity(b.getBus().getRoute().getSourceCity());
                dto.setDestinationCity(b.getBus().getRoute().getDestinationCity());
            }
        }

        if (b.getPayment() != null) {
            dto.setPaymentTransactionId(b.getPayment().getTransactionId());
            dto.setPaymentMethod(b.getPayment().getPaymentMethod());
        }

        if (b.getPassengers() != null) {
            List<PassengerDTO> pList = b.getPassengers().stream().map(p -> {
                PassengerDTO pDto = new PassengerDTO();
                pDto.setPassengerName(p.getPassengerName());
                pDto.setAge(p.getAge());
                pDto.setGender(p.getGender());
                if (p.getSeat() != null) {
                    pDto.setSeatId(p.getSeat().getId());
                    pDto.setSeatNumber(p.getSeat().getSeatNumber());
                }
                return pDto;
            }).collect(Collectors.toList());
            dto.setPassengers(pList);
        }

        return dto;
    }
}

