package com.gfreitash.flight_booking.repositories;

import com.gfreitash.flight_booking.entities.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Integer> {
}
