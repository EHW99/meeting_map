package com.capstone.meetingmap.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ScheduleShareRequestDto {
    private Integer scheduleNo; // 공유할 스케줄 번호
}
