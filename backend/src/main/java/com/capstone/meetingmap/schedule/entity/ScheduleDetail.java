package com.capstone.meetingmap.schedule.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleDetailNo;

    @Column(columnDefinition = "TEXT")
    private String scheduleContent;

    private String scheduleAddress;

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    private LocalDateTime scheduleStartTime;
    private LocalDateTime scheduleEndTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_no", nullable = false)
    private Schedule schedule;

    @Builder
    public ScheduleDetail(Integer scheduleDetailNo, String scheduleContent, String scheduleAddress, BigDecimal latitude, BigDecimal longitude, LocalDateTime scheduleStartTime, LocalDateTime scheduleEndTime, Schedule schedule) {
        this.scheduleDetailNo = scheduleDetailNo;
        this.scheduleContent = scheduleContent;
        this.scheduleAddress = scheduleAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.scheduleStartTime = scheduleStartTime;
        this.scheduleEndTime = scheduleEndTime;
        this.schedule = schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
