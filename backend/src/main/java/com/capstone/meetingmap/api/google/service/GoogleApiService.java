package com.capstone.meetingmap.api.google.service;

import com.capstone.meetingmap.api.google.dto.*;
import com.capstone.meetingmap.map.dto.DetailCommonResponseDto;
import com.capstone.meetingmap.map.dto.PlaceResponseDto;
import com.capstone.meetingmap.map.repository.PlaceCategoryDetailRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleApiService {
    private final RestClient googleRestClient;
    private final PlaceCategoryDetailRepository placeCategoryDetailRepository;

    public GoogleApiService(@Qualifier("googleRestClient") RestClient googleRestClient, PlaceCategoryDetailRepository placeCategoryDetailRepository) {
        this.googleRestClient = googleRestClient;
        this.placeCategoryDetailRepository = placeCategoryDetailRepository;
    }

    // 텍스트로 장소 검색 api 호출
    private GoogleApiResponse<GooglePlaceResult> getTextSearch(String placeName) {
        return googleRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/place/textsearch/json")
                        .queryParam("query", placeName)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    // 주변 지역 검색 api 호출
    private GoogleApiResponse<GooglePlaceResult> getNearBySearch(String longitude, String latitude, String searchType) {
        return googleRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/place/nearbysearch/json")
                        .queryParam("location", latitude + "," + longitude)
                        .queryParam("radius", "3000")
                        .queryParam("language", "ko")
                        .queryParam("type", searchType)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    // 장소 세부 검색 api 호출
    private GoogleApiDetailResponse<GooglePlaceDetailResult> getDetailSearch(String placeId) {
        return googleRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/place/details/json")
                        .queryParam("place_id", placeId)
                        .queryParam("language", "ko")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    // 장소로 이미지 검색 api 호출
    public String getPhoto(String photoReference, int maxWidth) {
        return googleRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/place/photo")
                        .queryParam("photoreference", photoReference)
                        .queryParam("maxwidth", maxWidth)
                        .build())
                .exchange((request, response) -> {
                    if (response.getStatusCode().is3xxRedirection()) {
                        return Mono.justOrEmpty(response.getHeaders().getFirst("Location"));
                    } else {
                        return Mono.<String>empty();
                    }
                })
                .block();
    }

    // 텍스트로 장소 검색해 평점 추출
    public RatingResponse searchPlaceWithRating(String placeName) {
        GoogleApiResponse<GooglePlaceResult> response = getTextSearch(placeName);
        if (response == null || response.getResults().isEmpty() || response.getResults().get(0).getRating() == null || response.getResults().get(0).getUser_ratings_total() == null)
            return RatingResponse.builder()
                    .rating(0)
                    .userRatingsTotal(0)
                    .build();
        return RatingResponse.fromGoogleApiResponse(response.getResults().get(0));
    }

    // 위치 기반 추가 검색
    public List<PlaceResponseDto> searchPlaceListByLocation(String longitude, String latitude, String searchType, int count) {
        GoogleApiResponse<GooglePlaceResult> response = getNearBySearch(longitude, latitude, searchType);
        if (response == null || response.getResults().isEmpty()) {
            return null;
        }
        return response.getResults()
                .stream()
                .filter(result -> result.getPhotos() != null && !result.getPhotos().isEmpty() &&
                        result.getRating() != null && result.getUser_ratings_total() > 0 &&
                        result.getVicinity() != null && result.getVicinity().contains("구")) // 이미지와 평점, "구"가 들어간 주소가 존재하는 경우만 가져오기
                .map(result -> PlaceResponseDto.fromGooglePlaceResultNearBySearch(
                        result,
                        placeCategoryDetailRepository.findBySearchType(searchType)
                                .orElseThrow(),
                        getPhoto(result.getPhotos().get(0).getPhoto_reference(), 800)
                ))
                .limit(count)  // 원하는 개수만큼 자르기
                .collect(Collectors.toList());
    }

    // 장소 세부 검색
    public DetailCommonResponseDto getGooglePlaceDetail(String contentId) {
        GoogleApiDetailResponse<GooglePlaceDetailResult> response = getDetailSearch(contentId);
        return DetailCommonResponseDto.fromGoogleApiPlace(response,
                response.getResult().getPhotos().stream()
                                .map(photo -> getPhoto(photo.getPhoto_reference(), 800))
                                        .collect(Collectors.toList()));
    }

    // 장소 세부 검색으로 리뷰 추출
    public ReviewResponse searchPlaceWithReview(String placeName) {
        GoogleApiResponse<GooglePlaceResult> textSearchResponse = getTextSearch(placeName);
        String contentId = textSearchResponse.getResults().get(0).getPlace_id();

        GoogleApiDetailResponse<GooglePlaceDetailResult> response = getDetailSearch(contentId);
        if (response == null || response.getResult() == null || response.getResult().getReviews().isEmpty())
            return ReviewResponse.builder()
                    .reviews(Collections.emptyList())
                    .build();
        return ReviewResponse.fromGoogleApiDetailResponse(response.getResult());
    }
}
