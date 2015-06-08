package com.bq.oss.lib.ws.model;

import javax.validation.*;
import java.util.Set;

public class ModelValidator {
    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static Validator validator = factory.getValidator();

    public static void validateObject(Object model){
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(model);
        if (constraintViolations.size() > 0)
            throw new ConstraintViolationException(constraintViolations);
    }
}