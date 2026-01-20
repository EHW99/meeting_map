package com.capstone.meetingmap.api.tourapi.service;

import com.capstone.meetingmap.api.tourapi.dto.CommonTourApiResponse;
import com.capstone.meetingmap.api.tourapi.dto.DetailCommonItem;
import com.capstone.meetingmap.api.tourapi.dto.DetailImageItem;
import com.capstone.meetingmap.api.tourapi.dto.LocationBasedListItem;
import com.capstone.meetingmap.map.dto.CodeResponseDto;
import com.capstone.meetingmap.map.dto.TourApiPlaceResponse;
import com.capstone.meetingmap.map.service.PlaceCategoryDetailService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TourApiService {
    private final RestClient tourApiRestClient;
    private final ObjectMapper objectMapper;
    private final PlaceCategoryDetailService placeCategoryDetailService;

    public TourApiService(@Qualifier("tourApiRestClient") RestClient tourApiRestClient, ObjectMapper objectMapper, PlaceCategoryDetailService placeCategoryDetailService) {
        this.tourApiRestClient = tourApiRestClient;
        this.objectMapper = objectMapper;
        this.placeCategoryDetailService = placeCategoryDetailService;
    }

    // 지역코드조회 api 호출
    private <T> List<CodeResponseDto> searchCodes(
            ParameterizedTypeReference<CommonTourApiResponse<T>> responseType,
            Function<T, CodeResponseDto> mapper
    ) {
        CommonTourApiResponse<T> response = fetchFromApi(
                "/areaCode1",
                responseType,
                builder -> {
                    builder.queryParam("numOfRows", 40)
                            .queryParam("pageNo", 1)
                            .queryParam("areaCode", "1");
                }
        );
        return getResultDtoList(mapper, response);
    }

    // 위치기반관광정보조회 api 호출
    private <T> List<TourApiPlaceResponse> searchPlaces(
            String mapX,
            String mapY,
            String radius,
            String contentTypeId,
            int count,
            ParameterizedTypeReference<CommonTourApiResponse<T>> responseType,
            Function<T, TourApiPlaceResponse> mapper,
            String category
    ) {
        CommonTourApiResponse<T> response = fetchFromApi(
                "/locationBasedList1",
                responseType,
                builder -> {
                    builder.queryParam("numOfRows", count)
                            .queryParam("pageNo", 1)
                            .queryParam("listYN", "Y")
                            .queryParam("arrange", "Q");
                    if (mapX != null && !mapX.isBlank()) {
                        builder.queryParam("mapX", mapX); //x좌표 적용
                    }
                    if (mapY != null && !mapY.isBlank()) {
                        builder.queryParam("mapY", mapY); //y좌표 적용
                    }
                    if (radius != null && !radius.isBlank()) {
                        builder.queryParam("radius", radius); //반경 적용
                    }
                    if (contentTypeId != null && !contentTypeId.isBlank()) {
                        builder.queryParam("contentTypeId", contentTypeId); //타입 ID 적용(관광지: 12, 숙박: 32, 쇼핑: 38, 음식점: 39, ...)
                    }
                }
        );
        return getResultDtoList(mapper, response, category);
    }

    //시군구코드를 조회
    public List<CodeResponseDto> getRegionCodes() {
        return searchCodes(new ParameterizedTypeReference<>() {}, CodeResponseDto::fromCodeItem);
    }

    //좌표(중간위치 포함)/도착지별 장소 조회
    public List<TourApiPlaceResponse> getPlaceList(String x, String y, String contentTypeId, int count, String category) {
        //좌표 기반 장소 조회(3km 반경)
        List<TourApiPlaceResponse> places = searchPlaces(x, y, "3000", contentTypeId, count,
                new ParameterizedTypeReference<CommonTourApiResponse<LocationBasedListItem>>() {},
                item -> TourApiPlaceResponse.fromLocationBasedListItem(item,
                        placeCategoryDetailService.searchPlaceCategoryDetail(item.getContenttypeid(), item.getCat1(), item.getCat2(), item.getCat3())
                ), category);
        return places.stream()
                .filter(place -> !place.getCategory().equals("other")) //contentTypeId가 0일때 제거
                .collect(Collectors.toList());
    }

    //  TourAPI로 얻은 장소 상세 조회
    public DetailCommonItem getTourApiPlaceDetail(String contentId) {
        CommonTourApiResponse<DetailCommonItem> response = fetchFromApi(
                "/detailCommon1",
                new ParameterizedTypeReference<>() {},
                builder -> builder.queryParam("numOfRows", 10)
                        .queryParam("pageNo", 1)
                        .queryParam("contentId", contentId) //콘텐츠ID 적용
                        .queryParam("defaultYN", "Y")
                        .queryParam("firstImageYN", "Y")
                        .queryParam("addrinfoYN", "Y")
                        .queryParam("mapinfoYN", "Y")
        );
        if (response == null
                || response.getResponse() == null
                || response.getResponse().getBody() == null
                || response.getResponse().getBody().getItems() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "반환값을 찾을 수 없습니다");
        }

        return response.getResponse().getBody().getItems().getItem().get(0);
    }

    //  TourAPI로 얻은 장소 이미지 조회
    public List<DetailImageItem> getTourApiPlaceDetailImage(String contentId) {
        CommonTourApiResponse<DetailImageItem> response = fetchFromApi(
                "/detailImage1",
                new ParameterizedTypeReference<>() {},
                builder -> builder.queryParam("numOfRows", 10)
                        .queryParam("pageNo", 1)
                        .queryParam("contentId", contentId) //콘텐츠ID 적용
                        .queryParam("imageYN", "Y")
                        .queryParam("subImageYN", "Y")
        );
        if (response == null
                || response.getResponse() == null
                || response.getResponse().getBody() == null
                || response.getResponse().getBody().getItems() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "반환값을 찾을 수 없습니다");
        }

        return response.getResponse().getBody().getItems().getItem();
    }

    // TourAPI를 restClient로 호출
    private <T> CommonTourApiResponse<T> fetchFromApi(
            String path,
            ParameterizedTypeReference<CommonTourApiResponse<T>> responseType,
            Consumer<UriBuilder> uriBuilderConsumer
    ) {
        String rawJson = tourApiRestClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path(path);
                    uriBuilderConsumer.accept(builder);
                    return builder.build();
                })
                .retrieve()
                .body(String.class);
        return parseTourApiResponse(responseType, rawJson);
    }

    //lists=""일 경우 빈 객체로 변환(변경X)
    private <T> CommonTourApiResponse<T> parseTourApiResponse(ParameterizedTypeReference<CommonTourApiResponse<T>> responseType, String rawJson) {
        // 이후 ObjectMapper로 수동 파싱
        try {
            JsonNode root = objectMapper.readTree(rawJson);

            // items가 빈 문자열인지 체크
            JsonNode itemsNode = root.path("response").path("body").path("items");
            if (itemsNode.isTextual() && itemsNode.asText().isEmpty()) {
                // 빈 리스트를 가진 객체 생성해서 반환
                CommonTourApiResponse<T> emptyResponse = new CommonTourApiResponse<>();
                CommonTourApiResponse.Response<T> res = new CommonTourApiResponse.Response<>();
                CommonTourApiResponse.Body<T> body = new CommonTourApiResponse.Body<>();
                body.setItems(new CommonTourApiResponse.Items<>());
                body.getItems().setItem(Collections.emptyList());
                res.setBody(body);
                emptyResponse.setResponse(res);
                return emptyResponse;
            }
            // 정상 파싱
            JavaType javaType = objectMapper.getTypeFactory().constructType(responseType.getType());
            return objectMapper.readValue(rawJson, javaType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse TourAPI response", e);
        }
    }

    // CodeResponse 외의 Response 검증 및 dto로 매핑
    private <T> List<TourApiPlaceResponse> getResultDtoList(
            Function<T, TourApiPlaceResponse> mapper,
            CommonTourApiResponse<T> response,
            String category
    ) {
        if (response == null
                || response.getResponse() == null
                || response.getResponse().getBody() == null
                || response.getResponse().getBody().getItems() == null) {
            return Collections.emptyList();
        }
        return response.getResponse().getBody().getItems().getItem()
                .stream()
                .map(mapper)
                .filter(tourApiPlaceResponse -> {       // 필터링 조건 추가
                    // category가 null이 아니고 해당 조건을 만족하는지 체크
                    if (category != null && !tourApiPlaceResponse.getCategory().contains(category)) {
                        return false; // category가 일치하지 않으면 필터링
                    }
                    return true; // 조건을 만족하면 true
                })
                .collect(Collectors.toList());
    }

    // CodeResponse 검증 및 dto로 매핑
    private static <T, R> List<R> getResultDtoList(Function<T, R> mapper, CommonTourApiResponse<T> response) {
        if (response == null
                || response.getResponse() == null
                || response.getResponse().getBody() == null
                || response.getResponse().getBody().getItems() == null) {
            return Collections.emptyList();
        }

        return response.getResponse().getBody().getItems().getItem()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
}
