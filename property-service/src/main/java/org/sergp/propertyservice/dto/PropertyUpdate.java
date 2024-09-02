package org.sergp.propertyservice.dto;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyUpdate {

    @Pattern(regexp = "^(?!\\s)\\S.*$", message = "Address should not start with a space and should not consist of spaces only.")
    private String address;

    @Pattern(regexp = "^(?!\\s)\\S.*$", message = "Title should not start with a space and should not consist of spaces only.")
    private String title;

    @Pattern(regexp = "^(?!\\s)\\S.*$", message = "Description should not start with a space and should not consist of spaces only.")
    private String description;

    @Positive(message = "Price not be negative or zero")
    private BigDecimal price;

    @Positive(message = "Rooms not be negative or zero")
    private Short bedrooms;

}
