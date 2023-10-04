package com.gfreitash.flight_booking.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    @Column(unique = true)
    private String iataCode;

    @NotNull
    private String city;

    @NotNull
    private String state;

    @NotNull
    private String stateAbbreviation;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private Long yearlyPassengers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport) o;
        return id.equals(airport.id) && iataCode.equals(airport.iataCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, iataCode);
    }
}
