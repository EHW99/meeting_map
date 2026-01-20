package com.capstone.meetingmap.schedule.dto;

import com.capstone.meetingmap.schedule.entity.Schedule;
import com.capstone.meetingmap.schedule.entity.ScheduleDetail;
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
public class ScheduleDetailRequestDto {
    private String scheduleContent;
    private String scheduleAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime scheduleStartTime;
    private LocalDateTime scheduleEndTime;
    private Integer scheduleNo; // 외래키

    //dto를 엔티티로 변환
    public ScheduleDetail toEntity(Schedule schedule) {
        return ScheduleDetail.builder()
                .scheduleContent(scheduleContent)
                .scheduleAddress(scheduleAddress)
                .latitude(latitude)
                .longitude(longitude)
                .scheduleStartTime(scheduleStartTime)
                .scheduleEndTime(scheduleEndTime)
                .schedule(schedule)
                .build();
    }
}
