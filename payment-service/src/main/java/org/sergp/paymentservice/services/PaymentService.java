package org.sergp.paymentservice.services;


import lombok.RequiredArgsConstructor;
import org.sergp.paymentservice.models.Payment;
import org.sergp.paymentservice.repositories.PaymentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;


    public Flux<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

}
