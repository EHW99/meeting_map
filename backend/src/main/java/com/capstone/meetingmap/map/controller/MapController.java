package com.capstone.meetingmap.map.controller;

import com.capstone.meetingmap.api.kakao.dto.PointCoord;
import com.capstone.meetingmap.api.kakao.dto.SearchKeywordResponse;
import com.capstone.meetingmap.api.kakao.dto.searchAddressByCoordinateResponse;
import com.capstone.meetingmap.api.kakao.dto.searchCoordinateByAddressResponse;
import com.capstone.meetingmap.api.kakao.service.KakaoApiService;
import com.capstone.meetingmap.map.dto.*;
import com.capstone.meetingmap.map.service.ConvexHullService;
import com.capstone.meetingmap.map.service.MapService;
import com.capstone.meetingmap.util.AddressUtil;
import com.capstone.meetingmap.util.ClampUtil;
import com.capstone.meetingmap.util.MiddlePointUtil;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/map")
public class MapController {

    private final ConvexHullService convexHullService;
    private final MapService mapService;
    private final KakaoApiService kakaoApiService;

    public MapController(ConvexHullService convexHullService, MapService mapService, KakaoApiService kakaoApiService) {
        this.convexHullService = convexHullService;
        this.mapService = mapService;
        this.kakaoApiService = kakaoApiService;
    }

    // 카테고리 코드 반환
    @GetMapping("/category")
    public ResponseEntity<?> getCategoryCodes(@RequestParam(value = "category", required = false) String category) {
        List<CodeResponseDto> codeResponseDtoList = mapService.getCategoryCodes(category);
        return ResponseEntity.ok(codeResponseDtoList);
    }

    // 지도 출력
    @GetMapping
    public ResponseEntity<?> getMap(
            @RequestParam(value = "search") String search, //장소 선택 방법
            @RequestParam(value = "sort") String sort, // 정렬 방법
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            @RequestParam(value = "name", required = false) List<String> name,
            @RequestParam(value = "category", required = false) String category
    ) {

        switch (search) {
            case "destination" -> { // 출발지-도착지 기반 검색이면 start, end 필요
                // start, end 각각 주소인지 여부 확인
                boolean startIsAddress = AddressUtil.isAddressQuery(start);
                boolean endIsAddress = AddressUtil.isAddressQuery(end);

                // start에 맞는 API 호출
                Object startResponse = startIsAddress
                        ? kakaoApiService.searchCoordinateByAddress(start)
                        : kakaoApiService.searchKeyword(start);

                // end에 맞는 API 호출
                Object endResponse = endIsAddress
                        ? kakaoApiService.searchCoordinateByAddress(end)
                        : kakaoApiService.searchKeyword(end);

                String lat, lon;
                if (endResponse instanceof searchCoordinateByAddressResponse) { // 주소 검색 결과이면
                    lat = ((searchCoordinateByAddressResponse) endResponse).getDocuments().get(0).getY();
                    lon = ((searchCoordinateByAddressResponse) endResponse).getDocuments().get(0).getX();
                } else {  // 장소명 검색 결과이면
                    lat = ((SearchKeywordResponse) endResponse).getDocuments().get(0).getY();
                    lon = ((SearchKeywordResponse) endResponse).getDocuments().get(0).getX();
                }

                // 장소 조회
                List<PlaceResponseDto> places = mapService.getAllPlaces(sort, lat, lon, category);

                // 응답 반환
                return ResponseEntity.ok(TwoPointsResponseDto.addDestinationResponse(startResponse, endResponse, places));
            }
            case "location" -> { // 현재 위치 기반 검색이면 latitude, longitude 필요
                String address = kakaoApiService.getAddressFromLocation(longitude, latitude);
                List<PlaceResponseDto> places = mapService.getAllPlaces(sort, String.valueOf(latitude), String.valueOf(longitude), category);
                return ResponseEntity.ok(PointResponseDto.addLocationResponse(address, latitude, longitude, places));
            }
            case "middle-point" -> { // 중간 위치 기반 검색이면 name 리스트 필요
                List<PointCoord> pointCoordList = new ArrayList<>();
                for (String placeName : name) {
                    PointCoord pointCoord = kakaoApiService.getPointCoord(placeName);
                    pointCoordList.add(pointCoord);
                }

                XYDto xyDto;
                if (name.size() == 2) { // 두 장소의 중간지점은 일반 좌표 평균 알고리즘 사용
                    xyDto = MiddlePointUtil.getMiddlePoint(pointCoordList);
                } else {
                    List<Coordinate> coordList = kakaoApiService.getCoordList(name);
                    Point middlePoint = convexHullService.calculateConvexHullCentroid(coordList);
                    Point adjustedMiddlePoint = ClampUtil.clampPoint(middlePoint);

                    xyDto = XYDto.buildXYDtoByGeometry(adjustedMiddlePoint, coordList);
                }
                searchAddressByCoordinateResponse response = kakaoApiService.searchAddressByCoordinate(xyDto.getMiddleX(), xyDto.getMiddleY());

                List<PlaceResponseDto> places = mapService.getAllPlaces(sort, String.valueOf(xyDto.getMiddleY()), String.valueOf(xyDto.getMiddleX()), category);

                return ResponseEntity.ok(MiddlePointResponseDto.addMiddlePointResponse(pointCoordList, response, xyDto, places));
            }
            default -> throw new IllegalStateException("올바르지 않은 검색 방법입니다");
        }
    }

    // 장소 세부 정보 조회
    @GetMapping("/detail")
    public ResponseEntity<DetailCommonResponseDto> getPlaceDetail(@RequestParam(value = "contentId") String contentId) {
        DetailCommonResponseDto detailCommonResponseDto = mapService.getPlaceDetail(contentId);
        return ResponseEntity.ok(detailCommonResponseDto);
    }

    // 장소명 자동완성
    @GetMapping("/autocomplete")
    public ResponseEntity<?> getAddressAutoComplete(@RequestParam(value = "name") String name) {
        List<AddressAutocompleteDto> addressAutocompleteDtoList = mapService.getAddressAutocomplete(name);
        return ResponseEntity.ok(addressAutocompleteDtoList);
    }
}
