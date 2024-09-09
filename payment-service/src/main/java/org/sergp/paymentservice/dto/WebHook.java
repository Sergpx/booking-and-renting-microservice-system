package org.sergp.paymentservice.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebHook {
    private String type;
    private String event;
    private ObjectDetails object;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ObjectDetails {
        private String id;
        private String status;
        private Amount amount;
        private Amount income_amount;
        private String description;
        private Recipient recipient;
        private PaymentMethod payment_method;
        private String captured_at;
        private String created_at;
        private boolean test;
        private Amount refunded_amount;
        private boolean paid;
        private boolean refundable;
        private Metadata metadata;
        private AuthorizationDetails authorization_details;
        private CancellationDetails cancellation_details;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amount {
        private String value;
        private String currency;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Recipient {
        private String account_id;
        private String gateway_id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentMethod {
        private String type;
        private String id;
        private boolean saved;
        private String title;
        private Card card;
        private String account_number;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Card {
        private String first6;
        private String last4;
        private String expiry_year;
        private String expiry_month;
        private String card_type;
        private CardProduct card_product;
        private String issuer_country;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CardProduct {
        private String code;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        private UUID booking_id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorizationDetails {
        private String rrn;
        private String auth_code;
        private ThreeDSecure three_d_secure;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ThreeDSecure {
        private boolean applied;
        private boolean method_completed;
        private boolean challenge_completed;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CancellationDetails{
        private String party;
        private String reason;
    }
}
