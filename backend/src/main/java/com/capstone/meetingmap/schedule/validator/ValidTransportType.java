package com.capstone.meetingmap.schedule.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TransportTypeValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTransportType {
    String message() default "이동 수단은 도보, 자동차, 대중교통 중 하나여야 합니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}