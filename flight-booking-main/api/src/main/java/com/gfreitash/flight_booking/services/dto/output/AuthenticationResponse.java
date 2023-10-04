package com.gfreitash.flight_booking.services.dto.output;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationResponse(@NotBlank String token) {
}
