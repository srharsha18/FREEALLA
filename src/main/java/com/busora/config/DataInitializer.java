package com.busora.config;

import com.busora.model.*;
import com.busora.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Automatically initializes seed data on application startup if database is empty.
 * Guarantees zero-friction demonstration during college viva/project presentations.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final PaymentRepository paymentRepository;

    public DataInitializer(UserRepository userRepository,
                           RouteRepository routeRepository,
                           BusRepository busRepository,
                           SeatRepository seatRepository,
                           BookingRepository bookingRepository,
                           PassengerRepository passengerRepository,
                           PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.busRepository = busRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("FREEALLA database already initialized with {} users. Skipping seed.", userRepository.count());
            return;
        }

        log.info("Initializing FREEALLA database with sample data...");

        // 1. Create Default Users
        User admin = new User("System Administrator", "admin@busora.com", "admin123", "+91 9876543210", Role.ADMIN);
        User rahul = new User("Rahul Sharma", "rahul@busora.com", "user123", "+91 9822012345", Role.USER);
        User priya = new User("Priya Patel", "priya@busora.com", "user123", "+91 9833054321", Role.USER);

        userRepository.saveAll(List.of(admin, rahul, priya));

        // 2. Create Popular Routes
        Route r1 = new Route("Mumbai", "Pune", 150.0, 3.5);
        Route r2 = new Route("Bangalore", "Chennai", 350.0, 6.0);
        Route r3 = new Route("Delhi", "Jaipur", 280.0, 5.0);
        Route r4 = new Route("Hyderabad", "Bangalore", 570.0, 9.5);
        Route r5 = new Route("Pune", "Goa", 450.0, 10.0);
        Route r6 = new Route("Ahmedabad", "Mumbai", 530.0, 8.5);

        routeRepository.saveAll(List.of(r1, r2, r3, r4, r5, r6));

        // 3. Create Specialized Buses using OOPS Subclasses
        List<Bus> buses = new ArrayList<>();

        // Bus 1: IntrCity SmartBus (AC Sleeper on Mumbai - Pune)
        SleeperBus b1 = new SleeperBus("MH-12-BZ-9090", "IntrCity SmartBus", BusType.AC_SLEEPER, 28,
                750.00, LocalTime.of(6, 30), LocalTime.of(10, 0), r1,
                "WiFi, USB Charging, Clean Blankets, Mineral Water, GPS Tracking");
        b1.setRating(4.8);
        buses.add(b1);

        // Bus 2: Zingbus Electric Express (AC Seater on Mumbai - Pune)
        ACBus b2 = new ACBus("MH-14-EX-4421", "Zingbus Electric Express", BusType.AC_SEATER, 32,
                450.00, LocalTime.of(8, 0), LocalTime.of(11, 30), r1,
                "WiFi, Pushback Seats, USB Charging, CCTV, Punctuality Guarantee");
        b2.setRating(4.6);
        buses.add(b2);

        // Bus 3: Purple Travels Gold (Non-AC Seater on Mumbai - Pune)
        NonACBus b3 = new NonACBus("MH-01-PR-7822", "Purple Travels Gold", BusType.NON_AC_SEATER, 36,
                320.00, LocalTime.of(14, 0), LocalTime.of(17, 30), r1,
                "Reading Light, Luggage Storage, First Aid");
        b3.setRating(4.2);
        buses.add(b3);

        // Bus 4: Greenline Multi-Axle Volvo (AC Sleeper on Bangalore - Chennai)
        SleeperBus b4 = new SleeperBus("KA-01-BL-1100", "Greenline Multi-Axle Volvo", BusType.AC_SLEEPER, 28,
                1150.00, LocalTime.of(21, 30), LocalTime.of(3, 30), r2,
                "AC, Bed Roll, Pillow, Live GPS, Emergency Exit, WiFi");
        b4.setRating(4.9);
        buses.add(b4);

        // Bus 5: KPN Travels Super Deluxe (AC Seater on Bangalore - Chennai)
        ACBus b5 = new ACBus("TN-09-CH-6543", "KPN Travels Super Deluxe", BusType.AC_SEATER, 32,
                680.00, LocalTime.of(7, 0), LocalTime.of(13, 0), r2,
                "Air Conditioned, Charging Port, Movie Screen, Water Bottle");
        b5.setRating(4.5);
        buses.add(b5);

        // Bus 6: Rajasthan State Express (AC Seater on Delhi - Jaipur)
        ACBus b6 = new ACBus("DL-01-JP-3344", "Rajasthan State Express", BusType.AC_SEATER, 36,
                550.00, LocalTime.of(6, 0), LocalTime.of(11, 0), r3,
                "AC, Comfortable Reclining Seats, Luggage Hold");
        b6.setRating(4.4);
        buses.add(b6);

        // Bus 7: Zingbus Royal Lounge (AC Sleeper on Delhi - Jaipur)
        SleeperBus b7 = new SleeperBus("RJ-14-ZB-8877", "Zingbus Royal Lounge", BusType.AC_SLEEPER, 28,
                890.00, LocalTime.of(22, 0), LocalTime.of(3, 0), r3,
                "Premium Bedding, Snacks, WiFi, Live Tracking");
        b7.setRating(4.8);
        buses.add(b7);

        // Bus 8: Orange Travels BharatBenz (AC Sleeper on Hyderabad - Bangalore)
        SleeperBus b8 = new SleeperBus("TS-08-HY-5511", "Orange Travels BharatBenz", BusType.AC_SLEEPER, 28,
                1350.00, LocalTime.of(20, 0), LocalTime.of(5, 30), r4,
                "Upper/Lower Berth, Charging, Water, Blanket, Reading Light");
        b8.setRating(4.7);
        buses.add(b8);

        // Bus 9: Kadamba Sleeper Coach (AC Sleeper on Pune - Goa)
        SleeperBus b9 = new SleeperBus("MH-12-GA-3321", "Kadamba Sleeper Coach", BusType.AC_SLEEPER, 28,
                1100.00, LocalTime.of(19, 30), LocalTime.of(5, 30), r5,
                "AC, Blankets, Mineral Water, Reading Lamp");
        b9.setRating(4.7);
        buses.add(b9);

        busRepository.saveAll(buses);

        // 4. Generate Seats for each bus
        for (Bus bus : buses) {
            List<Seat> seats = new ArrayList<>();
            int total = bus.getTotalSeats();
            boolean isSleeper = bus.getBusType() == BusType.AC_SLEEPER || bus.getBusType() == BusType.NON_AC_SLEEPER;

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

                Seat s = new Seat(seatNum, deck, seatClassification, bus);
                if (i == 3 || i == 4) {
                    s.setLadiesReserved(true);
                }
                // Pre-book couple seats for realistic visual representation
                if (i == 5 || i == 6) {
                    s.setBooked(true);
                }
                seats.add(s);
            }
            seatRepository.saveAll(seats);
        }

        // 5. Create Initial Sample Booking for Rahul Sharma
        Bus demoBus = buses.get(0);
        Booking demoBooking = new Booking(
            "FREEALLA-20261114-8841",
            rahul,
            demoBus,
            LocalDate.now().plusDays(2),
            1680.00,
            336.00,
            1344.00,
            "FREEALLA20"
        );

        List<Seat> bus1Seats = seatRepository.findByBusIdOrderBySeatNumberAsc(demoBus.getId());
        Seat bookedSeat1 = bus1Seats.get(4); // L5
        Seat bookedSeat2 = bus1Seats.get(5); // L6
        bookedSeat1.setBooked(true);
        bookedSeat2.setBooked(true);

        Passenger p1 = new Passenger("Rahul Sharma", 24, "MALE", bookedSeat1);
        Passenger p2 = new Passenger("Aman Verma", 25, "MALE", bookedSeat2);
        demoBooking.addPassenger(p1);
        demoBooking.addPassenger(p2);

        Payment payment = new Payment("TXN-UPI-993412089", "UPI", 1344.00, PaymentStatus.SUCCESS);
        demoBooking.setPayment(payment);

        bookingRepository.save(demoBooking);

        log.info("FREEALLA sample data successfully initialized! Ready for testing.");
    }
}

