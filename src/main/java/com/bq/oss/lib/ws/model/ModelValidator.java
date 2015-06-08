package com.bq.oss.lib.ws.model;

import javax.validation.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelValidator {
    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static Validator validator = factory.getValidator();

    public static void validateModelProperties(Object model) {
        int errors = 0;
        Set<ConstraintViolation<Object>> constraintViolations = null;

        for (String key : getFilledAndAnnotatedAttributes(model)) {
            constraintViolations = validator.validateProperty(model, key);

            if (constraintViolations.size() > 0) {
                errors++;
            }
        }

        if (errors > 0) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    private static Set<String> getFilledAndAnnotatedAttributes(Object model) {
        Class objectClass = model.getClass();
        return Arrays.stream(objectClass.getDeclaredFields())
                .map(field -> {
                    field.setAccessible(true);
                    return getFieldNameIfHasAnnotationAndValue(field, model);
                })
                .filter(s -> s != null)
                .collect(Collectors.toSet());
    }

    private static String getFieldNameIfHasAnnotationAndValue(Field field, Object model) {
        try {
            field.setAccessible(true);
            if (field.getDeclaredAnnotations().length > 0 && field.get(model) != null)
                return field.getName();
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}