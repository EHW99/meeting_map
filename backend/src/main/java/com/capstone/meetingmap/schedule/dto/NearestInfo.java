package com.capstone.meetingmap.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NearestInfo {
    SelectedPlace nearest;
    Integer minDistance;
    Integer minTime;
}
