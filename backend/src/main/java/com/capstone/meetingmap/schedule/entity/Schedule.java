package com.capstone.meetingmap.schedule.entity;

import com.capstone.meetingmap.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends ScheduleTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleNo;
    private String scheduleName;

    @Column(columnDefinition = "TEXT") //VARCHAR가 아닌 TEXT로 매핑
    private String scheduleAbout;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleDetail> details;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) //회원아이디 외래키
    private User user;

    @Builder
    public Schedule(String scheduleName, String scheduleAbout, List<ScheduleDetail> details, User user) {
        this.scheduleName = scheduleName;
        this.scheduleAbout = scheduleAbout;
        this.details = details;
        this.user = user;
    }

    public void setScheduleWithoutUserId(String scheduleName, String scheduleAbout, List<ScheduleDetail> details) {
        this.scheduleName = scheduleName;
        this.scheduleAbout = scheduleAbout;

        this.details.clear();
        this.details.addAll(details);
    }

    public void addDetails(List<ScheduleDetail> details) {
        this.details.clear();
        for (ScheduleDetail detail : details) {
            this.details.add(detail);
            detail.setSchedule(this);
        }
    }
}