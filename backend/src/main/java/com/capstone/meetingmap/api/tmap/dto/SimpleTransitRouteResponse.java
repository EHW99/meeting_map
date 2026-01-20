package com.capstone.meetingmap.api.tmap.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SimpleTransitRouteResponse {
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
        private String endY;
        private String endX;
        private String startY;
        private String startX;
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
}
