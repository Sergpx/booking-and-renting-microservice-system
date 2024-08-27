package org.sergp.propertyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDTO {

    private UUID id;

    private String address;

    private String description;

    private BigDecimal price;

    private short bedrooms;

    private UUID ownerId;



}
