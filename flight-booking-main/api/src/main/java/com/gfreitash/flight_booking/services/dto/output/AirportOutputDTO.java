package com.gfreitash.flight_booking.services.dto.output;
import com.gfreitash.flight_booking.entities.Airport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "airports", itemRelation = "airport")
public record AirportOutputDTO(@NotNull Integer id,
                               @NotBlank String name,
                               @NotBlank String iataCode,
                               @NotBlank String city,
                               @NotBlank String state,
                               @NotBlank String stateAbbreviation,
                               @NotNull Double latitude,
                               @NotNull Double longitude) {

    public AirportOutputDTO(Airport airport) {
        this(
                airport.getId(),
                airport.getName(),
                airport.getIataCode(),
                airport.getCity(),
                airport.getState(),
                airport.getStateAbbreviation(),
                airport.getLatitude(),
                airport.getLongitude()
        );
    }
}
