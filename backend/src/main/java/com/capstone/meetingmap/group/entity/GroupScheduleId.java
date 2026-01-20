package com.capstone.meetingmap.group.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
//복합키 구현을 위한 별도의 PK 클래스
public class GroupScheduleId {
    private Integer groupNo;
    private Integer scheduleNo;

    public GroupScheduleId(Integer groupNo, Integer scheduleNo) {
        this.groupNo = groupNo;
        this.scheduleNo = scheduleNo;
    }
}
