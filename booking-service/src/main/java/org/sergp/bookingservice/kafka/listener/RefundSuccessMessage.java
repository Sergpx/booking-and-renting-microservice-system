package org.sergp.bookingservice.kafka.listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefundSuccessMessage {

    private UUID bookingId;

    private String email;


}
