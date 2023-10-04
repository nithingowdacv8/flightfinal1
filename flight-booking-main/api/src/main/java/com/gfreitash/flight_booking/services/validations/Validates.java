package com.gfreitash.flight_booking.services.validations;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to designate a validator as the validator for a specific class.
 * It serves as a complementary annotation for a class that implements the SpecificationValidator interface,
 * allowing the generic type of the validator to be retrieved and used for injecting the validator
 * for a specific class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Validates {
    Class<?> value();
}
