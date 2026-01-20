package com.capstone.meetingmap.api.tmap.dto;

import lombok.Data;

import java.util.List;

@Data
public class RouteResponse {
    private String type;
    private List<Feature> features;
    private String usedFavoriteRouteVertices;

    @Data
    public static class Feature {
        private String type;
        private Geometry geometry;
        private Properties properties;
    }

    @Data
    public static class Geometry {
        private String type;
        private List<Object> coordinates; // LineString
    }

    @Data
    public static class Properties {
        // 공통 속성
        private Integer index;
        private String name;
        private String description;
        private String facilityName;

        // 자동차 전용
        private Integer totalFare;
        private Integer taxiFare;
        private String nextRoadName;

        // Point 전용
        private Integer totalDistance;
        private Integer totalTime;
        private Integer pointIndex;
        private String direction;
        private String nearPoiName;
        private String nearPoiX;
        private String nearPoiY;
        private String intersectionName;
        private Integer turnType;
        private String pointType;

        // LineString 전용
        private Integer lineIndex;
        private Integer distance;
        private Integer time;
        private Integer roadType;
        private Integer categoryRoadType;

        // 문자열 또는 정수 모두 허용
        private Object facilityType;
    }
}
