package org.sergp.paymentservice.repositories;

import org.sergp.paymentservice.models.Payment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


import java.util.UUID;

@Repository
public interface PaymentRepository extends ReactiveMongoRepository<Payment, UUID> {

    Mono<Payment> findPaymentByBookingId(UUID bookingId);

}
