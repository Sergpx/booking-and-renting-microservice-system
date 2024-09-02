package org.sergp.propertyservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyResponse {

    private UUID id;

    private String title;

    private String address;

    private String description;

    private BigDecimal price;

    private Short bedrooms;

    private Boolean status; // true = active, false = inActive

    private UUID ownerId;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

}
