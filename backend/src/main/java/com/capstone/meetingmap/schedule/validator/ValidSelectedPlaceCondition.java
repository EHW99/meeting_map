package com.capstone.meetingmap.schedule.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SelectedPlaceConditionValidator.class)
public @interface ValidSelectedPlaceCondition {
    String message() default "추가 추천이 꺼져 있을 때는 장소를 1개 이상 선택해야 합니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
