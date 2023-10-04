package com.gfreitash.flight_booking.repositories;

import com.gfreitash.flight_booking.entities.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
}
