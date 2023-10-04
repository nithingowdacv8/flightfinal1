package com.gfreitash.flight_booking.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name="origin")
    private Airport origin;

    @NotNull
    @ManyToOne
    @JoinColumn(name="destination")
    private Airport destination;

    @NotNull
    private LocalDateTime departure;

    @NotNull
    private LocalDateTime estimatedArrival;

    private LocalDateTime actualArrival;

    @NotNull
    private Integer seatRows;

    @NotNull
    private Integer seatColumns;

    public void setSeatColumns(Integer seatColumns) {
        if (seatColumns < 1) {
            throw new IllegalArgumentException("Seat columns must be greater than 0");
        } else if (seatColumns > 26) {
            throw new IllegalArgumentException("Seat columns must be less than 26");
        }
        this.seatColumns = seatColumns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return id.equals(flight.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}