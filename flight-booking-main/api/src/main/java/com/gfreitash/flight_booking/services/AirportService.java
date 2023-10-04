package com.gfreitash.flight_booking.services;

import com.gfreitash.flight_booking.services.dto.output.AirportOutputDTO;
import com.gfreitash.flight_booking.repositories.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;

    public Optional<AirportOutputDTO> getAirportById(String id) {
        return airportRepository.findById(Integer.valueOf(id)).map(AirportOutputDTO::new);
    }

    public List<AirportOutputDTO> getAllAirports() {
        return airportRepository.findAll().stream().map(AirportOutputDTO::new).toList();
    }

    public Page<AirportOutputDTO> getAllAirports(Pageable pagination) {
        return airportRepository.findAll(pagination).map(AirportOutputDTO::new);
    }

}
