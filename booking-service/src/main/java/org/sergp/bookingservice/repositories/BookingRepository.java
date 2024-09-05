package org.sergp.bookingservice.repositories;

import org.sergp.bookingservice.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query(value = "SELECT COUNT(*) " +
            "FROM booking b " +
            "WHERE :propertyId = b.property_id " +
            "AND b.status NOT IN ('CANCELLED', 'EXPIRED') " +
            "AND ( " +
            "(b.start_date BETWEEN :startDate AND :endDate) OR " +
            "(b.end_date BETWEEN :startDate AND :endDate) OR " +
            "(b.start_date <= :startDate AND b.end_date >= :endDate) " +
            ")",
            nativeQuery = true)

    Long countBookingsInDateRange(@Param("propertyId") UUID propertyId,
                           @Param("startDate") OffsetDateTime startDate,
                           @Param("endDate") OffsetDateTime endDate);

    List<Booking> findBookingsByUserId(UUID userId);

}
