package org.sergp.paymentservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
public class PaymentResponse {

    private String id;
    private String status;
    private Amount amount;
    private String description;
    private Recipient recipient;
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
    private Confirmation confirmation;
    private boolean test;
    private boolean paid;
    private boolean refundable;
    private Map<String, Object> metadata;

    // Геттеры и сеттеры

    @Data
    public static class Amount {
        private String value;
        private String currency;

        // Геттеры и сеттеры
    }

    @Data
    public static class Recipient {
        @JsonProperty("account_id")
        private String accountId;

        @JsonProperty("gateway_id")
        private String gatewayId;

        // Геттеры и сеттеры
    }

    @Data
    public static class Confirmation {
        private String type;

        @JsonProperty("confirmation_url")
        private String confirmationUrl;

        // Геттеры и сеттеры
    }
}
