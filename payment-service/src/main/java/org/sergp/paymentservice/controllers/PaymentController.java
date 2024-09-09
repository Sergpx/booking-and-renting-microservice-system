package org.sergp.paymentservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sergp.paymentservice.dto.WebHook;
import org.sergp.paymentservice.models.Payment;
import org.sergp.paymentservice.services.PaymentService;
import org.sergp.paymentservice.services.WebHookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {

    private final WebHookService webHookService;
    private final PaymentService paymentService;

    @GetMapping
    public Flux<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @PostMapping("/webhook")
    public Mono<ResponseEntity<Object>> webhook(@RequestBody WebHook webHook, HttpServletRequest request) {
        return webHookService.webhookProcess(webHook, request)
                .then(Mono.just(ResponseEntity.status(HttpStatus.OK).build()))
                .onErrorResume(e -> {
                    log.error("Error processing webhook: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}
