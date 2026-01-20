package com.capstone.meetingmap.path.dto;

import com.capstone.meetingmap.api.kakao.dto.SearchKeywordResponse;
import com.capstone.meetingmap.api.kakao.dto.PointCoord;
import com.capstone.meetingmap.api.tmap.dto.TransitRouteResponse;
import com.capstone.meetingmap.map.dto.XYDto;
import com.capstone.meetingmap.api.kakao.dto.searchCoordinateByAddressResponse;
import com.capstone.meetingmap.schedule.dto.ScheduleDetailCreateDto;
import com.capstone.meetingmap.util.ParseUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TransitPathResponseDto {
    private Location origin;
    private Location destination;
    private List<Plan> plan;

    private String message; // 에러 받는 멤버 변수

    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Location {
        private String name;
        private double x; // longitude
        private double y; // latitude
    }

    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Plan {
        private Integer totalDistance;
        private Integer totalTime;
        private Integer totalWalkTime;
        private Integer transferCount;
        private Integer fare;
        private Integer pathType;
        private List<Detail> detail;
    }

    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Detail {
        private String mode;
        private String timeline;
        private Integer distance;
        private Integer time;
        private String routeColor;
        private String routeStyle;
        private Location start;
        private Location end;
        private List<List<Double>> coordinates;
    }

    public static TransitPathResponseDto fromError(String start, String end, ScheduleDetailCreateDto first, ScheduleDetailCreateDto last, String errorMessage) {
        return TransitPathResponseDto.builder()
                .origin(Location.builder()
                        .name(start)
                        .x(first.getLongitude().doubleValue())
                        .y(first.getLatitude().doubleValue())
                        .build())
                .destination(Location.builder()
                        .name(end)
                        .x(last.getLongitude().doubleValue())
                        .y(last.getLatitude().doubleValue())
                        .build())
                .message(errorMessage)
                .build();
    }

    public static TransitPathResponseDto fromScheduleDetailCreateDto(String start, String end, ScheduleDetailCreateDto first, ScheduleDetailCreateDto last, List<Plan> plan) {
        return TransitPathResponseDto.builder()
                .origin(Location.builder()
                        .name(start)
                        .x(first.getLongitude().doubleValue())
                        .y(first.getLatitude().doubleValue())
                        .build())
                .destination(Location.builder()
                        .name(end)
                        .x(last.getLongitude().doubleValue())
                        .y(last.getLatitude().doubleValue())
                        .build())
                .plan(plan)
                .build();
    }

    public static TransitPathResponseDto fromDocuments(String start, String end, Object first, Object last, List<Plan> plan) {
        String firstX, firstY, lastX, lastY;
        if (first instanceof searchCoordinateByAddressResponse.Document) {
            firstX = ((searchCoordinateByAddressResponse.Document) first).getX();
            firstY = ((searchCoordinateByAddressResponse.Document) first).getY();
        } else {
            firstX = ((SearchKeywordResponse.Documents) first).getX();
            firstY = ((SearchKeywordResponse.Documents) first).getY();
        }
        if (last instanceof searchCoordinateByAddressResponse.Document) {
            lastX = ((searchCoordinateByAddressResponse.Document) last).getX();
            lastY = ((searchCoordinateByAddressResponse.Document) last).getY();
        } else {
            lastX = ((SearchKeywordResponse.Documents) last).getX();
            lastY = ((SearchKeywordResponse.Documents) last).getY();
        }

        return TransitPathResponseDto.builder()
                .origin(Location.builder()
                        .name(start)
                        .x(ParseUtil.parseDoubleSafe(firstX))
                        .y(ParseUtil.parseDoubleSafe(firstY))
                        .build())
                .destination(Location.builder()
                        .name(end)
                        .x(ParseUtil.parseDoubleSafe(lastX))
                        .y(ParseUtil.parseDoubleSafe(lastY))
                        .build())
                .plan(plan)
                .build();
    }

    public static TransitPathResponseDto fromCoordinateAndXyDto(String name, PointCoord first, XYDto last, List<Plan> plan) {
        return TransitPathResponseDto.builder()
                .origin(Location.builder()
                        .name(name)
                        .x(ParseUtil.parseDoubleSafe(first.getLon()))
                        .y(ParseUtil.parseDoubleSafe(first.getLat()))
                        .build())
                .destination(Location.builder()
                        .name("중간 지점")
                        .x(last.getMiddleX())
                        .y(last.getMiddleY())
                        .build())
                .plan(plan)
                .build();
    }

    // Plan 매핑
    public static List<TransitPathResponseDto.Plan> getPlan(TransitRouteResponse transitRouteResponse) {
        List<TransitPathResponseDto.Plan> planList = new ArrayList<>();

        for (TransitRouteResponse.Itinerary itinerary : transitRouteResponse.getMetaData().getPlan().getItineraries()) {
            planList.add(TransitPathResponseDto.Plan.builder()
                    .totalDistance(itinerary.getTotalDistance())
                    .totalTime(itinerary.getTotalTime() / 60)
                    .totalWalkTime(itinerary.getTotalWalkTime() / 60)
                    .transferCount(itinerary.getTransferCount())
                    .fare(itinerary.getFare().getRegular().getTotalFare())
                    .pathType(itinerary.getPathType())
                    .detail(getDetail(itinerary.getLegs()))
                    .build());
        }
        return planList;
    }

    // Leg에 해당하는 details 매핑
    public static List<TransitPathResponseDto.Detail> getDetail(List<TransitRouteResponse.Leg> details) {
        List<TransitPathResponseDto.Detail> detailList = new ArrayList<>();

        for (TransitRouteResponse.Leg detail : details) {
            String mode;
            StringBuilder timeline;
            String routeColor;
            String routeStyle;
            String coordinates;
            switch (detail.getMode()) {
                case "WALK" -> {
                    mode = "도보";
                    routeColor = "4D524C";
                    routeStyle = "dashed";
                    timeline = new StringBuilder(detail.getEnd().getName() + "까지 도보로 이동");

                    if (detail.getSteps() == null) { // 도보인데 steps가 아닌 passShape를 주는 경우
                        coordinates = detail.getPassShape().getLinestring();
                    } else {
                        StringBuilder linestring = new StringBuilder();
                        for (TransitRouteResponse.Step step : detail.getSteps()) {
                            if (linestring.toString().isEmpty()) {
                                linestring.append(step.getLinestring());
                            } else {
                                linestring.append(",").append(step.getLinestring());
                            }
                        }
                        coordinates = linestring.toString();
                    }
                }
                case "SUBWAY" -> {
                    mode = "지하철";
                    routeColor = detail.getRouteColor();
                    routeStyle = "solid";
                    timeline = new StringBuilder(detail.getStart().getName() + " 승차 - " + detail.getRoute() + " - " + detail.getEnd().getName() + " 하차");
                    coordinates = detail.getPassShape().getLinestring();
                }
                case "BUS" -> {
                    mode = "버스";
                    routeColor = detail.getRouteColor();
                    routeStyle = "solid";
                    if (detail.getLane() != null) { // 여러 노선이면
                        timeline = new StringBuilder(detail.getStart().getName() + " 승차 - ");
                        for (TransitRouteResponse.Lanes lane : detail.getLane()) {
                            timeline.append(lane.getRoute()).append(" - ");
                        }
                        timeline.append(detail.getEnd().getName()).append(" 하차");
                    } else {
                        timeline = new StringBuilder(detail.getStart().getName() + " 승차 - " + detail.getRoute() + " - " + detail.getEnd().getName() + " 하차");
                    }
                    coordinates = detail.getPassShape().getLinestring();
                }
                default -> {
                    continue;
                }
            }
            detailList.add(TransitPathResponseDto.Detail.builder()
                    .mode(mode)
                    .timeline(timeline.toString())
                    .distance(detail.getDistance())
                    .time(detail.getSectionTime() / 60)
                    .routeColor(routeColor)
                    .routeStyle(routeStyle)
                    .start(Location.builder()
                            .name(detail.getStart().getName())
                            .x(detail.getStart().getLon())
                            .y(detail.getStart().getLat())
                            .build())
                    .end(Location.builder()
                            .name(detail.getEnd().getName())
                            .x(detail.getEnd().getLon())
                            .y(detail.getEnd().getLat())
                            .build())
                    .coordinates(getCoordinates(coordinates))
                    .build());
        }
        return detailList;
    }

    // 문자열을 List<List<Double>>로 변환
    public static List<List<Double>> getCoordinates(String linestring) {
        // 공백으로 자르고 각 쌍을 리스트로 변환
        List<List<Double>> coordinates = new ArrayList<>();
        for (String pair : linestring.trim().split(" ")) {
            String[] parts = pair.split(",");
            if (parts.length == 2) {
                double lon = Double.parseDouble(parts[0]);
                double lat = Double.parseDouble(parts[1]);
                coordinates.add(Arrays.asList(lon, lat));
            }
        }
        return coordinates;
    }


}
