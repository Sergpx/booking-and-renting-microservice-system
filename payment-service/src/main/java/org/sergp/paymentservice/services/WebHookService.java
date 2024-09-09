package org.sergp.paymentservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sergp.paymentservice.exceptions.PaymentNotFoundException;
import org.sergp.paymentservice.exceptions.UnknownWebHookException;
import org.sergp.paymentservice.kafka.message.PaymentConfirmationEvent;
import org.sergp.paymentservice.dto.WebHook;
import org.sergp.paymentservice.models.Payment;

import org.sergp.paymentservice.models.Status;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
public class WebHookService {

    private final KafkaTemplate<String, PaymentConfirmationEvent> paymentKafkaTemplate;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final TransactionalOperator transactionalOperator;

    private final String PAYMENT_CONFIRMATION_EVENT_TOPIC = "payment-confirmation";

//    private final List<String> ipAddresses = List.of(
//            "172.16.17.32" // IpAddresses wallet service for verification webhooks
//    );


    public Mono<Void> webhookProcess(WebHook webHook, HttpServletRequest request) {
        // method for verifying webhooks by ip (non local service) or maybe use filters
        log.info("Webhook received: {}", webHook);
        switch (webHook.getEvent()) {
            case "payment.succeeded" -> {
                return anyWebHook(webHook, Status.COMPLETED);
            }
            case "payment.canceled" -> {
                return anyWebHook(webHook, Status.EXPIRED);
            }
            case "refund.succeeded" -> {
                return Mono.empty();
            }
            default -> {
                log.error("Unknown event: {}", webHook);
                return Mono.error(new UnknownWebHookException("Unknown event type"));
            }
        }

    }

    // private methods

    private Mono<Void> anyWebHook(WebHook webHook, Status status) {
       return transactionalOperator.execute(context ->
                reactiveMongoTemplate.findAndModify(
                                Query.query(Criteria.where("bookingId").is(webHook.getObject().getMetadata().getBooking_id())),
                                new Update().set("status", status).set("payment_method", webHook.getObject().getPayment_method().getType()),
                                FindAndModifyOptions.options().returnNew(true),
                                Payment.class
                )
                .switchIfEmpty(Mono.error(new PaymentNotFoundException("Payment not found")))
                .flatMap(payment -> sendPaymentConfirmationEventMessage(payment, status))
                .doOnError(e -> log.error("Error processing canceled webhook: {}", e.getMessage()))
        ).then();
    }

    private Mono<Void> sendPaymentConfirmationEventMessage(Payment payment, Status status) {
        return Mono.fromFuture(() -> paymentKafkaTemplate.send(
                PAYMENT_CONFIRMATION_EVENT_TOPIC,
                String.valueOf(payment.get_id()),
                PaymentConfirmationEvent.builder()
                        .bookingId(payment.getBookingId())
                        .status(status)
                        .build())
                )
                .doOnSuccess(result -> log.info("Message sent to Kafka"))
                .doOnError(ex -> log.error("Error sending message to Kafka: {}", ex.getMessage()))
                .then();
    }

}
