package org.sergp.bookingservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sergp.bookingservice.dto.BookingCreateRequest;
import org.sergp.bookingservice.exceptions.*;
import org.sergp.bookingservice.kafka.message.CanceledPaymentEvent;
import org.sergp.bookingservice.kafka.message.InitiatePaymentCommand;
import org.sergp.bookingservice.dto.PropertyAvailableResponse;
import org.sergp.bookingservice.models.Booking;
import org.sergp.bookingservice.models.Status;
import org.sergp.bookingservice.repositories.BookingRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
//@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, InitiatePaymentCommand> initiatePaymentCommandTemplate;
    private final KafkaTemplate<String, CanceledPaymentEvent> canceledPaymentEventTemplate;

    private final String IS_PROPERTY_ACTIVE_URL = "lb://property-service/api/properties/{id}/isPropertyActive";

    private final String INITIATE_PAYMENT_TOPIC = "initiate-payment-topic";
    private final String CANCEL_PAYMENT_TOPIC = "cancel-payment-topic";


    public List<Booking> getUserBookings(HttpServletRequest request) {
        return bookingRepository.findBookingsByUserId(UUID.fromString(request.getHeader("id")));
    }

    public Boolean getBookingAvailability(UUID propertyId, OffsetDateTime startDate, OffsetDateTime endDate, HttpServletRequest request) {
            if(!endDate.isAfter(startDate)){
                throw new IncorrectDateException("End date should be after start date");
            }
            PropertyAvailableResponse propertyAvailableResponse = getPropertyResponse(propertyId, request);
            if(!propertyAvailableResponse.getStatus()) return false;
            Long count = bookingRepository.countBookingsInDateRange(propertyId, startDate, endDate);
            return (count == 0);

    }

    @Transactional(transactionManager = "transactionManager")
    public Booking createBooking(BookingCreateRequest booking, HttpServletRequest request) {
        OffsetDateTime startDate = booking.getStartDate().atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        OffsetDateTime endDate = booking.getEndDate().atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();

        if(!endDate.isAfter(startDate)){
            throw new IncorrectDateException("End date should be after start date");
        }

        PropertyAvailableResponse propertyAvailableResponse = getPropertyResponse(booking.getPropertyId(), request);

        if(!propertyAvailableResponse.getStatus() ||
                bookingRepository.countBookingsInDateRange(booking.getPropertyId(), startDate, endDate) != 0){
            throw new BookingNotAvailableException("Property is not available");
        }

        Booking newBooking = bookingRepository.save(Booking
                .builder()
                .propertyId(booking.getPropertyId())
                .userId(UUID.fromString(request.getHeader("id")))
                .startDate(startDate)
                .endDate(endDate)
                .status(Status.PENDING_PAYMENT)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build()
        );

        bookingRepository.save(newBooking);

        initiatePaymentCommandTemplate.executeInTransaction((operations) -> {
            long days = Duration.between(startDate, endDate).toDays();
            InitiatePaymentCommand initiatePaymentCommand = InitiatePaymentCommand
                    .builder()
                    .bookingId(newBooking.getId())
                    .userId(newBooking.getUserId())
                    .amount(propertyAvailableResponse.getPrice().multiply(BigDecimal.valueOf(days)))
                    .email(booking.getEmail())
                    .build();

            initiatePaymentCommandTemplate.send(INITIATE_PAYMENT_TOPIC, String.valueOf(newBooking.getId()), initiatePaymentCommand);
            log.info("Message in Kafka sent");
            return null;
        });
            return newBooking;


    }

    @Transactional(transactionManager = "transactionManager")
    public void cancelBooking(UUID id, HttpServletRequest request) {
        Booking booking = findById(id);
        if(!booking.getUserId().equals(UUID.fromString(request.getHeader("id")))){
            throw new AccessDeniedException("You can only cancel your own booking");
        }
        if(!(booking.getStatus() == Status.COMPLETED)){
            throw new WrongStatusException("You can cancel only completed bookings");
        }
        booking.setStatus(Status.REFUND_PROCESS);
        booking.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        bookingRepository.save(booking);
        CanceledPaymentEvent canceledPaymentEvent = CanceledPaymentEvent
                .builder()
                .bookingId(id)
                .build();
        canceledPaymentEventTemplate.executeInTransaction(operations -> {
            canceledPaymentEventTemplate.send(CANCEL_PAYMENT_TOPIC, canceledPaymentEvent);
            log.info("Cancelled payment event sent to Kafka {}", canceledPaymentEvent);
            return null;
        });
    }


    // private methods

    @Transactional(readOnly = true)
    public PropertyAvailableResponse getPropertyResponse(UUID propertyId, HttpServletRequest request) {

        return webClientBuilder.build().get()
                .uri(IS_PROPERTY_ACTIVE_URL, propertyId)
                .header("Authorization", request.getHeader("auth-token")) // TEST WITHOUT AUTH
                .retrieve()
                .bodyToMono(PropertyAvailableResponse.class)
                .doOnError(error -> log.error(error.getMessage()))
                .block();

    }

    private Booking findById(UUID id) {
        return bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException("Booking not found"));
    }



}
