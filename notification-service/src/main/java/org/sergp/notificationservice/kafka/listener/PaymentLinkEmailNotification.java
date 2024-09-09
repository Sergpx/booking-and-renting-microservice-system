package org.sergp.notificationservice.kafka.listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentLinkEmailNotification {

    private UUID paymentId;
    private String confirmationUrl;
    private String email;
    private String number;

}
