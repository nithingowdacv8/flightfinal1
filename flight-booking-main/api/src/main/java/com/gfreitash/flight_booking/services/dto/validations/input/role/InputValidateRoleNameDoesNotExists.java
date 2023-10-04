package com.gfreitash.flight_booking.services.dto.validations.input.role;

import com.gfreitash.flight_booking.repositories.RoleRepository;
import com.gfreitash.flight_booking.services.dto.input.RoleInputDTO;
import com.gfreitash.flight_booking.services.validations.SpecificationValidator;
import com.gfreitash.flight_booking.services.validations.Validates;
import com.gfreitash.flight_booking.services.validations.exceptions.RoleAlreadyExistsException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validates(RoleInputDTO.class)
public class InputValidateRoleNameDoesNotExists implements SpecificationValidator<RoleInputDTO> {
    private final RoleRepository roleRepository;

    @Override
    public void validate(RoleInputDTO dto) {
        if(roleRepository.existsByName(dto.name()))
            throw new RoleAlreadyExistsException(String.format("Role with name %s already exists", dto.name()));
    }
}
