package com.capstone.meetingmap.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ScheduleDetailCreateDto {
    private String scheduleContent;
    private String scheduleAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime scheduleStartTime;
    private LocalDateTime scheduleEndTime;
}
