package com.capstone.meetingmap.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ScheduleCreateResponseDto {
    private List<SelectedPlace> places;
    private List<ScheduleDetailCreateDto> schedules;
}
