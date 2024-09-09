package org.sergp.paymentservice.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "payments")
public class Payment {

    @Id
    private UUID _id;

    private String paymentId;

    private UUID bookingId;

    private UUID userId;

    private BigDecimal amount;

    private Status status;

    private String payment_method;

    private String created_at;

    private String confirmationUrl;

    private String email;

}
