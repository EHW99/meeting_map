package com.capstone.meetingmap.schedule.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = {"name", "latitude", "longitude"}) // 비교 기준 필드 명시
public class SelectedPlace {
    private String contentId;
    private String address;
    private String name;
    private String latitude;
    private String longitude;
    private String category;

    @NotNull(message = "머무는 시간은 반드시 필요합니다")
    private Integer stayMinutes;

    @Override
    public String toString() {
        return "SelectedPlace{name='" + name + "', lat=" + latitude + ", lng=" + longitude + "}";
    }
}
