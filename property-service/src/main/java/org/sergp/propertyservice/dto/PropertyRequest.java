package org.sergp.propertyservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyRequest {

    @NotBlank(message = "Address must not be empty")
    private String address;

    @NotBlank(message = "Title must not be empty")
    private String title;

    @NotBlank(message = "Description must not be empty")
    private String description;

    @NotNull
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Rooms must not be empty")
    private Short bedrooms;





}
