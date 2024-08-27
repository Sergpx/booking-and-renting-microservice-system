package org.sergp.propertyservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "Property")
public class Property {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", name = "id")
    private UUID id;

    @Column(name = "address")
    @NotBlank
    private String address;

// TODO: add more fields
    //  @Column(name = "title")
    //  @NotBlank
    //  private String title;

    @Column(name = "description")
    @NotBlank
    private String description;

    @Column(name = "price")
    @NotNull
    private BigDecimal price;

    @Column(name = "bedrooms")
    @NotNull
    private Short bedrooms;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "owner_id")
    private UUID ownerId;



    // TODO: add more fields
}
