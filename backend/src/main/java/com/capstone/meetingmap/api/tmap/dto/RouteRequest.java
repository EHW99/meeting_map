package com.capstone.meetingmap.api.tmap.dto;

import lombok.Builder;
import lombok.Data;

// 자동차 경로 안내 api 요청을 보낼 dto
@Data
@Builder
public class RouteRequest {
    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;
}
