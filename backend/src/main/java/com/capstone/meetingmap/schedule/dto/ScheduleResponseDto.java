package com.capstone.meetingmap.schedule.dto;

import com.capstone.meetingmap.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ScheduleResponseDto {
    private Integer scheduleNo;
    private String scheduleName;
    private String scheduleAbout;
    private LocalDateTime scheduleCreatedDate;
    private String userId;
    private List<ScheduleDetailResponseDto> details;

    //엔티티를 dto로 변환
    public static ScheduleResponseDto fromEntity(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .scheduleNo(schedule.getScheduleNo())
                .scheduleName(schedule.getScheduleName())
                .scheduleAbout(schedule.getScheduleAbout())
                .scheduleCreatedDate(schedule.getScheduleCreatedDate())
                .userId(schedule.getUser().getUserId())
                .details(schedule.getDetails().stream().map(ScheduleDetailResponseDto::fromEntity).collect(Collectors.toList()))
                .build();
    }
}
