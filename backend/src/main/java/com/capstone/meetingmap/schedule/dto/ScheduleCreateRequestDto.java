package com.capstone.meetingmap.schedule.dto;

import com.capstone.meetingmap.schedule.validator.ValidSelectedPlaceCondition;
import com.capstone.meetingmap.schedule.validator.ValidTransportType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ValidSelectedPlaceCondition
public class ScheduleCreateRequestDto {
    // additionalRecommendation가 false면 반드시 1개 이상 작성 필요
    private List<SelectedPlace> selectedPlace;

    @NotNull(message = "스케줄 시작 시간은 반드시 필요합니다")
    @FutureOrPresent(message = "스케줄 시작 시간은 현재 시간 이후만 가능합니다")
    private LocalDateTime scheduleStartTime;

    @NotNull(message = "스케줄 종료 시간은 반드시 필요합니다")
    @FutureOrPresent(message = "스케줄 종료 시간은 현재 시간 이후만 가능합니다")
    private LocalDateTime scheduleEndTime;

    @NotBlank(message = "이동 수단은 반드시 선택해야 합니다")
    @ValidTransportType
    private String transport;

    @NotNull(message = "추가 추천 여부는 반드시 필요합니다")
    private Boolean additionalRecommendation;

    private Boolean aiRecommendation;

    private Integer totalPlaceCount;
    private String theme;
    private Double minimumRating;
    private Integer stayMinutesMean;

    private PointCoordinate pointCoordinate;

    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class PointCoordinate {
        private String latitude;
        private String longitude;
    }
}
