package com.capstone.meetingmap.schedule.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class TransportTypeValidator implements ConstraintValidator<ValidTransportType, String> {
    private static final Set<String> VALID_TYPES = Set.of("pedestrian", "car", "transit");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // null은 @NotNull로 따로 체크
        return VALID_TYPES.contains(value);
    }
}