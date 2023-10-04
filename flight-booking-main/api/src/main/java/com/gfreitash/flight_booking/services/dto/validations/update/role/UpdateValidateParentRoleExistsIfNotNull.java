package com.gfreitash.flight_booking.services.dto.validations.update.role;

import com.gfreitash.flight_booking.repositories.RoleRepository;
import com.gfreitash.flight_booking.services.dto.update.RoleUpdateDTO;
import com.gfreitash.flight_booking.services.validations.SpecificationValidator;
import com.gfreitash.flight_booking.services.validations.Validates;
import com.gfreitash.flight_booking.services.validations.exceptions.RoleDoesNotExistException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validates(RoleUpdateDTO.class)
public class UpdateValidateParentRoleExistsIfNotNull implements SpecificationValidator<RoleUpdateDTO> {
    private final RoleRepository roleRepository;
    @Override
    public void validate(RoleUpdateDTO dto) {
        if (dto.parentRole() != null) {
            var parentRole = roleRepository.findByName(dto.parentRole());
            if (parentRole.isEmpty()) {
                throw new RoleDoesNotExistException("Parent role does not exist");
            }
        }
    }
}
