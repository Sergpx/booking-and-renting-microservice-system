package org.sergp.bookingservice.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sergp.bookingservice.exceptions.BookingNotFoundException;
import org.sergp.bookingservice.kafka.listener.PaymentConfirmationEvent;
import org.sergp.bookingservice.kafka.listener.RefundSuccessMessage;
import org.sergp.bookingservice.kafka.message.RefundEmailNotification;
import org.sergp.bookingservice.models.Booking;
import org.sergp.bookingservice.models.Status;
import org.sergp.bookingservice.repositories.BookingRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final BookingRepository bookingRepository;
    private final String PAYMENT_CONFIRMATION_EVENT_TOPIC = "payment-confirmation";
    private final String REFUND_SUCCESS_TOPIC = "refund-success-topic";

    private final KafkaTemplate<String, RefundEmailNotification> refundEmailNotificationTemplate;
    private final String REFUND_EMAIL_NOTIFICATION_TOPIC = "refund-email-notification";


    @Transactional
    @KafkaListener(topics = PAYMENT_CONFIRMATION_EVENT_TOPIC, groupId = "payment-confirmation-group", containerFactory = "PaymentConfirmationEventContainerFactory")
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
    @Transactional
    @KafkaListener(topics = REFUND_SUCCESS_TOPIC, groupId = "refund-success-message-group", containerFactory = "RefundSuccessMessageContainerFactory")
    public void refundSuccessMessage(RefundSuccessMessage refundMessage, Acknowledgment acknowledgment) {
        Booking booking = getBookingByUUID(refundMessage.getBookingId());

        changeStatus(booking.getId(), Status.CANCELLED);
        RefundEmailNotification kafkaMessage = RefundEmailNotification.builder()
                .bookingId(booking.getId())
                .email(refundMessage.getEmail())
                .build();
        refundEmailNotificationTemplate.send(REFUND_EMAIL_NOTIFICATION_TOPIC, String.valueOf(booking.getId()), kafkaMessage);
        acknowledgment.acknowledge();
    }

    private void changeStatus(UUID bookingId, Status status){
        Booking booking = getBookingByUUID(bookingId);
        booking.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        booking.setStatus(status);
        bookingRepository.save(booking);
    }

    private Booking getBookingByUUID(UUID uuid){
        return bookingRepository.findById(uuid)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
    }

}
