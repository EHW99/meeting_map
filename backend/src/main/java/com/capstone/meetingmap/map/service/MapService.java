package com.capstone.meetingmap.map.service;

import com.capstone.meetingmap.api.google.dto.DetailCommonItemWithRatingAndReview;
import com.capstone.meetingmap.api.google.dto.RatingResponse;
import com.capstone.meetingmap.api.google.dto.ReviewResponse;
import com.capstone.meetingmap.api.google.service.GoogleApiService;
import com.capstone.meetingmap.api.kakao.dto.SearchKeywordResponse;
import com.capstone.meetingmap.api.kakao.service.KakaoApiService;
import com.capstone.meetingmap.api.tourapi.dto.DetailCommonItem;
import com.capstone.meetingmap.api.tourapi.dto.DetailImageItem;
import com.capstone.meetingmap.api.tourapi.service.TourApiService;
import com.capstone.meetingmap.map.dto.*;
import com.capstone.meetingmap.map.entity.PlaceCategory;
import com.capstone.meetingmap.map.entity.PlaceCategoryDetail;
import com.capstone.meetingmap.map.repository.PlaceCategoryDetailRepository;
import com.capstone.meetingmap.map.repository.PlaceCategoryRepository;
import com.capstone.meetingmap.util.ParseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapService {

    private final KakaoApiService kakaoApiService;
    private final PlaceCategoryDetailRepository placeCategoryDetailRepository;
    private final PlaceCategoryRepository placeCategoryRepository;
    private final GoogleApiService googleApiService;
    private final TourApiService tourApiService;

    public MapService(KakaoApiService kakaoApiService, PlaceCategoryDetailRepository placeCategoryDetailRepository, PlaceCategoryRepository placeCategoryRepository, GoogleApiService googleApiService, TourApiService tourApiService) {
        this.kakaoApiService = kakaoApiService;
        this.placeCategoryDetailRepository = placeCategoryDetailRepository;
        this.placeCategoryRepository = placeCategoryRepository;
        this.googleApiService = googleApiService;
        this.tourApiService = tourApiService;
    }

    // 카테고리를 반환
    public List<CodeResponseDto> getCategoryCodes(String category) {
        if (category == null || category.isEmpty()) {
            List<PlaceCategory> placeCategoryList = placeCategoryRepository.findByPlaceCategoryCodeNot("other");
            return placeCategoryList.stream()
                    .map(CodeResponseDto::fromPlaceCategory)
                    .collect(Collectors.toList());
        } else {
            List<PlaceCategoryDetail> placeCategoryDetailList = placeCategoryDetailRepository.findByPlaceCategoryDetailCodeStartingWith(category + "-");
            return placeCategoryDetailList.stream()
                    .map(CodeResponseDto::fromPlaceCategoryDetail)
                    .collect(Collectors.toList());
        }
    }

    // TourAPI 결과와 Google Places API 결과를 합쳐서 출력
    public List<PlaceResponseDto> getAllPlaces(String sort, String latitude, String longitude, String category) {
        List<PlaceCategoryDetail> placeCategoryDetailList = new ArrayList<>();
        List<PlaceResponseDto> mergedList = new ArrayList<>();
        int count;
        // 카테고리를 선택한 경우
        if (category != null) {
            // 매핑되는 코드 가져오기
            PlaceCategoryDetail detail = placeCategoryDetailRepository.findByPlaceCategoryDetailCode(category)
                    .orElse(null);
            if (detail == null) {
                placeCategoryDetailList.addAll(placeCategoryDetailRepository.findByPlaceCategoryDetailCodeStartingWith(category + "-"));
                count = 15; // 카테고리 검색은 15개정도
            } else {
                placeCategoryDetailList.add(detail);
                if (detail.getPlaceCategoryDetailCode().equals("food-korean")) {
                    count = 10; // 세부 카테고리 검색은 한식은 10개만
                } else {
                    count = 30; // 나머지는 30개 정도
                }
            }

            for (PlaceCategoryDetail categoryDetail : placeCategoryDetailList) {

                if (categoryDetail.getContentTypeId() != null) {
                    // TourAPI로 장소 검색
                    List<TourApiPlaceResponse> tourApiPlaceList = tourApiService.getPlaceList(longitude, latitude, categoryDetail.getContentTypeId(), count, categoryDetail.getPlaceCategoryDetailCode());
                    // Google Places API로 장소에 대해 평점 붙이기
                    if (!tourApiPlaceList.isEmpty()) {
                        List<PlaceResponseDto> placeListWithRating = getPlaceListWithRating(tourApiPlaceList);
                        mergedList.addAll(placeListWithRating);
                    }
                }

                // 추가 검색 조건
                boolean isRequiredAdditionalSearch = categoryDetail.getAdditionalSearch();

                // 추가 검색 조건에 속한다면 Google Places API의 nearbysearch로 5개 추가 검색
                if (isRequiredAdditionalSearch) {
                    List<PlaceResponseDto> additionalPlace = googleApiService.searchPlaceListByLocation(longitude, latitude, categoryDetail.getSearchType(), 5);
                    mergedList.addAll(additionalPlace);
                }
            }
        } else { // 전체 검색을 선택한 경우
            // TourAPI로 10개 장소 검색
            List<TourApiPlaceResponse> tourApiPlaceList = tourApiService.getPlaceList(longitude, latitude, null, 10, null);
            // Google Places API로 10개 장소에 대해 평점 붙이기
            if (!tourApiPlaceList.isEmpty()) {
                List<PlaceResponseDto> placeListWithRating = getPlaceListWithRating(tourApiPlaceList);
                mergedList.addAll(placeListWithRating);
            }

            // Google Places API의 nearbysearch로 2개씩 추가 검색
            for (String searchType : placeCategoryDetailRepository.findDistinctSearchTypes()) {
                List<PlaceResponseDto> additionalPlace = googleApiService.searchPlaceListByLocation(longitude, latitude, searchType, 2);
                mergedList.addAll(additionalPlace);
            }
        }

        switch (sort) {
            case "title_dsc":
                mergedList.sort(Comparator.comparing(PlaceResponseDto::getName).reversed());
                break;
            case "rating_asc":
                mergedList.sort(Comparator.comparing(dto -> ParseUtil.parseDoubleSafe(dto.getRating())));
                break;
            case "rating_dsc":
                mergedList.sort(Comparator.comparing((PlaceResponseDto dto) -> ParseUtil.parseDoubleSafe(dto.getRating())).reversed());
                break;
            case "user_ratings_total_asc":
                mergedList.sort(Comparator.comparing(dto -> ParseUtil.parseIntSafe(dto.getUserRatingsTotal())));
                break;
            case "user_ratings_total_dsc":
                mergedList.sort(Comparator.comparing((PlaceResponseDto dto) -> ParseUtil.parseIntSafe(dto.getUserRatingsTotal())).reversed());
                break;
            case "title_asc":
            default:
                mergedList.sort(Comparator.comparing(PlaceResponseDto::getName));
        }

        return mergedList;
    }

    // 장소 세부 조회
    public DetailCommonResponseDto getPlaceDetail(String contentId) {
        if (contentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 장소를 찾지 못했습니다");
        }
        if (contentId.startsWith("google_")) { // 구글로 검색된 장소면
            String trimmedContentId = contentId.substring("google_".length());
            return googleApiService.getGooglePlaceDetail(trimmedContentId);
        } else {
            DetailCommonItem commonItem = tourApiService.getTourApiPlaceDetail(contentId);
            List<DetailImageItem> imageItems = new ArrayList<>(tourApiService.getTourApiPlaceDetailImage(contentId)); // 가변 리스트로

            DetailImageItem detailImageItem = DetailImageItem.builder()
                    .contentid(commonItem.getContentid())
                    .imgname("")
                    .originimgurl(commonItem.getFirstimage())
                    .serialnum("")
                    .cpyrhtDivCd(commonItem.getCpyrhtDivCd())
                    .smallimageurl(commonItem.getFirstimage2())
                    .build();

            imageItems.add(0, detailImageItem);

            RatingResponse ratingResponse = googleApiService.searchPlaceWithRating(commonItem.getTitle());
            ReviewResponse reviewResponse = googleApiService.searchPlaceWithReview(commonItem.getTitle());
            DetailCommonItemWithRatingAndReview itemWithRatingAndReview = DetailCommonItemWithRatingAndReview.fromTourApiPlaceDetailResponse(commonItem, ratingResponse, reviewResponse);
            return DetailCommonResponseDto.fromTourApiPlace(itemWithRatingAndReview, imageItems);
        }
    }

    // 장소명 자동완성
    public List<AddressAutocompleteDto> getAddressAutocomplete(String keyword) {
        SearchKeywordResponse response = kakaoApiService.searchKeyword(keyword);
        return AddressAutocompleteDto.fromKakaoApiResponse(response);
    }

    // tourAPI 결과에 rating, user_ratings_total 붙여서 반환
    private List<PlaceResponseDto> getPlaceListWithRating(List<TourApiPlaceResponse> tourApiPlaceList) {
        return tourApiPlaceList.stream()
                .map(place -> {
                    RatingResponse ratingResponse = googleApiService.searchPlaceWithRating(place.getName());
                    return PlaceResponseDto.fromTourApiPlaceResponse(place, ratingResponse);
                })
                .collect(Collectors.toList());
    }
}
