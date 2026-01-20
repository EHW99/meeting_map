package com.capstone.meetingmap.schedule.dto;

import com.capstone.meetingmap.schedule.entity.ScheduleDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ScheduleDetailResponseDto {
    private Integer scheduleDetailNo;
    private String scheduleContent;
    private String scheduleAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime scheduleStartTime;
    private LocalDateTime scheduleEndTime;
    private Integer scheduleNo; // 외래키

    @Builder
    public ScheduleDetailResponseDto(Integer scheduleDetailNo, String scheduleContent, String scheduleAddress, BigDecimal latitude, BigDecimal longitude, LocalDateTime scheduleStartTime, LocalDateTime scheduleEndTime, Integer scheduleNo) {
        this.scheduleDetailNo = scheduleDetailNo;
        this.scheduleContent = scheduleContent;
        this.scheduleAddress = scheduleAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.scheduleStartTime = scheduleStartTime;
        this.scheduleEndTime = scheduleEndTime;
        this.scheduleNo = scheduleNo;
    }

    //엔티티를 dto로 변환
    public static ScheduleDetailResponseDto fromEntity(ScheduleDetail scheduleDetail) {
        return ScheduleDetailResponseDto.builder()
                .scheduleDetailNo(scheduleDetail.getScheduleDetailNo())
                .scheduleContent(scheduleDetail.getScheduleContent())
                .scheduleAddress(scheduleDetail.getScheduleAddress())
                .latitude(scheduleDetail.getLatitude())
                .longitude(scheduleDetail.getLongitude())
                .scheduleStartTime(scheduleDetail.getScheduleStartTime())
                .scheduleEndTime(scheduleDetail.getScheduleEndTime())
                .scheduleNo(scheduleDetail.getSchedule().getScheduleNo())
                .build();
    }
}
