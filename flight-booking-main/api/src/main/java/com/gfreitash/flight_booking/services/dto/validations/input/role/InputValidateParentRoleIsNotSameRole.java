package com.gfreitash.flight_booking.services.dto.validations.input.role;

import com.gfreitash.flight_booking.services.dto.input.RoleInputDTO;
import com.gfreitash.flight_booking.services.validations.SpecificationValidator;
import com.gfreitash.flight_booking.services.validations.Validates;
import com.gfreitash.flight_booking.services.validations.exceptions.RoleSameAsParentRoleException;

import java.text.Collator;

@Validates(RoleInputDTO.class)
public class InputValidateParentRoleIsNotSameRole implements SpecificationValidator<RoleInputDTO> {
    @Override
    public void validate(RoleInputDTO dto) {
        var collator = Collator.getInstance();
        collator.setStrength(Collator.PRIMARY);

        if (dto.name() != null && dto.parentRole() != null && collator.compare(dto.name(), dto.parentRole()) == 0) {
            throw new RoleSameAsParentRoleException("Parent role cannot be the same as the role");
        }
    }
}
