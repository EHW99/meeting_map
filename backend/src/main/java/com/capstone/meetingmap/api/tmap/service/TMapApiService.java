package com.capstone.meetingmap.api.tmap.service;

import com.capstone.meetingmap.api.tmap.dto.*;
import com.capstone.meetingmap.schedule.dto.NearestInfo;
import com.capstone.meetingmap.schedule.dto.SelectedPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class TMapApiService {
    private static final Logger log = LoggerFactory.getLogger(TMapApiService.class);
    private final RestClient tMapRestClient;

    public TMapApiService(@Qualifier("tMapRestClient") RestClient tMapRestClient) {
        this.tMapRestClient = tMapRestClient;
    }

    // 보행자 경로 안내 api 호출
    public RouteResponse getPedestrianRoutes(PedestrianRouteRequest request) {
        try {
            return tMapRestClient.post()
                    .uri("/tmap/routes/pedestrian")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.error("TMap 보행자 경로 API 클라이언트 오류: {}", res.getStatusCode());
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "경로 요청 오류: 좌표를 확인해주세요");
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("TMap 보행자 경로 API 서버 오류: {}", res.getStatusCode());
                        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "경로 서비스를 일시적으로 사용할 수 없습니다");
                    })
                    .body(RouteResponse.class);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("TMap 보행자 경로 API 호출 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "경로 조회 중 오류가 발생했습니다");
        }
    }

    // 자동차 경로 안내 api 호출
    public RouteResponse getCarRoutes(RouteRequest request) {
        try {
            return tMapRestClient.post()
                    .uri("/tmap/routes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.error("TMap 자동차 경로 API 클라이언트 오류: {}", res.getStatusCode());
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "경로 요청 오류: 좌표를 확인해주세요");
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("TMap 자동차 경로 API 서버 오류: {}", res.getStatusCode());
                        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "경로 서비스를 일시적으로 사용할 수 없습니다");
                    })
                    .body(RouteResponse.class);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("TMap 자동차 경로 API 호출 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "경로 조회 중 오류가 발생했습니다");
        }
    }

    // 대중교통 경로 안내 api 호출
    public TransitRouteResponse getTransitRoutes(RouteRequest request) {
        try {
            return tMapRestClient.post()
                    .uri("/transit/routes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.error("TMap 대중교통 경로 API 클라이언트 오류: {}", res.getStatusCode());
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대중교통 경로 요청 오류");
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("TMap 대중교통 경로 API 서버 오류: {}", res.getStatusCode());
                        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "대중교통 서비스를 일시적으로 사용할 수 없습니다");
                    })
                    .body(TransitRouteResponse.class);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("TMap 대중교통 경로 API 호출 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "대중교통 경로 조회 중 오류가 발생했습니다");
        }
    }

    // 대중교통 요약정보 안내 api 호출
    public SimpleTransitRouteResponse getSimpleTransitRoutes(RouteRequest request) {
        try {
            return tMapRestClient.post()
                    .uri("/transit/routes/sub")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.error("TMap 대중교통 요약 API 클라이언트 오류: {}", res.getStatusCode());
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대중교통 요약 요청 오류");
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("TMap 대중교통 요약 API 서버 오류: {}", res.getStatusCode());
                        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "대중교통 서비스를 일시적으로 사용할 수 없습니다");
                    })
                    .body(SimpleTransitRouteResponse.class);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("TMap 대중교통 요약 API 호출 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "대중교통 요약 조회 중 오류가 발생했습니다");
        }
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
