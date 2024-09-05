package org.sergp.bookingservice.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateRequest {

    @NotNull(message = "Property Id is required")
    private UUID propertyId;

    @NotNull(message = "Start Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Future
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "End Date is required")
    @Future
    private LocalDate endDate;

    @Email(message = "Email is required")
    @NotBlank(message = "Email is required")
    private String email;


}
