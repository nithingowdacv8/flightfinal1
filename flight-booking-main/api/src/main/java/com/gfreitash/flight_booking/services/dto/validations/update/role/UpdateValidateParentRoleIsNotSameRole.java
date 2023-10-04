package com.gfreitash.flight_booking.services.dto.validations.update.role;

import com.gfreitash.flight_booking.services.dto.update.RoleUpdateDTO;
import com.gfreitash.flight_booking.services.validations.SpecificationValidator;
import com.gfreitash.flight_booking.services.validations.Validates;
import com.gfreitash.flight_booking.services.validations.exceptions.RoleSameAsParentRoleException;

import java.text.Collator;

@Validates(RoleUpdateDTO.class)
public class UpdateValidateParentRoleIsNotSameRole implements SpecificationValidator<RoleUpdateDTO> {
    @Override
    public void validate(RoleUpdateDTO dto) {
        var collator = Collator.getInstance();
        collator.setStrength(Collator.PRIMARY);

        if (dto.name() != null && dto.parentRole() != null && collator.compare(dto.name(), dto.parentRole()) == 0) {
            throw new RoleSameAsParentRoleException("Parent role cannot be the same as the role");
        }
    }
}
