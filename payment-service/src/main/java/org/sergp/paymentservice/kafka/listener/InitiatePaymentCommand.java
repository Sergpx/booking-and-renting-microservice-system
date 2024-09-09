package org.sergp.paymentservice.kafka.listener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InitiatePaymentCommand {

    private UUID bookingId;

    private UUID userId;

    private BigDecimal amount;

    private String email;

    private String phoneNumber;



}
