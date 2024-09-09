package org.sergp.paymentservice.kafka.message;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RefundSuccessMessage {

    private UUID bookingId;

    private String email;


}
