package org.sergp.bookingservice.kafka.listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sergp.bookingservice.models.Status;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentConfirmationEvent {

    private UUID bookingId;

    private Status status;

}
