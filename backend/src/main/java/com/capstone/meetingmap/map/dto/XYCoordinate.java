package com.capstone.meetingmap.map.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class XYCoordinate {
    private String x;
    private String y;

    @Builder
    public XYCoordinate(String x, String y) {
        this.x = x;
        this.y = y;
    }
}
