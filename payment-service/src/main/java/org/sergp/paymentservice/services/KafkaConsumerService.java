package org.sergp.paymentservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.sergp.paymentservice.exceptions.PaymentNotFoundException;
import org.sergp.paymentservice.kafka.listener.CanceledPaymentEvent;
import org.sergp.paymentservice.kafka.listener.InitiatePaymentCommand;
import org.sergp.paymentservice.kafka.message.PaymentLinkEmailNotification;
import org.sergp.paymentservice.kafka.message.RefundSuccessMessage;
import org.sergp.paymentservice.models.*;
import org.sergp.paymentservice.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class KafkaConsumerService {

    private final PaymentRepository paymentRepository;
    private final WebClient webClient;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final TransactionalOperator transactionalOperator;

    // Kafka Messages
    private final KafkaSender<String, PaymentLinkEmailNotification> paymentLinkEmailNotificationKafkaSender;
    private final String PAYMENT_LINK_EMAIL_NOTIFICATION_TOPIC= "payment-link-notification-topic";

    private final KafkaSender<String, RefundSuccessMessage> refundSuccessKafkaSender;
    private final String REFUND_SUCCESS_TOPIC = "refund-success-topic";

    // Kafka Listeners
    private final String INITIATE_PAYMENT_TOPIC = "initiate-payment-topic";
    private final String CANCEL_PAYMENT_TOPIC = "cancel-payment-topic";

    // YOOKASSA URLs
    private final String RETURN_URL = "https://github.com/Sergpx/booking-and-renting-microservice-system";
    private final String API_YOOKASSA_PAYMENTS_URI = "https://api.yookassa.ru/v3/payments";
    private final String API_YOOKASSA_REFUNDS_URI = "https://api.yookassa.ru/v3/refunds";

    // Secrets for YOOKASSA
    @Value("${SHOP_ID}")
    private String SHOP_ID;
    @Value("${SHOP_SECRET_KEY}")
    private String SHOP_SECRET_KEY;



    @KafkaListener(topics = INITIATE_PAYMENT_TOPIC, groupId = "initiate-payment-group", containerFactory = "InitiatePaymentCommandContainerFactory")
    public Mono<Void> initiatePaymentCommandConsumer(InitiatePaymentCommand initiatePaymentCommand, Acknowledgment acknowledgment){
        log.info("Message received: {}", initiatePaymentCommand);
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .amount(PaymentRequest.Amount.builder()
                        .value(initiatePaymentCommand.getAmount())
                        .currency("RUB")
                        .build())
                .paymentMethodData(PaymentRequest.PaymentMethodData.builder()
                        .type("bank_card")
                        .build())
                .confirmation(PaymentRequest.Confirmation.builder()
                        .type("redirect")
                        .return_url(RETURN_URL)
                        .build())
                .metadata(PaymentRequest.Metadata.builder()
                        .booking_id(initiatePaymentCommand.getBookingId())
                        .build())
                .description("Property rental")
                .capture(true)
                .build();

                return transactionalOperator.execute(context ->
                        webClient.post()
                                .uri(API_YOOKASSA_PAYMENTS_URI)
                                .header("Idempotence-Key", String.valueOf(UUID.randomUUID()))
                                .header("Content-Type", "application/json")
                                .headers(headers -> headers.setBasicAuth(SHOP_ID, SHOP_SECRET_KEY))
                                .bodyValue(paymentRequest)
                                .retrieve()
                                .bodyToMono(PaymentResponse.class)
                                .flatMap(response -> savePayment(response, initiatePaymentCommand))
                                .flatMap(savePayment -> sendPaymentLinkEmailNotificationMessage(savePayment, initiatePaymentCommand))
                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(3)))

                        ).then()
                .doOnSuccess(savedPayment -> {
                    // Успешная обработка
                    log.info("Payment and Kafka message processed successfully.");
                    acknowledgment.acknowledge();  // Подтверждение сообщения после успешного запроса
                })
                .doOnError(e -> {
                    log.error("Error occurred during payment processing for booking ID {}: {}", initiatePaymentCommand.getBookingId(), e.getMessage());
                    log.error("Full stacktrace: ", e);
                });
    }

    @KafkaListener(topics = CANCEL_PAYMENT_TOPIC, groupId = "cancel-payment-group", containerFactory = "CanceledPaymentEventContainerFactory")
    public Mono<Void> canceledPaymentEventConsumer(CanceledPaymentEvent canceledPaymentEvent, Acknowledgment acknowledgment){
        return paymentRepository.findPaymentByBookingId(canceledPaymentEvent.getBookingId())
                .switchIfEmpty(Mono.error(new PaymentNotFoundException("Payment not found")))
                .flatMap(payment -> {
                    RefundRequest refundRequest = RefundRequest.builder()
                            .amount(RefundRequest.Amount.builder()
                                    .value(payment.getAmount())
                                    .currency("RUB")
                                    .build())
                            .paymentId(payment.getPaymentId())
                            .build();

                    return transactionalOperator.execute(context ->
                                webClient.post()
                                        .uri(API_YOOKASSA_REFUNDS_URI)
                                        .header("Idempotence-Key", String.valueOf(UUID.randomUUID()))
                                        .header("Content-Type", "application/json")
                                        .headers(headers -> headers.setBasicAuth(SHOP_ID, SHOP_SECRET_KEY))
                                        .bodyValue(refundRequest)
                                        .retrieve()
                                        .bodyToMono(RefundResponse.class)
                                        .flatMap(response -> updateStatusPayment(payment, response))
                                        .flatMap(this::sendRefundSuccessMessage)
                                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(3)))

                    ).then()
                    .doOnSuccess(savedPayment -> {
                        // Успешная обработка
                        log.info("Payment canceled and Kafka message processed successfully");
                        acknowledgment.acknowledge();  // Подтверждение сообщения после успешного запроса
                    })
                    .doOnError(e -> {
                        log.error("Error occurred during payment cancellation for booking ID {}: {}", canceledPaymentEvent.getBookingId(), e.getMessage());
                        log.error("Full stacktrace: ", e);
                    });

                });
    }

    // private methods

    private Mono<Payment> savePayment(PaymentResponse response, InitiatePaymentCommand initiatePaymentCommand) {
        Payment payment = Payment.builder()
                ._id(UUID.randomUUID())
                .paymentId(response.getId())
                .bookingId(initiatePaymentCommand.getBookingId())
                .userId(initiatePaymentCommand.getUserId())
                .amount(initiatePaymentCommand.getAmount())
                .status(Status.PENDING_PAYMENT)
                .created_at(response.getCreatedAt().toString())
                .confirmationUrl(response.getConfirmation().getConfirmationUrl())
                .email(initiatePaymentCommand.getEmail())
                .build();

        return reactiveMongoTemplate.save(payment);
    }

    private Mono<Payment> updateStatusPayment(Payment payment, RefundResponse refundResponse) {
        log.info("paymentResponse: {}", refundResponse.toString());
        if (refundResponse.getStatus().equals("succeeded")){
            payment.setStatus(Status.CANCELLED);
            return reactiveMongoTemplate.save(payment);
        }
        return Mono.error(new RuntimeException(refundResponse.getStatus()));
    }

    // KAFKA

    private Mono<Void> sendPaymentLinkEmailNotificationMessage(Payment savedPayment, InitiatePaymentCommand initiatePaymentCommand) {
        PaymentLinkEmailNotification kafkaMessage = PaymentLinkEmailNotification.builder()
                .paymentId(savedPayment.get_id())
                .confirmationUrl(savedPayment.getConfirmationUrl())
                .phoneNumber(initiatePaymentCommand.getPhoneNumber())
                .email(initiatePaymentCommand.getEmail())
                .build();

        return paymentLinkEmailNotificationKafkaSender.send(Mono.just(SenderRecord.create(
                        new ProducerRecord<>(PAYMENT_LINK_EMAIL_NOTIFICATION_TOPIC, String.valueOf(savedPayment.get_id()), kafkaMessage),
                        savedPayment.get_id()
                ))).then();
    }

    private Mono<Void> sendRefundSuccessMessage(Payment savedPayment) {
        RefundSuccessMessage kafkaMessage = RefundSuccessMessage.builder()
                .bookingId(savedPayment.getBookingId())
                .email(savedPayment.getEmail())
                .build();

        return refundSuccessKafkaSender.send(Mono.just(SenderRecord.create(
                new ProducerRecord<>(REFUND_SUCCESS_TOPIC, String.valueOf(savedPayment.get_id()), kafkaMessage),
                savedPayment.get_id()
        ))).then();
    }

}
