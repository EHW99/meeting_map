package com.capstone.meetingmap.schedule.dto;

import com.capstone.meetingmap.schedule.entity.Schedule;
import com.capstone.meetingmap.schedule.entity.ScheduleDetail;
import com.capstone.meetingmap.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ScheduleSaveRequestDto {
    @NotBlank(message = "스케줄 이름은 반드시 입력해야 합니다")
    private String scheduleName;

    @NotBlank(message = "스케줄 설명은 반드시 입력해야 합니다")
    private String scheduleAbout;
    private List<ScheduleDetailRequestDto> details;

    @Builder
    public ScheduleSaveRequestDto(String scheduleName, String scheduleAbout, List<ScheduleDetailRequestDto> details) {
        this.scheduleName = scheduleName;
        this.scheduleAbout = scheduleAbout;
        this.details = details;
    }

    //dto를 엔티티로 변환
    public Schedule toEntity(ScheduleSaveRequestDto scheduleSaveRequestDto, User user) {
        Schedule schedule = Schedule.builder()
                .scheduleName(scheduleSaveRequestDto.getScheduleName())
                .scheduleAbout(scheduleSaveRequestDto.getScheduleAbout())
                .user(user)
                .build();

        List<ScheduleDetail> detailList = scheduleSaveRequestDto.getDetails().stream()
                .map(detailDto -> detailDto.toEntity(schedule))
                .collect(Collectors.toList());

        schedule.addDetails(detailList);

        return schedule;
    }
}
