package com.capstone.meetingmap.schedule.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
//생성 시간 관리를 위한 클래스
public class ScheduleTime {
    @CreatedDate
    private LocalDateTime scheduleCreatedDate; //게시글 작성일
}
