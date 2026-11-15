package com.busora.oops.abstraction;

import com.busora.model.Booking;

/**
 * ABSTRACTION: NotificationService interface
 * Decouples ticket dispatch from booking business logic.
 */
public interface NotificationService {

    void sendBookingConfirmation(Booking booking);

    void sendCancellationAlert(Booking booking);

    String getChannelType();
}
