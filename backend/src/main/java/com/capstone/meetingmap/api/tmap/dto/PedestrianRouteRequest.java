package com.capstone.meetingmap.api.tmap.dto;

import lombok.Builder;
import lombok.Data;

// 보행자 경로 안내 api 요청을 보낼 dto
@Data
@Builder
public class PedestrianRouteRequest {
    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;
    private final String startName = "start";
    private final String endName = "end";
}
