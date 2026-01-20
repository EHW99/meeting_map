package com.capstone.meetingmap.group.entity;

import com.capstone.meetingmap.schedule.entity.Schedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupSchedule {

    @EmbeddedId
    private GroupScheduleId groupScheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("groupNo")  // 복합키 클래스(GroupScheduleId)의 scheduleNo와 연결
    @JoinColumn(name = "group_no", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("scheduleNo")  // 복합키 클래스(GroupScheduleId)의 scheduleNo와 연결
    @JoinColumn(name = "schedule_no", nullable = false)
    private Schedule schedule;

    @Builder
    public GroupSchedule(Group group, Schedule schedule) {
        this.groupScheduleId = new GroupScheduleId(group.getGroupNo(), schedule.getScheduleNo());
        this.group = group;
        this.schedule = schedule;
    }
}
