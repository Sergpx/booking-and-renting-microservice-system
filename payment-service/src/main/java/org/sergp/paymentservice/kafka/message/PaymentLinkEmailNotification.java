package org.sergp.paymentservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentLinkEmailNotification {

    private UUID paymentId;
    private String confirmationUrl;
    private String email;
    private String phoneNumber;

}
