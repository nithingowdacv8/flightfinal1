package com.gfreitash.flight_booking.services.validations;

public interface SpecificationValidator<T> {
    void validate(T dto);
}
