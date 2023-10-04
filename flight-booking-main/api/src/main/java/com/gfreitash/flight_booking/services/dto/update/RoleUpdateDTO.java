package com.gfreitash.flight_booking.services.dto.update;

import com.gfreitash.dto_mapper_processor.DtoMapper;
import com.gfreitash.flight_booking.config.ApplicationConfig;
import com.gfreitash.flight_booking.entities.Role;
import com.gfreitash.flight_booking.services.dto.mappers.RoleMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
@DtoMapper(
        entity = Role.class,
        componentModel = "spring",
        implementationPackage = ApplicationConfig.MAPPERS_PACKAGE,
        uses = {RoleMapper.class}
)
public record RoleUpdateDTO(
        @Positive Integer id,
        @NotBlank String name,
        String parentRole) {
}
