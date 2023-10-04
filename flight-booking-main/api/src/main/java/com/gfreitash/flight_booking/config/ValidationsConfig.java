package com.gfreitash.flight_booking.config;

import com.gfreitash.flight_booking.services.validations.SpecificationValidator;
import com.gfreitash.flight_booking.services.validations.Validates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidationsConfig {

    private final ApplicationContext applicationContext;

    /**
     * This method create a list of all the validators that are annotated with @Validates
     * and implements SpecificationValidator.
     */
    public <T> List<SpecificationValidator<T>> getSpecificationValidators(Class<T> targetClass) {
        List<SpecificationValidator<T>> specificationValidators = new ArrayList<>();
        Map<String, Object> validatorBeans = applicationContext.getBeansWithAnnotation(Validates.class);
        for (Object validatorBean : validatorBeans.values()) {
            Validates annotation = validatorBean.getClass().getAnnotation(Validates.class);
            Class<?> validateClass = annotation.value();

            try {
                if (SpecificationValidator.class.isAssignableFrom(validatorBean.getClass())) {
                    log.info("Found validator {} for class {}", validatorBean.getClass().getName(), validateClass.getName());
                    log.warn("Is " + validateClass.getName() + " the same as " + targetClass.getName() + "? " + validateClass.equals(targetClass));
                    if(validateClass.equals(targetClass))
                        specificationValidators.add((SpecificationValidator<T>) validatorBean);
                } else {
                    throw new ClassCastException("The validator " + validatorBean.getClass().getName()
                            + " does not implement SpecificationValidator<" + targetClass.getName() + ">");
                }
            } catch (ClassCastException e) {
                log.error(e.getMessage());
            }
        }
        return specificationValidators;
    }
}
