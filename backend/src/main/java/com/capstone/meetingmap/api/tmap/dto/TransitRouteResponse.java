package com.capstone.meetingmap.api.tmap.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TransitRouteResponse {
    private MetaData metaData;

    // 에러 받는 멤버 변수
    private ApiError result;

    @Data
    @Builder
    public static class ApiError {
        private String message;
        private Integer status;
    }

    @Data
    @Builder
    public static class MetaData {
        private RequestParameters requestParameters;
        private Plan plan;
    }
    @Data
    @Builder
    public static class RequestParameters {
        private Integer busCount;
        private Integer expressbusCount;
        private Integer subwayCount;
        private Integer airplaneCount;
        private String locale;
        private String endY;
        private String endX;
        private Integer wideareaRouteCount;
        private Integer subwayBusCount;
        private String startY;
        private String startX;
        private Integer ferryCount;
        private Integer trainCount;
        private String reqDttm;
    }

    @Data
    @Builder
    public static class Plan {
        private List<Itinerary> itineraries;
    }

    @Data
    @Builder
    public static class Itinerary {
        private Fare fare;
        private Integer totalTime;
        private List<Leg> legs;
        private Integer totalWalkTime;
        private Integer transferCount;
        private Integer totalDistance;
        private Integer pathType;
        private Integer totalWalkDistance;
    }

    @Data
    @Builder
    public static class Fare {
        private Regular regular;
    }

    @Data
    @Builder
    public static class Regular {
        private Integer totalFare;
        private Currency currency;
    }

    @Data
    @Builder
    public static class Currency {
        private String symbol;
        private String currency;
        private String currencyCode;
    }

    @Data
    @Builder
    public static class Leg {
        private String mode;
        private String routeColor; // 도보가 아닌 경우
        private Integer sectionTime;
        private String route; // 대중교통 이름
        private String routeId; // 대중교통 번호
        private Integer distance;
        private Integer service; // 운행 여부
        private Location start;
        private List<Lanes> Lane; // 노선 종류
        private PassStopList passStopList; // 대중교통 구간
        private Location end;
        private Integer type; // 이동수단별 노선코드
        private List<Step> steps; // 도보 상세정보
        private PassShape passShape;
    }

    @Data
    @Builder
    public static class Lanes { // 노선 종류
        private String routeColor;
        private String route;
        private String routeId;
        private String service;
        private Integer type;
    }

    @Data
    @Builder
    public static class PassStopList { // 정차역 목록
        private List<StationList> stationList;
    }

    @Data
    @Builder
    public static class StationList { // 정차역
        private String index;
        private String stationName;
        private String lon;
        private String lat;
        private String stationID;
    }

    @Data
    @Builder
    public static class Location {
        private String name;
        private Double lon;
        private Double lat;
    }

    @Data
    @Builder
    public static class Step {
        private String streetName;
        private Integer distance;
        private String description;
        private String linestring;
    }

    @Data
    @Builder
    public static class PassShape {
        private String linestring;
    }
}
