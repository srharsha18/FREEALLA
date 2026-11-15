# FREEALLA – SMART BUS RESERVATION SYSTEM
> *"Your Journey, Your Seat, Your Way."*

A complete, modern, responsive, and visually impressive online bus ticket reservation platform developed as a **College Object-Oriented Programming (OOPS) Mini Project** using Java 17, Spring Boot 3, Spring Data JPA, MySQL, and a modern responsive travel-tech frontend.

---

## 🌟 Key Features

- **RedBus & MakeMyTrip-Style UI**: Deep Navy Blue (`#0f172a`), Electric Orange (`#ff5722`), crisp rounded cards, smooth micro-animations, and full mobile/tablet responsiveness.
- **Interactive Visual Seat Layout**:
  - Color-coded seat maps: Green (Available), Electric Orange (Selected), Grey (Booked), Pink (Ladies Reserved).
  - Deck toggle (Lower Deck / Upper Deck) for Sleeper coaches.
  - Driver cabin steering wheel indicator and seat number pills.
  - Real-time gross fare, GST taxes, and net amount calculator.
- **Concurrency & Double-Booking Protection**:
  - Database unique constraints `(bus_id, seat_number)`.
  - Service-level `@Transactional(isolation = Isolation.SERIALIZABLE)` preventing race conditions.
- **Promotional Coupon Engine**:
  - `FREEALLA20` — 20% discount on total fare.
  - `FIRST50` — 50% discount up to ₹200.
  - `SAVE100` — Flat ₹100 instant discount.
- **Instant Digital E-Ticket & Boarding Pass**:
  - Unique PNR number, boarding pass aesthetics, simulated QR code, and barcode.
  - One-click browser printing (`window.print()`) with print-optimized CSS.
- **Self-Service Passenger Dashboard**:
  - View upcoming, completed, and cancelled tickets.
  - Instant ticket cancellation with automatic seat release and simulated refund.
- **Comprehensive Admin Fleet Portal**:
  - Metrics: Total users, total active buses, total reservations, gross revenue.
  - Fleet management: Add new buses (instantiating specialized subclasses), delete buses.
  - Live reservation monitor with real-time search.
- **Dual Database Out-of-the-Box**:
  - Pre-configured to run instantly with zero setup via in-memory H2 (MySQL mode).
  - Switch to local MySQL server with a single configuration flag (`application-mysql.properties`).
- **Dark Mode Support**:
  - Integrated theme switcher with persistent `localStorage` preference.

---

## 🎓 OOPS Demonstration Highlights

| OOPS Principle | Implementation in FREEALLA |
| :--- | :--- |
| **Encapsulation** | Strict private fields, validated accessors, and state guard methods in `User`, `Bus`, `Seat`, `Booking`, `Passenger`, and `Payment`. |
| **Inheritance** | Base class `Bus` specialized into `ACBus`, `SleeperBus`, `NonACBus`, and `SeaterBus` using JPA Single Table inheritance. |
| **Polymorphism (Dynamic)** | Method overriding for dynamic fare calculation, comfort rating, and category badges across bus types and `FareCalculator` implementations. |
| **Polymorphism (Static)** | Method overloading in `Bus.calculateFare()` and `BookingService.createBooking()`. |
| **Abstraction** | Interfaces decoupling implementation: `FareCalculator`, `PaymentProcessor`, and `NotificationService`. |
| **Composition** | `Bus` *-- `Seat`, `Booking` *-- `Passenger`, and `Booking` *-- `Payment`. |
| **Association** | `User` ➔ `Booking`, `Bus` ➔ `Route`. |

---

## 📁 Project Directory Structure

```text
busora/
├── pom.xml                                   # Maven dependencies & build config
├── run.bat                                   # Quick start Windows batch script
├── README.md                                 # Complete documentation & project guide
├── database/
│   └── busora_mysql_schema.sql               # Production MySQL DDL & seed data
├── docs/
│   ├── UML_CLASS_DIAGRAM.md                  # Mermaid UML class diagram
│   ├── USE_CASE_DIAGRAM.md                   # Mermaid use case diagram
│   ├── ER_DIAGRAM.md                         # Mermaid ER entity-relationship diagram
│   └── OOPS_EXPLANATION.md                   # Detailed viva voce explanation guide
└── src/
    └── main/
        ├── java/com/busora/
        │   ├── FreeallaApplication.java        # Spring Boot entry point
        │   ├── config/
        │   │   └── DataInitializer.java      # Startup auto-seed runner
        │   ├── controller/
        │   │   ├── AuthController.java       # User authentication API
        │   │   ├── BusController.java        # Bus search & seat layout API
        │   │   ├── RouteController.java      # Intercity corridors API
        │   │   ├── BookingController.java    # Reservation & cancellation API
        │   │   ├── AdminController.java      # Fleet & revenue analytics API
        │   │   └── WebPageController.java    # Spring MVC view router
        │   ├── dto/                          # Clean Data Transfer Objects
        │   ├── exception/
        │   │   └── GlobalExceptionHandler.java # Centralized error handling
        │   ├── model/                        # Domain entities (OOPS model)
        │   │   ├── User.java, Route.java
        │   │   ├── Bus.java, ACBus.java, SleeperBus.java, NonACBus.java, SeaterBus.java
        │   │   ├── Seat.java, Booking.java, Passenger.java, Payment.java
        │   │   └── Enums (Role, BusType, SeatStatus, BookingStatus, PaymentStatus)
        │   ├── oops/
        │   │   ├── abstraction/              # FareCalculator, PaymentProcessor, NotificationService
        │   │   ├── polymorphism/             # StandardFare, LuxuryFare, UPI, Card, NetBanking
        │   │   └── notification/             # Email & SMS notification dispatchers
        │   ├── repository/                   # Spring Data JPA repositories
        │   └── service/                      # Transactional business logic
        └── resources/
            ├── application.properties        # Default configuration (H2 out-of-the-box)
            ├── application-mysql.properties  # MySQL production profile
            └── static/                       # Modern Frontend
                ├── css/styles.css            # Custom CSS & Boarding Pass print styles
                ├── js/app.js                 # Global session & theme manager
                ├── index.html                # Hero landing & search page
                ├── search.html               # Filterable bus search results
                ├── seat-select.html          # Interactive visual seat selection
                ├── checkout.html             # Passenger details & payment simulation
                ├── ticket.html               # Digital E-Ticket & QR boarding pass
                ├── my-bookings.html          # Passenger booking history dashboard
                └── admin.html                # Administrator fleet & revenue dashboard
```

---

## 🚀 Execution Instructions

### Prerequisites
- **Java**: JDK 17 or higher
- **Maven**: Version 3.8+ (or open project directly in IDE)
- **Database**: MySQL 8.0+ (Optional: application runs with zero configuration on in-memory H2 if MySQL is not running)

### Option 1: Run with Out-of-the-Box In-Memory Database (Zero Setup)
Open your terminal in the project directory (`busora/`):
```bash
mvn spring-boot:run
```
Once started, access the application in your browser:
- **Web Portal**: [http://localhost:8080/](http://localhost:8080/)
- **Admin Dashboard**: [http://localhost:8080/admin](http://localhost:8080/admin)
- **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:busora_db`, Username: `sa`, Password: empty)

### Option 2: Run with Local MySQL Database
1. Open MySQL Workbench or MySQL CLI:
   ```bash
   mysql -u root -p < database/busora_mysql_schema.sql
   ```
2. Verify credentials in `src/main/resources/application-mysql.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/busora_db
   spring.datasource.username=root
   spring.datasource.password=root123
   ```
3. Run with the MySQL profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

### Option 3: Run via IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Open IntelliJ IDEA / VS Code.
2. Select **File ➔ Open** and choose the `busora` folder.
3. Allow the IDE to import Maven dependencies from `pom.xml`.
4. Run `com.busora.FreeallaApplication.java`.

---

## 🔑 Demo Login Credentials

The system includes pre-seeded demo accounts with one-click autofill in the login modal:

| Role | Email | Password | Access Privileges |
| :--- | :--- | :--- | :--- |
| **Passenger (User)** | `rahul@busora.com` | `user123` | Book tickets, select seats, cancel bookings, view history. |
| **Administrator** | `admin@busora.com` | `admin123` | Full fleet management, add/delete buses, view all reservations & revenue. |
| **Passenger 2** | `priya@busora.com` | `user123` | Additional demo passenger account. |

---

## 📑 College Mini-Project Report Outline

Use this recommended structure for your college academic mini-project documentation:

1. **Title Page**: FREEALLA – Smart Bus Reservation System
2. **Certificate & Declaration**
3. **Acknowledgement & Abstract**
4. **Chapter 1: Introduction**
   - 1.1 Problem Statement & Background
   - 1.2 Objectives & Scope
   - 1.3 Target Audience
5. **Chapter 2: Literature Survey & Existing Systems**
   - 2.1 Comparison with existing portals (RedBus, AbhiBus, IRCTC)
   - 2.2 Shortcomings of legacy paper-based/counter booking
6. **Chapter 3: System Requirements & Specifications**
   - 3.1 Hardware Requirements
   - 3.2 Software Requirements (Java 17, Spring Boot 3, MySQL 8, HTML5, CSS3, JS)
7. **Chapter 4: Object-Oriented Software Architecture**
   - 4.1 Encapsulation implementation with code evidence
   - 4.2 Inheritance bus hierarchy (`Bus` ➔ `ACBus` ➔ `SleeperBus`)
   - 4.3 Polymorphism (Dynamic fare overrides & Static method overloading)
   - 4.4 Abstraction through interfaces (`PaymentProcessor`, `FareCalculator`, `NotificationService`)
   - 4.5 Association & Composition models
8. **Chapter 5: System Design & Diagrams**
   - 5.1 Use Case Diagram (refer to `docs/USE_CASE_DIAGRAM.md`)
   - 5.2 UML Class Diagram (refer to `docs/UML_CLASS_DIAGRAM.md`)
   - 5.3 Entity-Relationship (ER) Diagram (refer to `docs/ER_DIAGRAM.md`)
   - 5.4 Sequence Flow Diagrams (Search ➔ Seat Select ➔ Checkout ➔ Ticket)
9. **Chapter 6: Implementation & Key Code Snippets**
   - 6.1 Concurrency control and double-booking prevention (`@Transactional`)
   - 6.2 Interactive seat-selection grid logic
   - 6.3 Dynamic coupon discount calculations
10. **Chapter 7: Testing & Verification**
    - 7.1 Unit Testing (Fare calculators and validation rules)
    - 7.2 Integration Testing (Booking lifecycle and seat release on cancellation)
11. **Chapter 8: Conclusion & Future Enhancements**
    - 8.1 Real-time GPS vehicle tracking integration
    - 8.2 Real SMS/WhatsApp notification gateway integration
12. **References & Bibliography**

