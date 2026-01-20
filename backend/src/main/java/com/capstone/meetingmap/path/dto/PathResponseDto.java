package com.capstone.meetingmap.path.dto;

import com.capstone.meetingmap.api.kakao.dto.SearchKeywordResponse;
import com.capstone.meetingmap.api.kakao.dto.PointCoord;
import com.capstone.meetingmap.api.tmap.dto.RouteResponse;
import com.capstone.meetingmap.map.dto.XYDto;
import com.capstone.meetingmap.api.kakao.dto.searchCoordinateByAddressResponse;
import com.capstone.meetingmap.schedule.dto.ScheduleDetailCreateDto;
import com.capstone.meetingmap.util.ParseUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PathResponseDto {
    private Location origin;
    private Location destination;
    private Integer distance;
    private Integer time;
    private Integer fare;
    private List<List<Double>> coordinates;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Location {
        private String name;
        private double x; // longitude
        private double y; // latitude
    }

    // 스케줄로 경로 조회하는 함수로부터
    public static PathResponseDto fromRouteResponse(RouteResponse routeResponse, ScheduleDetailCreateDto first, ScheduleDetailCreateDto last, List<List<Double>> coordinates) {
        return PathResponseDto.builder()
                .origin(Location.builder()
                        .name(first.getScheduleContent().replaceFirst(" 방문$", ""))
                        .x(first.getLongitude().doubleValue())
                        .y(first.getLatitude().doubleValue())
                        .build())
                .destination(Location.builder()
                        .name(last.getScheduleContent().replaceFirst(" 방문$", ""))
                        .x(last.getLongitude().doubleValue())
                        .y(last.getLatitude().doubleValue())
                        .build())
                .distance(routeResponse.getFeatures().get(0).getProperties().getTotalDistance())
                .time(routeResponse.getFeatures().get(0).getProperties().getTotalTime() / 60)
                .fare(routeResponse.getFeatures().get(0).getProperties().getTaxiFare())
                .coordinates(coordinates)
                .build();
    }

    // 장소 이름으로 경로 조회하는 함수로부터
    public static PathResponseDto fromDocuments(String start, String end, RouteResponse routeResponse, Object first, Object last, List<List<Double>> coordinates) {
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

        return PathResponseDto.builder()
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
                .distance(routeResponse.getFeatures().get(0).getProperties().getTotalDistance())
                .time(routeResponse.getFeatures().get(0).getProperties().getTotalTime() / 60)
                .fare(routeResponse.getFeatures().get(0).getProperties().getTaxiFare())
                .coordinates(coordinates)
                .build();
    }

    public static PathResponseDto fromCoordinateAndXyDto(String name, RouteResponse routeResponse, PointCoord first, XYDto last, List<List<Double>> coordinates) {
        return PathResponseDto.builder()
                .origin(PathResponseDto.Location.builder()
                        .name(name)
                        .x(ParseUtil.parseDoubleSafe(first.getLon()))
                        .y(ParseUtil.parseDoubleSafe(first.getLat()))
                        .build())
                .destination(PathResponseDto.Location.builder()
                        .name("중간 지점")
                        .x(last.getMiddleX())
                        .y(last.getMiddleY())
                        .build())
                .distance(routeResponse.getFeatures().get(0).getProperties().getTotalDistance())
                .time(routeResponse.getFeatures().get(0).getProperties().getTotalTime() / 60)
                .fare(routeResponse.getFeatures().get(0).getProperties().getTaxiFare())
                .coordinates(coordinates)
                .build();
    }
}
