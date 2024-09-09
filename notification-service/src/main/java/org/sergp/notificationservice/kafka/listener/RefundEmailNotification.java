package org.sergp.notificationservice.kafka.listener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundEmailNotification {

    private UUID bookingId;
    private String email;

}
