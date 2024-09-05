package org.sergp.bookingservice.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sergp.bookingservice.exceptions.BookingNotFoundException;
import org.sergp.bookingservice.kafka.PaymentConfirmationEvent;
import org.sergp.bookingservice.models.Booking;
import org.sergp.bookingservice.models.Status;
import org.sergp.bookingservice.repositories.BookingRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class KafkaConsumerService {

    private final BookingRepository bookingRepository;



    @KafkaListener(topics = "booking-payment-confirmations", groupId = "payment-confirmation-group", containerFactory = "PaymentConfirmationEventContainerFactory")
    public void paymentConfirmationEvent(PaymentConfirmationEvent paymentMessage, Acknowledgment acknowledgment) {

        log.info("Payment from Kafka: {}", paymentMessage);
        if (paymentMessage.getStatus().equals(Status.COMPLETED)) {
            changeStatus(paymentMessage.getBookingId(), Status.COMPLETED);
            acknowledgment.acknowledge();
        } else if (paymentMessage.getStatus().equals(Status.EXPIRED)) {
            changeStatus(paymentMessage.getBookingId(), Status.EXPIRED);
            acknowledgment.acknowledge();
        }
    }

    private void changeStatus(UUID bookingId, Status status){
        Booking booking = getBookingByUUID(bookingId);
        booking.setStatus(status);
        bookingRepository.save(booking);
    }

    private Booking getBookingByUUID(UUID uuid){
        return bookingRepository.findById(uuid)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
    }

}
