package com.busora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebPageController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/search")
    public String search() {
        return "forward:/search.html";
    }

    @GetMapping("/seat-select")
    public String seatSelect() {
        return "forward:/seat-select.html";
    }

    @GetMapping("/checkout")
    public String checkout() {
        return "forward:/checkout.html";
    }

    @GetMapping("/my-bookings")
    public String myBookings() {
        return "forward:/my-bookings.html";
    }

    @GetMapping("/ticket")
    public String ticket() {
        return "forward:/ticket.html";
    }

    @GetMapping("/admin")
    public String admin() {
        return "forward:/admin.html";
    }
}
