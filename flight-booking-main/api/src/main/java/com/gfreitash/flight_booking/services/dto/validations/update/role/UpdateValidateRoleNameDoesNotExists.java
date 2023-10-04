package com.gfreitash.flight_booking.services.dto.validations.update.role;

import com.gfreitash.flight_booking.repositories.RoleRepository;
import com.gfreitash.flight_booking.services.dto.update.RoleUpdateDTO;
import com.gfreitash.flight_booking.services.validations.SpecificationValidator;
import com.gfreitash.flight_booking.services.validations.Validates;
import com.gfreitash.flight_booking.services.validations.exceptions.RoleAlreadyExistsException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validates(RoleUpdateDTO.class)
public class UpdateValidateRoleNameDoesNotExists implements SpecificationValidator<RoleUpdateDTO> {
    private final RoleRepository roleRepository;

    @Override
    public void validate(RoleUpdateDTO dto) {
        var role = roleRepository.findByName(dto.name());
        if(role.isPresent() && !role.get().getId().equals(dto.id()))
            throw new RoleAlreadyExistsException(String.format("Role with name %s already exists", dto.name()));
    }
}
