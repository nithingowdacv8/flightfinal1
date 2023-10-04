package com.gfreitash.flight_booking.services.dto.validations.update.role;

import com.gfreitash.flight_booking.repositories.RoleRepository;
import com.gfreitash.flight_booking.services.dto.update.RoleUpdateDTO;
import com.gfreitash.flight_booking.services.validations.SpecificationValidator;
import com.gfreitash.flight_booking.services.validations.Validates;
import com.gfreitash.flight_booking.services.validations.exceptions.RoleIsOwnSubRoleException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validates(RoleUpdateDTO.class)
public class UpdateValidateRoleNotSubroleOfItself implements SpecificationValidator<RoleUpdateDTO> {
    private final RoleRepository roleRepository;

    @Override
    public void validate(RoleUpdateDTO dto) {
        if (dto.parentRole() == null) {
            return;
        }
        var originalRole = roleRepository.findById(dto.id()).orElse(null);
        var parentRole = roleRepository.findByName(dto.parentRole()).orElse(null);
        while (parentRole != null && originalRole != null) {
            if (parentRole.getName().equals(originalRole.getName())) {
                throw new RoleIsOwnSubRoleException("Role cannot be a sub role of itself");
            }
            parentRole = parentRole.getParentRole();
        }

    }
}
