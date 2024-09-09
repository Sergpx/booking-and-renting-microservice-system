package org.sergp.paymentservice.kafka.listener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CanceledPaymentEvent {

    private UUID bookingId;

}
