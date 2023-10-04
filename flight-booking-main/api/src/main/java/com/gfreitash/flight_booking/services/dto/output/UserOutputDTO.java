package com.gfreitash.flight_booking.services.dto.output;

import com.gfreitash.flight_booking.entities.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserOutputDTO(
        @NotNull Integer id,
        @NotBlank @Email String email,
        @NotBlank String name,
        @NotBlank String surname,
        @NotBlank @Valid RoleOutputDTO role
) {
    public UserOutputDTO(User user) {
        this(user.getId(), user.getEmail(), user.getName(), user.getSurname(), new RoleOutputDTO(user.getRole()));
    }
}
