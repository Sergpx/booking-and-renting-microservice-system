package org.sergp.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyAvailableResponse {

    private Boolean status;
    private BigDecimal price;

}
