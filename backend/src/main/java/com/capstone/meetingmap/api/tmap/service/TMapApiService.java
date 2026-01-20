package com.capstone.meetingmap.api.tmap.service;

import com.capstone.meetingmap.api.tmap.dto.*;
import com.capstone.meetingmap.schedule.dto.NearestInfo;
import com.capstone.meetingmap.schedule.dto.SelectedPlace;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class TMapApiService {
    private final RestClient tMapRestClient;

    public TMapApiService(@Qualifier("tMapRestClient") RestClient tMapRestClient) {
        this.tMapRestClient = tMapRestClient;
    }

    // 보행자 경로 안내 api 호출
    public RouteResponse getPedestrianRoutes(PedestrianRouteRequest request) {
        return tMapRestClient.post()
                .uri("/tmap/routes/pedestrian")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(RouteResponse.class);
    }

    // 자동차 경로 안내 api 호출
    public RouteResponse getCarRoutes(RouteRequest request) {
        return tMapRestClient.post()
                .uri("/tmap/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(RouteResponse.class);
    }

    // 대중교통 경로 안내 api 호출
    public TransitRouteResponse getTransitRoutes(RouteRequest request) {
        return tMapRestClient.post()
                .uri("/transit/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(TransitRouteResponse.class);
    }

    // 대중교통 요약정보 안내 api 호출
    public SimpleTransitRouteResponse getSimpleTransitRoutes(RouteRequest request) {
        return tMapRestClient.post()
                .uri("/transit/routes/sub")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(SimpleTransitRouteResponse.class);
    }

    // 최소거리의 장소와 거리, 시간 반환
    public NearestInfo findNearest(List<RouteResponse> routeResponseList, List<SelectedPlace> candidates) {
        return IntStream.range(0, routeResponseList.size())
                .boxed()
                .flatMap(i -> routeResponseList.get(i).getFeatures().stream()
                        .filter(f -> f.getProperties() != null && f.getProperties().getTotalDistance() != null)
                        .map(f -> new AbstractMap.SimpleEntry<>(i, f)))
                .min(Comparator.comparingInt(e -> e.getValue().getProperties().getTotalDistance()))
                .map(entry -> NearestInfo.builder()
                        .nearest(candidates.get(entry.getKey()))
                        .minDistance(entry.getValue().getProperties().getTotalDistance())
                        .minTime(entry.getValue().getProperties().getTotalTime() / 60)
                        .build())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "최소 거리의 장소를 찾지 못했습니다"));
    }

    // 최소거리의 장소와 거리, 시간 반환
    public NearestInfo findNearestTransit(List<SimpleTransitRouteResponse> routeResponseList, List<SelectedPlace> candidates) {
        return IntStream.range(0, routeResponseList.size())
                .boxed()
                .flatMap(i -> routeResponseList.get(i).getMetaData().getPlan().getItineraries().stream()
                        .map(f -> new AbstractMap.SimpleEntry<>(i, f)))
                .min(Comparator.comparingInt(e -> e.getValue().getTotalDistance()))
                .map(entry -> NearestInfo.builder()
                        .nearest(candidates.get(entry.getKey()))
                        .minDistance(entry.getValue().getTotalDistance())
                        .minTime(entry.getValue().getTotalTime() / 60)
                        .build())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "최소 거리의 장소를 찾지 못했습니다"));
    }


}
