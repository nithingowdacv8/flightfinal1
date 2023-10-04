package com.gfreitash.flight_booking.services.dto.validations.input.role;

import com.gfreitash.flight_booking.services.dto.input.RoleInputDTO;
import com.gfreitash.flight_booking.services.validations.Validates;
import com.gfreitash.flight_booking.services.validations.SpecificationValidator;
import com.gfreitash.flight_booking.services.validations.exceptions.RoleDoesNotExistException;
import com.gfreitash.flight_booking.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;

@Validates(RoleInputDTO.class)
@RequiredArgsConstructor
public class InputValidateParentRoleExistsIfNotNull implements SpecificationValidator<RoleInputDTO> {
    private final RoleRepository roleRepository;

    @Override
    public void validate(RoleInputDTO dto) {
        if (dto.parentRole() != null) {
            var parentRole = roleRepository.findByName(dto.parentRole());
            if (parentRole.isEmpty()) {
                throw new RoleDoesNotExistException("Parent role does not exist");
            }
        }
    }
}
