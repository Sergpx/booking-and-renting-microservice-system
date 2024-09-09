package org.sergp.paymentservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaymentRequest {

    private Amount amount;
    private PaymentMethodData paymentMethodData;
    private Confirmation confirmation;
    private Metadata metadata;
    private String description;
    private Boolean capture;

    // Конструкторы, геттеры и сеттеры

    @Builder
    @Data
    public static class Amount {
        private BigDecimal value;
        private String currency;

        // Конструкторы, геттеры и сеттеры
    }

    @Builder
    @Data
    public static class PaymentMethodData {
        private String type;

        // Конструкторы, геттеры и сеттеры
    }

    @Builder
    @Data
    public static class Confirmation {
        private String type;
        private String return_url;

        // Конструкторы, геттеры и сеттеры
    }

    @Builder
    @Data
    public static class Metadata {
        private UUID booking_id;
    }

}
