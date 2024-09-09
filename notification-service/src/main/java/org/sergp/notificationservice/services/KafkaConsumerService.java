package org.sergp.notificationservice.services;


import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.sergp.notificationservice.kafka.listener.PaymentLinkEmailNotification;
import org.sergp.notificationservice.kafka.listener.RefundEmailNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final String PAYMENT_LINK_EMAIL_NOTIFICATION_TOPIC= "payment-link-notification-topic";
    private final String REFUND_EMAIL_NOTIFICATION_TOPIC = "refund-email-notification";

    @KafkaListener(topics = PAYMENT_LINK_EMAIL_NOTIFICATION_TOPIC, groupId = "notification-group", containerFactory = "PaymentLinkEmailNotificationContainerFactory")
    public void notificationConsumer(PaymentLinkEmailNotification notificationMessage, Acknowledgment acknowledgment) {

        log.info("Notification message received: {}", notificationMessage.toString());
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromEmail);
        simpleMailMessage.setTo(notificationMessage.getEmail());
        simpleMailMessage.setSubject("Link to payment");
        simpleMailMessage.setText(notificationMessage.getConfirmationUrl());

        mailSender.send(simpleMailMessage);
        log.info("Payment link sent to {}", notificationMessage.getEmail());

        acknowledgment.acknowledge();

    }

    @KafkaListener(topics = REFUND_EMAIL_NOTIFICATION_TOPIC, groupId = "notification-group", containerFactory = "RefundEmailNotificationContainerFactory")
    public void notificationConsumer(RefundEmailNotification refundEmailNotification, Acknowledgment acknowledgment) {

        log.info("Notification message received: {}", refundEmailNotification.toString());
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromEmail);
        simpleMailMessage.setTo(refundEmailNotification.getEmail());
        simpleMailMessage.setSubject("Refund successful");

        StringBuilder sb = new StringBuilder();
        sb.append("Booking ").append(refundEmailNotification.getBookingId()).append(" has been successfully refunded");

        simpleMailMessage.setText(sb.toString());

        mailSender.send(simpleMailMessage);
        log.info("Refund email sent to {}", refundEmailNotification.getEmail());

        acknowledgment.acknowledge();

    }


}
