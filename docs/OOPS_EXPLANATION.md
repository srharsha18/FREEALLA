# BUSORA – Object-Oriented Programming (OOPS) Implementation Guide

> **Prepared for College Mini-Project Defense & Viva Voce**

This document explains exactly how each core Object-Oriented Programming principle is implemented in **BUSORA**, with code references and explanation points for presentation to examiners.

---

## 1. Encapsulation

### Definition
Encapsulation is the bundling of data attributes and the methods that operate on that data into a single unit (class), while restricting direct access to internal state through private fields and validated public accessors.

### Implementation in BUSORA

1. **Information Hiding**: All domain entity attributes in `User`, `Bus`, `Seat`, `Booking`, `Passenger`, and `Payment` are strictly declared `private`.
2. **Data Integrity & Validation**: Setters protect against invalid states rather than blindly assigning values.

```java
// File: src/main/java/com/busora/model/User.java
public class User {
    private String email;
    private String password;

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.trim().toLowerCase();
    }
}
```

3. **Domain Business Invariants**: State changes are encapsulated inside entity methods. For example, reserving or releasing a seat enforces state guards:

```java
// File: src/main/java/com/busora/model/Seat.java
public synchronized void reserveSeat() {
    if (this.booked) {
        throw new IllegalStateException("Seat " + seatNumber + " is already booked!");
    }
    this.booked = true;
}
```

---

## 2. Inheritance

### Definition
Inheritance allows specialized child classes to inherit attributes and behaviors from a common base class, promoting code reuse and establishing an **"is-a"** relationship.

### Implementation in BUSORA

We designed a clean Bus classification hierarchy:
- `Bus` (Base entity containing universal attributes: `busNumber`, `operatorName`, `baseFare`, `departureTime`, `route`, `seats`)
  - `ACBus` (`extends Bus`) — Adds air conditioning surcharges and climate-control amenities.
    - `SleeperBus` (`extends ACBus`) — Specializes into an overnight luxury coach with upper and lower sleeping berths and bed linen fees.
  - `NonACBus` (`extends Bus`) — Budget economical transit without AC overheads.
  - `SeaterBus` (`extends Bus`) — Ergonomic 2x2 pushback seating.

```java
// File: src/main/java/com/busora/model/ACBus.java
@Entity
@DiscriminatorValue("AC_BUS")
public class ACBus extends Bus {
    private static final double AC_MAINTENANCE_SURCHARGE = 50.0;

    public ACBus(String busNumber, String operatorName, BusType busType, int totalSeats,
                 double baseFare, LocalTime departureTime, LocalTime arrivalTime,
                 Route route, String amenities) {
        super(busNumber, operatorName, busType, totalSeats, baseFare, departureTime, arrivalTime, route, amenities);
    }
    ...
}
```

---

## 3. Polymorphism

### A. Dynamic (Runtime) Polymorphism – Method Overriding

Runtime polymorphism allows a subclass to provide a specific implementation of a method already defined in its superclass or an interface.

#### 1. Dynamic Fare Calculation
Each bus subclass overrides the `calculateDynamicFare(int seatCount)` method to apply specialized multipliers and surcharges:

```java
// In Bus.java (Base):
public double calculateDynamicFare(int seatCount) {
    return baseFare * seatCount * busType.getFareMultiplier();
}

// In ACBus.java (Override):
@Override
public double calculateDynamicFare(int seatCount) {
    double standard = super.calculateDynamicFare(seatCount);
    return standard + (AC_MAINTENANCE_SURCHARGE * seatCount);
}

// In SleeperBus.java (Override):
@Override
public double calculateDynamicFare(int seatCount) {
    return super.calculateDynamicFare(seatCount) + (BED_LINEN_FEE * seatCount);
}

// In NonACBus.java (Override):
@Override
public double calculateDynamicFare(int seatCount) {
    return getBaseFare() * seatCount;
}
```

#### 2. Polymorphic Display Badges
- `ACBus.getCategoryBadge()` returns `"AC Deluxe"`
- `SleeperBus.getCategoryBadge()` returns `"Royal Sleeper (1+2 Berths)"`
- `NonACBus.getCategoryBadge()` returns `"Non-AC Express"`

### B. Static (Compile-time) Polymorphism – Method Overloading

Compile-time polymorphism occurs when multiple methods in the same class share the same name with different parameter signatures.

#### Implementation in `Bus.java`:
```java
// Overload 1: Base calculation
public double calculateFare(int seats) {
    return calculateDynamicFare(seats);
}

// Overload 2: Base calculation with festival surge multiplier
public double calculateFare(int seats, double surgeMultiplier) {
    return calculateDynamicFare(seats) * surgeMultiplier;
}

// Overload 3: Base calculation with surge and promotional discount
public double calculateFare(int seats, double surgeMultiplier, double discountPercentage) {
    double gross = calculateFare(seats, surgeMultiplier);
    double discount = (gross * discountPercentage) / 100.0;
    return Math.max(0.0, gross - discount);
}
```

#### Implementation in `BookingService.java`:
```java
// Overload 1: Create booking from web DTO
public BookingResponseDTO createBooking(BookingRequestDTO req);

// Overload 2: Create booking with explicit domain parameters
public BookingResponseDTO createBooking(Long busId, Long userId, LocalDate journeyDate,
                                        List<PassengerDTO> passengerDTOs, String couponCode,
                                        String paymentMethod, String accountDetails);
```

---

## 4. Abstraction

### Definition
Abstraction hides low-level implementation complexities and exposes only essential contracts through interfaces or abstract classes.

### Interfaces in BUSORA

1. **`FareCalculator`** (`com.busora.oops.abstraction.FareCalculator`):
   - Contract for calculating gross fares, calculating GST/taxes, and evaluating promotional coupons (`BUSORA20`, `FIRST50`, `SAVE100`).
   - Implementations: `StandardFareCalculator`, `LuxuryFareCalculator`.

2. **`PaymentProcessor`** (`com.busora.oops.abstraction.PaymentProcessor`):
   - Decouples booking logic from payment gateways.
   - Implementations:
     - `UPIPaymentProcessor` (Virtual Payment Address simulation)
     - `CardPaymentProcessor` (Credit/Debit Card token simulation)
     - `NetBankingPaymentProcessor` (Corporate banking simulation)

3. **`NotificationService`** (`com.busora.oops.abstraction.NotificationService`):
   - Decouples communication channels from business events.
   - Implementations: `EmailNotificationService`, `SMSNotificationService`.

---

## 5. Association, Aggregation, and Composition

| Relationship Type | Classes | Justification |
| :--- | :--- | :--- |
| **Association** | `User` ➔ `Booking` | A user places bookings. If a user is deleted, historical booking records can persist for accounting. |
| **Association** | `Bus` ➔ `Route` | A bus is assigned to a route. Both entities have independent lifecycles. |
| **Composition** | `Bus` *-- `Seat` | A seat cannot exist without a bus. When a bus is decommissioned, its seats cease to exist (`orphanRemoval = true`). |
| **Composition** | `Booking` *-- `Passenger` | A passenger record in this platform belongs strictly to an active booking reservation. If a booking is deleted, passenger records are cascade deleted. |
| **Composition** | `Booking` *-- `Payment` | Exactly one financial payment ledger record belongs to a booking (`@OneToOne(cascade = CascadeType.ALL)`). |

---

## 6. Examiner Viva Q&A Preparation

**Q1: Why did you choose inheritance for the `Bus` class instead of just adding a string column?**  
*Answer:* In real-world travel systems, different bus types exhibit distinct behavioral rules—such as different fare algorithms (AC maintenance fees, sleeper berth linen surcharges), different deck capabilities (Lower & Upper berth), and different amenity profiles. Inheritance allows us to use polymorphic dispatch to calculate pricing dynamically without cluttering the service with complex `if-else` cascades.

**Q2: How does BUSORA prevent two passengers from booking the exact same seat simultaneously?**  
*Answer:* We enforce double-booking prevention at two layers:
1. **Database Layer**: A unique constraint `UNIQUE (bus_id, seat_number)` in the `seats` table ensures no duplicate seat entity can exist.
2. **Service Layer**: In `BookingService.createBooking()`, we use `@Transactional(isolation = Isolation.SERIALIZABLE)` and `synchronized` seat reservation state guards (`seat.reserveSeat()`). If a seat is already booked, a custom `SeatAlreadyBookedException` is thrown before any booking record is generated.

**Q3: Where is Abstraction used in your payment flow?**  
*Answer:* We defined the `PaymentProcessor` interface. The `BookingService` does not know whether the user pays via UPI, Credit Card, or Net Banking; it simply invokes `paymentProcessor.processPayment()`. If tomorrow we want to add PayPal or Apple Pay, we simply create a new class implementing `PaymentProcessor` without modifying the core booking engine (demonstrating the Open/Closed Principle of SOLID).
