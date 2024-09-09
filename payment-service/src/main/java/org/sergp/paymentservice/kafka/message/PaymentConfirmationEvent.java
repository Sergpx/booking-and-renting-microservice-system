package org.sergp.paymentservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sergp.paymentservice.models.Status;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaymentConfirmationEvent {

    private UUID bookingId;
    private Status status;

}
