package com.busora.oops.notification;

import com.busora.model.Booking;
import com.busora.oops.abstraction.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Concrete implementation of NotificationService for Email dispatch.
 */
@Service("emailNotificationService")
public class EmailNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Override
    public void sendBookingConfirmation(Booking booking) {
        if (booking == null || booking.getUser() == null) return;
        String recipient = booking.getUser().getEmail();
        log.info("[EMAIL DISPATCH] >>> To: {} | Subject: BUSORA E-Ticket Confirmed [PNR: {}]", recipient, booking.getPnrNumber());
        log.info("[EMAIL CONTENT] Dear {}, your ticket on {} for {} is confirmed. Net Paid: ₹{}",
                booking.getUser().getFullName(),
                booking.getBus() != null ? booking.getBus().getOperatorName() : "BUSORA Express",
                booking.getJourneyDate(),
                booking.getFinalAmount());
    }

    @Override
    public void sendCancellationAlert(Booking booking) {
        if (booking == null || booking.getUser() == null) return;
        log.info("[EMAIL DISPATCH] >>> To: {} | Subject: BUSORA Booking Cancelled [PNR: {}]",
                booking.getUser().getEmail(), booking.getPnrNumber());
    }

    @Override
    public String getChannelType() {
        return "EMAIL";
    }
}
