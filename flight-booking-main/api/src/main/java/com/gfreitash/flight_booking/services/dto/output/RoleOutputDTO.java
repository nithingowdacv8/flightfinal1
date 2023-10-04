package com.gfreitash.flight_booking.services.dto.output;

import com.gfreitash.dto_mapper_processor.DtoMapper;
import com.gfreitash.flight_booking.config.ApplicationConfig;
import com.gfreitash.flight_booking.entities.Role;
import com.gfreitash.flight_booking.services.dto.mappers.RoleMapper;
import com.gfreitash.flight_booking.services.dto.mappers.RoleOutputDTOMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.hateoas.server.core.Relation;

@Builder
@Relation(collectionRelation = "roles", itemRelation = "role")
@DtoMapper(
        entity = Role.class,
        componentModel = "spring",
        implementationPackage = ApplicationConfig.MAPPERS_PACKAGE,
        uses = {RoleMapper.class}
)


public record RoleOutputDTO(
        @NotNull Integer id,

        @NotBlank String name,

        String parentRole
) {

    /**
     * @deprecated Use {@link RoleOutputDTOMapper} instead
     */
    @Deprecated(since = "Use RoleOutputDTOMapper instead")
    public RoleOutputDTO(Role role) {
        this(
                role.getId(),
                role.getName(),
                role.getParentRole() != null ? role.getParentRole().getName() : null
        );
    }
}
