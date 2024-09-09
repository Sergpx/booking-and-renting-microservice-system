package org.sergp.paymentservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class RefundResponse {

    private String id;
    @JsonProperty("payment_id")
    private String paymentId;
    private String status;
    @JsonProperty("created_at")
    private String createdAt;
    private Amount amount;

    @Data
    public static class Amount {
        private String value;
        private String currency;

    }
}
