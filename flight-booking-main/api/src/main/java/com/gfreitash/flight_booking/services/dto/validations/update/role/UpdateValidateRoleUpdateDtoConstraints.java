package com.gfreitash.flight_booking.services.dto.validations.update.role;

import com.gfreitash.flight_booking.services.dto.update.RoleUpdateDTO;
import com.gfreitash.flight_booking.services.validations.FieldsConstraintsAreValid;
import com.gfreitash.flight_booking.services.validations.Validates;
import jakarta.validation.Validator;

@Validates(RoleUpdateDTO.class)
public class UpdateValidateRoleUpdateDtoConstraints extends FieldsConstraintsAreValid<RoleUpdateDTO> {
    public UpdateValidateRoleUpdateDtoConstraints(Validator validator) {
        super(validator);
    }
}
