package com.gfreitash.flight_booking.repositories;

import com.gfreitash.flight_booking.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
}
