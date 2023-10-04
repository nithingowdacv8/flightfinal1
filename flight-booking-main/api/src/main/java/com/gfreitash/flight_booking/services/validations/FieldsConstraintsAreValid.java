package com.gfreitash.flight_booking.services.validations;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;


/**
 * This class is used to validate the constraints of the fields of an object,
 * just like if you use @Valid annotation.
 * However, this class may be used to validate objects not annotated with @Valid.
 * Since it also implements SpecificationValidator interface, it can be used in conjunction with
 * other validators.
 */
@RequiredArgsConstructor
public abstract class FieldsConstraintsAreValid<T> implements SpecificationValidator<T> {

    private final Validator validator;
    public void validate(T t) {
        var violations = validator.validate(t);
        if (!violations.isEmpty()) {
           throw new ConstraintViolationException(violations);
        }
    }
}
