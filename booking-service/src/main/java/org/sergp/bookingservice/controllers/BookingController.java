package org.sergp.bookingservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sergp.bookingservice.dto.BookingCreateRequest;
import org.sergp.bookingservice.services.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";


    @GetMapping
    public ResponseEntity<?> getUserBookings(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getUserBookings(request));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<?> getBookingAvailability(@PathVariable UUID id,
                            @RequestParam("startDate") @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) LocalDate startDate,
                            @RequestParam("endDate") @DateTimeFormat(pattern = DEFAULT_DATE_FORMAT) LocalDate endDate,
                            HttpServletRequest request) {

        OffsetDateTime start = startDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        OffsetDateTime end = endDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();

        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getBookingAvailability(id, start, end, request));
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingCreateRequest booking, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(booking, request));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable UUID id, HttpServletRequest request) {
        bookingService.cancelBooking(id, request);
        return ResponseEntity.status(HttpStatus.OK).body("Cancellation of the booking in progress");
    }

    // add admin endpoints






}
