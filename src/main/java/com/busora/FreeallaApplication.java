package com.busora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * FREEALLA - Smart Bus Reservation System
 * College OOPS Mini Project Main Application Entry Point
 */
@SpringBootApplication
public class FreeallaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreeallaApplication.class, args);
        System.out.println("==================================================================");
        System.out.println(" 🚌 FREEALLA - SMART BUS RESERVATION SYSTEM STARTED SUCCESSFULLY!  ");
        System.out.println(" Web Portal:        http://localhost:8080/                       ");
        System.out.println(" Admin Dashboard:   http://localhost:8080/admin                  ");
        System.out.println(" H2 DB Console:     http://localhost:8080/h2-console             ");
        System.out.println(" Demo User Login:   user@busora.com  / user123                   ");
        System.out.println(" Demo Admin Login:  admin@busora.com / admin123                  ");
        System.out.println("==================================================================");
    }
}

