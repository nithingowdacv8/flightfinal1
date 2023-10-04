package com.gfreitash.flight_booking.repositories;

import com.gfreitash.flight_booking.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Booking.BookingId> {
}
