package com.gfreitash.flight_booking.services.dto.update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateDTO(
        @NotBlank String name,
        @NotBlank String surname,
        @NotBlank @Email String email,

        @NotBlank String password
) {
}
