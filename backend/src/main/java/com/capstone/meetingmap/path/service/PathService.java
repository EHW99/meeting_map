package com.capstone.meetingmap.path.service;

import com.capstone.meetingmap.api.kakao.dto.PointCoord;
import com.capstone.meetingmap.api.kakao.service.KakaoApiService;
import com.capstone.meetingmap.api.tmap.dto.PedestrianRouteRequest;
import com.capstone.meetingmap.api.tmap.dto.RouteRequest;
import com.capstone.meetingmap.api.tmap.dto.RouteResponse;
import com.capstone.meetingmap.api.tmap.dto.TransitRouteResponse;
import com.capstone.meetingmap.api.tmap.service.TMapApiService;
import com.capstone.meetingmap.map.dto.XYDto;
import com.capstone.meetingmap.map.service.ConvexHullService;
import com.capstone.meetingmap.path.dto.PathResponseDto;
import com.capstone.meetingmap.path.dto.TransitPathResponseDto;
import com.capstone.meetingmap.schedule.dto.ScheduleDetailCreateDto;
import com.capstone.meetingmap.util.ClampUtil;
import com.capstone.meetingmap.util.MiddlePointUtil;
import com.capstone.meetingmap.util.ParseUtil;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {
    private final TMapApiService tMapApiService;
    private final KakaoApiService kakaoApiService;
    private final ConvexHullService convexHullService;

    public PathService(TMapApiService tMapApiService, KakaoApiService kakaoApiService, ConvexHullService convexHullService) {
        this.tMapApiService = tMapApiService;
        this.kakaoApiService = kakaoApiService;
        this.convexHullService = convexHullService;
    }

    // 보행자 경로 출력
    public List<PathResponseDto> getPedestrianPath(List<ScheduleDetailCreateDto> dtoList) {
        if (dtoList.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "경로를 그리기 위한 장소가 너무 적습니다");
        }

        List<PathResponseDto> pathResponseDtoList = new ArrayList<>();
        for (int i = 0; i < dtoList.size() - 1; i++) {
            RouteResponse routeResponse = tMapApiService.getPedestrianRoutes(PedestrianRouteRequest.builder()
                    .startX(dtoList.get(i).getLongitude().doubleValue())
                    .startY(dtoList.get(i).getLatitude().doubleValue())
                    .endX(dtoList.get(i + 1).getLongitude().doubleValue())
                    .endY(dtoList.get(i + 1).getLatitude().doubleValue())
                    .build());

            pathResponseDtoList.add(PathResponseDto.fromRouteResponse(routeResponse, dtoList.get(i), dtoList.get(i + 1), getCoordinates(routeResponse)));
        }
        return pathResponseDtoList;
    }

    // 장소 이름으로 도보 경로 출력
    public List<PathResponseDto> getPedestrianPathByName(String start, String end, List<String> names) {
        if (names != null && names.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "경로를 그리기 위한 장소가 너무 적습니다");
        } else if (start != null && end != null) { // 출발지-도착지 구조면

            PointCoord startPointCoord = kakaoApiService.getPointCoord(start);
            PointCoord endPointCoord = kakaoApiService.getPointCoord(end);

            List<PathResponseDto> pathResponseDtoList = new ArrayList<>();
            RouteResponse routeResponse = tMapApiService.getPedestrianRoutes(PedestrianRouteRequest.builder()
                    .startX(ParseUtil.parseDoubleSafe(startPointCoord.getLon()))
                    .startY(ParseUtil.parseDoubleSafe(startPointCoord.getLat()))
                    .endX(ParseUtil.parseDoubleSafe(endPointCoord.getLon()))
                    .endY(ParseUtil.parseDoubleSafe(endPointCoord.getLat()))
                    .build());
            pathResponseDtoList.add(PathResponseDto.fromDocuments(start, end, routeResponse, startPointCoord.getDoc(), endPointCoord.getDoc(), getCoordinates(routeResponse)));
            return pathResponseDtoList;
        } else {
            List<PointCoord> pointCoordList = new ArrayList<>();
            for (String placeName : names) {
                PointCoord pointCoord = kakaoApiService.getPointCoord(placeName);
                pointCoordList.add(pointCoord);
            }

            XYDto xyDto;
            if (names.size() == 2) { // 두 장소의 중간지점은 일반 좌표 평균 알고리즘 사용
                xyDto = MiddlePointUtil.getMiddlePoint(pointCoordList);
            } else {
                List<Coordinate> coordList = kakaoApiService.getCoordList(names);
                Point middlePoint = convexHullService.calculateConvexHullCentroid(coordList);
                Point adjustedMiddlePoint = ClampUtil.clampPoint(middlePoint);

                xyDto = XYDto.buildXYDtoByGeometry(adjustedMiddlePoint, coordList);
            }

            List<PathResponseDto> pathResponseDtoList = new ArrayList<>();
            int i = 0;
            for (PointCoord pointCoord : pointCoordList) {
                RouteResponse routeResponse = tMapApiService.getPedestrianRoutes(PedestrianRouteRequest.builder()
                        .startX(ParseUtil.parseDoubleSafe(String.valueOf(pointCoord.getLon())))
                        .startY(ParseUtil.parseDoubleSafe(String.valueOf(pointCoord.getLat())))
                        .endX(ParseUtil.parseDoubleSafe(String.valueOf(xyDto.getMiddleX())))
                        .endY(ParseUtil.parseDoubleSafe(String.valueOf(xyDto.getMiddleY())))
                        .build());

                pathResponseDtoList.add(PathResponseDto.fromCoordinateAndXyDto(names.get(i), routeResponse, pointCoord, xyDto, getCoordinates(routeResponse)));
                i++;
            }

            return pathResponseDtoList;
        }
    }

    // 자동차 경로 출력
    public List<PathResponseDto> getCarPath(List<ScheduleDetailCreateDto> dtoList) {
        if (dtoList.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "경로를 그리기 위한 장소가 너무 적습니다");
        }

        List<PathResponseDto> pathResponseDtoList = new ArrayList<>();
        for (int i = 0; i < dtoList.size() - 1; i++) {
            RouteResponse routeResponse = tMapApiService.getCarRoutes(RouteRequest.builder()
                    .startX(dtoList.get(i).getLongitude().doubleValue())
                    .startY(dtoList.get(i).getLatitude().doubleValue())
                    .endX(dtoList.get(i + 1).getLongitude().doubleValue())
                    .endY(dtoList.get(i + 1).getLatitude().doubleValue())
                    .build());

            pathResponseDtoList.add(PathResponseDto.fromRouteResponse(routeResponse, dtoList.get(i), dtoList.get(i + 1), getCoordinates(routeResponse)));
        }
        return pathResponseDtoList;
    }

    // 장소 이름으로 자동차 경로 출력
    public List<PathResponseDto> getCarPathByName(String start, String end, List<String> names) {
        if (names != null && names.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "경로를 그리기 위한 장소가 너무 적습니다");
        } else if (start != null && end != null) { // 출발지-도착지 구조면

            PointCoord startPointCoord = kakaoApiService.getPointCoord(start);
            PointCoord endPointCoord = kakaoApiService.getPointCoord(end);

            List<PathResponseDto> pathResponseDtoList = new ArrayList<>();
            RouteResponse routeResponse = tMapApiService.getCarRoutes(RouteRequest.builder()
                    .startX(ParseUtil.parseDoubleSafe(startPointCoord.getLon()))
                    .startY(ParseUtil.parseDoubleSafe(startPointCoord.getLat()))
                    .endX(ParseUtil.parseDoubleSafe(endPointCoord.getLon()))
                    .endY(ParseUtil.parseDoubleSafe(endPointCoord.getLat()))
                    .build());
            pathResponseDtoList.add(PathResponseDto.fromDocuments(start, end, routeResponse, startPointCoord.getDoc(), endPointCoord.getDoc(), getCoordinates(routeResponse)));
            return pathResponseDtoList;
        } else {
            List<PointCoord> pointCoordList = new ArrayList<>();
            for (String placeName : names) {
                PointCoord pointCoord = kakaoApiService.getPointCoord(placeName);
                pointCoordList.add(pointCoord);
            }

            XYDto xyDto;
            if (names.size() == 2) { // 두 장소의 중간지점은 일반 좌표 평균 알고리즘 사용
                xyDto = MiddlePointUtil.getMiddlePoint(pointCoordList);
            } else {
                List<Coordinate> coordList = kakaoApiService.getCoordList(names);
                Point middlePoint = convexHullService.calculateConvexHullCentroid(coordList);
                Point adjustedMiddlePoint = ClampUtil.clampPoint(middlePoint);

                xyDto = XYDto.buildXYDtoByGeometry(adjustedMiddlePoint, coordList);
            }

            List<PathResponseDto> pathResponseDtoList = new ArrayList<>();
            int i = 0;
            for (PointCoord pointCoord : pointCoordList) {
                RouteResponse routeResponse = tMapApiService.getCarRoutes(RouteRequest.builder()
                        .startX(ParseUtil.parseDoubleSafe(String.valueOf(pointCoord.getLon())))
                        .startY(ParseUtil.parseDoubleSafe(String.valueOf(pointCoord.getLat())))
                        .endX(ParseUtil.parseDoubleSafe(String.valueOf(xyDto.getMiddleX())))
                        .endY(ParseUtil.parseDoubleSafe(String.valueOf(xyDto.getMiddleY())))
                        .build());

                pathResponseDtoList.add(PathResponseDto.fromCoordinateAndXyDto(names.get(i), routeResponse, pointCoord, xyDto, getCoordinates(routeResponse)));
                i++;
            }
            return pathResponseDtoList;
        }
    }

    // 대중교통 경로 출력
    public List<TransitPathResponseDto> getTransitPath(List<ScheduleDetailCreateDto> dtoList) {
        if (dtoList.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "경로를 그리기 위한 장소가 너무 적습니다");
        }

        List<TransitPathResponseDto> pathResponseDtoList = new ArrayList<>();
        for (int i = 0; i < dtoList.size() - 1; i++) {
            TransitRouteResponse transitRouteResponse = tMapApiService.getTransitRoutes(RouteRequest.builder()
                    .startX(dtoList.get(i).getLongitude().doubleValue())
                    .startY(dtoList.get(i).getLatitude().doubleValue())
                    .endX(dtoList.get(i + 1).getLongitude().doubleValue())
                    .endY(dtoList.get(i + 1).getLatitude().doubleValue())
                    .build());

            // 에러가 있다면
            if (transitRouteResponse.getResult() != null) {
                pathResponseDtoList.add(TransitPathResponseDto.fromError(dtoList.get(i).getScheduleContent().replaceFirst(" 방문$", ""),
                        dtoList.get(i + 1).getScheduleContent().replaceFirst(" 방문$", ""),
                        dtoList.get(i), dtoList.get(i + 1), transitRouteResponse.getResult().getMessage()));
                continue;
            }

            pathResponseDtoList.add(TransitPathResponseDto.fromScheduleDetailCreateDto(dtoList.get(i).getScheduleContent().replaceFirst(" 방문$", ""),
                    dtoList.get(i + 1).getScheduleContent().replaceFirst(" 방문$", ""),
                    dtoList.get(i), dtoList.get(i + 1), TransitPathResponseDto.getPlan(transitRouteResponse)));
        }
        return pathResponseDtoList;
    }

    // 장소 이름으로 대중교통 경로 출력
    public List<TransitPathResponseDto> getTransitPathByName(String start, String end, List<String> names) {
        if (names != null && names.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "경로를 그리기 위한 장소가 너무 적습니다");
        } else if (start != null && end != null) {

            PointCoord startPointCoord = kakaoApiService.getPointCoord(start);
            PointCoord endPointCoord = kakaoApiService.getPointCoord(end);

            List<TransitPathResponseDto> pathResponseDtoList = new ArrayList<>();
            TransitRouteResponse transitRouteResponse = tMapApiService.getTransitRoutes(RouteRequest.builder()
                    .startX(ParseUtil.parseDoubleSafe(startPointCoord.getLon()))
                    .startY(ParseUtil.parseDoubleSafe(startPointCoord.getLat()))
                    .endX(ParseUtil.parseDoubleSafe(endPointCoord.getLon()))
                    .endY(ParseUtil.parseDoubleSafe(endPointCoord.getLat()))
                    .build());

            pathResponseDtoList.add(TransitPathResponseDto.fromDocuments(start, end, startPointCoord.getDoc(), endPointCoord.getDoc(), TransitPathResponseDto.getPlan(transitRouteResponse)));
            return pathResponseDtoList;
        } else {
            List<PointCoord> pointCoordList = new ArrayList<>();
            for (String placeName : names) {
                PointCoord pointCoord = kakaoApiService.getPointCoord(placeName);
                pointCoordList.add(pointCoord);
            }

            XYDto xyDto;
            if (names.size() == 2) { // 두 장소의 중간지점은 일반 좌표 평균 알고리즘 사용
                xyDto = MiddlePointUtil.getMiddlePoint(pointCoordList);
            } else {
                List<Coordinate> coordList = kakaoApiService.getCoordList(names);
                Point middlePoint = convexHullService.calculateConvexHullCentroid(coordList);
                Point adjustedMiddlePoint = ClampUtil.clampPoint(middlePoint);

                xyDto = XYDto.buildXYDtoByGeometry(adjustedMiddlePoint, coordList);
            }

            List<TransitPathResponseDto> pathResponseDtoList = new ArrayList<>();
            int i = 0;
            for (PointCoord pointCoord : pointCoordList) {
                TransitRouteResponse transitRouteResponse = tMapApiService.getTransitRoutes(RouteRequest.builder()
                        .startX(ParseUtil.parseDoubleSafe(String.valueOf(pointCoord.getLon())))
                        .startY(ParseUtil.parseDoubleSafe(String.valueOf(pointCoord.getLat())))
                        .endX(ParseUtil.parseDoubleSafe(String.valueOf(xyDto.getMiddleX())))
                        .endY(ParseUtil.parseDoubleSafe(String.valueOf(xyDto.getMiddleY())))
                        .build());

                pathResponseDtoList.add(TransitPathResponseDto.fromCoordinateAndXyDto(names.get(i), pointCoord, xyDto, TransitPathResponseDto.getPlan(transitRouteResponse)));
                i++;
            }
            return pathResponseDtoList;
        }
    }


    // 경로 좌표 변환
    public static List<List<Double>> getCoordinates(RouteResponse routeResponse) {
        return routeResponse.getFeatures().stream()
                .filter(feature -> "LineString".equals(feature.getGeometry().getType()))
                .flatMap(feature -> {
                    Object coordsObj = feature.getGeometry().getCoordinates();
                    List<List<Object>> coords = (List<List<Object>>) coordsObj;

                    return coords.stream()
                            .map(coord -> coord.stream()
                                    .map(o -> ((Number) o).doubleValue())
                                    .collect(Collectors.toList()));
                })
                .toList();
    }


}
