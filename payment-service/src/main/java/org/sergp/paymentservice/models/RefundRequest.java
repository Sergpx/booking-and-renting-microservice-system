package org.sergp.paymentservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {

    private Amount amount;
    @JsonProperty("payment_id")
    private String paymentId;

    @Builder
    @Data
    public static class Amount {
        private BigDecimal value;
        private String currency;

        // Конструкторы, геттеры и сеттеры
    }

}
