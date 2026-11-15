-- =================================================================
-- BUSORA – SMART BUS RESERVATION SYSTEM
-- Database: MySQL 8.0+
-- Description: Complete schema with constraints, indexes & sample data
-- =================================================================

DROP DATABASE IF EXISTS busora_db;
CREATE DATABASE busora_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE busora_db;

-- 1. USERS TABLE
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. ROUTES TABLE
CREATE TABLE routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_city VARCHAR(100) NOT NULL,
    destination_city VARCHAR(100) NOT NULL,
    distance_km DOUBLE NOT NULL,
    estimated_duration_hours DOUBLE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT uq_route UNIQUE (source_city, destination_city)
);

-- 3. BUSES TABLE (OOPS Inheritance Root Table)
CREATE TABLE buses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_number VARCHAR(30) NOT NULL UNIQUE,
    operator_name VARCHAR(100) NOT NULL,
    bus_type VARCHAR(50) NOT NULL, -- AC_SEATER, AC_SLEEPER, NON_AC_SEATER, NON_AC_SLEEPER
    total_seats INT NOT NULL,
    base_fare DOUBLE NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    rating DOUBLE DEFAULT 4.5,
    amenities VARCHAR(255),
    route_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_bus_route FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
);

-- 4. SEATS TABLE
CREATE TABLE seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seat_number VARCHAR(10) NOT NULL,
    deck_level VARCHAR(10) DEFAULT 'LOWER', -- LOWER, UPPER
    seat_type VARCHAR(20) DEFAULT 'SEATER', -- SEATER, SLEEPER
    bus_id BIGINT NOT NULL,
    is_booked BOOLEAN DEFAULT FALSE,
    is_ladies_reserved BOOLEAN DEFAULT FALSE,
    CONSTRAINT uq_bus_seat UNIQUE (bus_id, seat_number),
    CONSTRAINT fk_seat_bus FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE CASCADE
);

-- 5. BOOKINGS TABLE
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pnr_number VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    bus_id BIGINT NOT NULL,
    journey_date DATE NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DOUBLE NOT NULL,
    discount_amount DOUBLE DEFAULT 0.0,
    final_amount DOUBLE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED', -- CONFIRMED, CANCELLED
    coupon_applied VARCHAR(50),
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_booking_bus FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE RESTRICT
);

-- 6. PASSENGERS TABLE (Composition: Passenger belongs to Booking)
CREATE TABLE passengers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    passenger_name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL,
    CONSTRAINT fk_passenger_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_passenger_seat FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE RESTRICT
);

-- 7. PAYMENTS TABLE (Abstraction implementation)
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    transaction_id VARCHAR(50) NOT NULL UNIQUE,
    payment_method VARCHAR(30) NOT NULL, -- UPI, CREDIT_CARD, DEBIT_CARD, NET_BANKING, WALLET
    amount DOUBLE NOT NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS', -- SUCCESS, FAILED, REFUNDED
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

-- =================================================================
-- SAMPLE SEED DATA
-- =================================================================

-- 1. Insert Users (Password is plain text for college demo simplicity: 'admin123', 'user123')
INSERT INTO users (full_name, email, password, phone, role) VALUES
('System Administrator', 'admin@busora.com', 'admin123', '+91 9876543210', 'ADMIN'),
('Rahul Sharma', 'rahul@busora.com', 'user123', '+91 9822012345', 'USER'),
('Priya Patel', 'priya@busora.com', 'user123', '+91 9833054321', 'USER');

-- 2. Insert Routes
INSERT INTO routes (source_city, destination_city, distance_km, estimated_duration_hours) VALUES
('Mumbai', 'Pune', 150.0, 3.5),
('Bangalore', 'Chennai', 350.0, 6.0),
('Delhi', 'Jaipur', 280.0, 5.0),
('Hyderabad', 'Bangalore', 570.0, 9.5),
('Pune', 'Goa', 450.0, 10.0),
('Ahmedabad', 'Mumbai', 530.0, 8.5);

-- 3. Insert Buses (Specialized types)
INSERT INTO buses (bus_number, operator_name, bus_type, total_seats, base_fare, departure_time, arrival_time, rating, amenities, route_id) VALUES
('MH-12-BZ-9090', 'IntrCity SmartBus', 'AC_SLEEPER', 28, 850.00, '06:30:00', '10:00:00', 4.8, 'WiFi, Charging Port, Blankets, Water Bottle, Tracking', 1),
('MH-14-EX-4421', 'Zingbus Electric Express', 'AC_SEATER', 32, 450.00, '08:00:00', '11:30:00', 4.6, 'WiFi, USB Charging, Reclining Seats, Water Bottle', 1),
('MH-01-PR-7822', 'Purple Travels Gold', 'NON_AC_SEATER', 36, 320.00, '14:00:00', '17:30:00', 4.1, 'Reading Light, USB Charging', 1),

('KA-01-BL-1100', 'Greenline Multi-Axle Volvo', 'AC_SLEEPER', 28, 1250.00, '21:30:00', '03:30:00', 4.9, 'AC, Bed Roll, Pillow, Live GPS, Emergency Exit, WiFi', 2),
('TN-09-CH-6543', 'KPN Travels Super Deluxe', 'AC_SEATER', 32, 750.00, '07:00:00', '13:00:00', 4.5, 'AC, Charging Port, Movie Screen, Water Bottle', 2),

('DL-01-JP-3344', 'Rajasthan State Express', 'AC_SEATER', 36, 650.00, '06:00:00', '11:00:00', 4.4, 'AC, Reclining Seats, Luggage Storage', 3),
('RJ-14-ZB-8877', 'Zingbus Royal Lounge', 'AC_SLEEPER', 28, 950.00, '22:00:00', '03:00:00', 4.8, 'Blankets, WiFi, Snacks, Live Tracking', 3),

('TS-08-HY-5511', 'Orange Travels BharatBenz', 'AC_SLEEPER', 28, 1400.00, '20:00:00', '05:30:00', 4.7, 'AC, Upper/Lower Berth, Charging, Water, Blanket', 4),
('KA-05-VR-9922', 'VRL Travels Comfort Seater', 'NON_AC_SEATER', 36, 600.00, '21:00:00', '06:30:00', 4.2, 'Comfort Seats, Emergency Assist', 4),

('MH-12-GA-3321', 'Kadamba Sleeper Coach', 'AC_SLEEPER', 28, 1100.00, '19:30:00', '05:30:00', 4.7, 'AC, Blankets, Mineral Water, Reading Lamp', 5);

-- 4. Generate Seats for Bus 1 (IntrCity SmartBus, 28 seats: Lower L1-L14, Upper U1-U14)
INSERT INTO seats (seat_number, deck_level, seat_type, bus_id, is_booked, is_ladies_reserved) VALUES
('L1', 'LOWER', 'SLEEPER', 1, FALSE, FALSE),
('L2', 'LOWER', 'SLEEPER', 1, TRUE, FALSE),
('L3', 'LOWER', 'SLEEPER', 1, FALSE, TRUE),
('L4', 'LOWER', 'SLEEPER', 1, FALSE, TRUE),
('L5', 'LOWER', 'SLEEPER', 1, FALSE, FALSE),
('L6', 'LOWER', 'SLEEPER', 1, TRUE, FALSE),
('L7', 'LOWER', 'SLEEPER', 1, FALSE, FALSE),
('L8', 'LOWER', 'SLEEPER', 1, FALSE, FALSE),
('L9', 'LOWER', 'SLEEPER', 1, FALSE, FALSE),
('L10', 'LOWER', 'SLEEPER', 1, FALSE, FALSE),
('L11', 'LOWER', 'SLEEPER', 1, FALSE, FALSE),
('L12', 'LOWER', 'SLEEPER', 1, FALSE, FALSE),
('L13', 'LOWER', 'SLEEPER', 1, FALSE, FALSE),
('L14', 'LOWER', 'SLEEPER', 1, FALSE, FALSE),
('U1', 'UPPER', 'SLEEPER', 1, FALSE, FALSE),
('U2', 'UPPER', 'SLEEPER', 1, FALSE, FALSE),
('U3', 'UPPER', 'SLEEPER', 1, FALSE, FALSE),
('U4', 'UPPER', 'SLEEPER', 1, FALSE, FALSE),
('U5', 'UPPER', 'SLEEPER', 1, FALSE, FALSE),
('U6', 'UPPER', 'SLEEPER', 1, FALSE, FALSE),
('U7', 'UPPER', 'SLEEPER', 1, FALSE, FALSE),
('U8', 'UPPER', 'SLEEPER', 1, FALSE, FALSE),
('U9', 'UPPER', 'SLEEPER', 1, FALSE, FALSE),
('U10', 'UPPER', 'SLEEPER', 1, FALSE, FALSE),
('U11', 'UPPER', 'SLEEPER', 1, FALSE, FALSE),
('U12', 'UPPER', 'SLEEPER', 1, FALSE, FALSE),
('U13', 'UPPER', 'SLEEPER', 1, FALSE, FALSE),
('U14', 'UPPER', 'SLEEPER', 1, FALSE, FALSE);

-- Generate Seats for Bus 2 (Zingbus Electric Express - 32 Seater seats S1-S32)
INSERT INTO seats (seat_number, deck_level, seat_type, bus_id, is_booked, is_ladies_reserved) VALUES
('S1', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S2', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S3', 'LOWER', 'SEATER', 2, FALSE, TRUE),
('S4', 'LOWER', 'SEATER', 2, FALSE, TRUE),
('S5', 'LOWER', 'SEATER', 2, TRUE, FALSE),
('S6', 'LOWER', 'SEATER', 2, TRUE, FALSE),
('S7', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S8', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S9', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S10', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S11', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S12', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S13', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S14', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S15', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S16', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S17', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S18', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S19', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S20', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S21', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S22', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S23', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S24', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S25', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S26', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S27', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S28', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S29', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S30', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S31', 'LOWER', 'SEATER', 2, FALSE, FALSE),
('S32', 'LOWER', 'SEATER', 2, FALSE, FALSE);

-- 5. Insert Sample Booking
INSERT INTO bookings (pnr_number, user_id, bus_id, journey_date, total_amount, discount_amount, final_amount, status, coupon_applied) VALUES
('BUSORA-20261114-8841', 2, 1, DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), 1700.00, 340.00, 1360.00, 'CONFIRMED', 'BUSORA20');

-- 6. Insert Passenger details for sample booking
INSERT INTO passengers (booking_id, seat_id, passenger_name, age, gender) VALUES
(1, 2, 'Rahul Sharma', 24, 'MALE'),
(1, 6, 'Aman Verma', 25, 'MALE');

-- 7. Insert Payment for sample booking
INSERT INTO payments (booking_id, transaction_id, payment_method, amount, payment_status) VALUES
(1, 'TXN_UPI_993412089', 'UPI', 1360.00, 'SUCCESS');
