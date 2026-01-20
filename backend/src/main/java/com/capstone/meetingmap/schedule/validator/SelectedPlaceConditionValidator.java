package com.capstone.meetingmap.schedule.validator;

import com.capstone.meetingmap.schedule.dto.ScheduleCreateRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class SelectedPlaceConditionValidator implements ConstraintValidator<ValidSelectedPlaceCondition, ScheduleCreateRequestDto> {

    @Override
    public boolean isValid(ScheduleCreateRequestDto dto, ConstraintValidatorContext context) {
        Boolean additional = dto.getAdditionalRecommendation();
        List<?> places = dto.getSelectedPlace();

        // 조건: 추가 추천이 false일 경우 selectedPlace가 null이 아니고 비어있지 않아야 함
        if (Boolean.FALSE.equals(additional)) {
            return places != null && !places.isEmpty();
        }

        // true거나 null이면 검사하지 않음
        return true;
    }
}