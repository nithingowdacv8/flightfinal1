package com.gfreitash.flight_booking.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @EmbeddedId
    private BookingId id;

    @NotNull
    private Integer ticket;

    @NotNull
    private Integer seatRow;

    @NotNull
    private Integer seatColumn;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingId implements Serializable {
        @NotNull
        private Integer flightId;
        @NotNull
        private Integer passengerId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BookingId bookingId = (BookingId) o;
            return flightId.equals(bookingId.flightId) && passengerId.equals(bookingId.passengerId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(flightId, passengerId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id.equals(booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
