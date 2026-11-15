package com.busora.oops.notification;

import com.busora.model.Booking;
import com.busora.oops.abstraction.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Concrete implementation of NotificationService for SMS Gateway.
 */
@Service("smsNotificationService")
public class SMSNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(SMSNotificationService.class);

    @Override
    public void sendBookingConfirmation(Booking booking) {
        if (booking == null || booking.getUser() == null) return;
        String phone = booking.getUser().getPhone();
        log.info("[SMS GATEWAY] >>> Sent to {}: BUSORA: PNR {} confirmed for {}. Show this SMS at boarding. Happy Journey!",
                phone, booking.getPnrNumber(), booking.getJourneyDate());
    }

    @Override
    public void sendCancellationAlert(Booking booking) {
        if (booking == null || booking.getUser() == null) return;
        log.info("[SMS GATEWAY] >>> Sent to {}: BUSORA: Your booking {} has been cancelled. Refund initiated.",
                booking.getUser().getPhone(), booking.getPnrNumber());
    }

    @Override
    public String getChannelType() {
        return "SMS";
    }
}
