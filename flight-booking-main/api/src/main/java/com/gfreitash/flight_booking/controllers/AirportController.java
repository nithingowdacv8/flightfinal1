package com.gfreitash.flight_booking.controllers;

import com.gfreitash.flight_booking.controllers.assemblers.EntityModelAssembler;
import com.gfreitash.flight_booking.services.dto.output.AirportOutputDTO;
import com.gfreitash.flight_booking.services.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.function.Function;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportService airportService;
    private final EntityModelAssembler<AirportOutputDTO> airportAssembler;

    @Autowired
    public AirportController(AirportService airportService) {
        this.airportService = airportService;
        this.airportAssembler = new EntityModelAssembler<>(AirportController.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AirportOutputDTO>> getOneAirport(@PathVariable String id) {
        var selfLink = linkTo(methodOn(AirportController.class).getOneAirport(id)).withSelfRel();

        return airportService.getAirportById(id)
                .map(airport -> airportAssembler.toModel(airport, selfLink))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<AirportOutputDTO>>> getAllAirports(Pageable pagination) {
        var airports = airportService.getAllAirports(pagination);

        Function<EntityModel<AirportOutputDTO>, Void> itemLinks = airportModel -> {
            var airportId = String.valueOf(Objects.requireNonNull(airportModel.getContent()).id());

            airportModel.add(linkTo(methodOn(AirportController.class).getOneAirport(airportId)).withSelfRel());
            return null;
        };

        var airportCollectionModel = airportAssembler.toCollectionModel(airports.getContent(), itemLinks);
        var pagedModel = airportAssembler.toPagedModel(airports, pagination, airportCollectionModel);

        return ResponseEntity.ok().body(pagedModel);
    }
}
