package com.capstone.meetingmap.map.dto;

import com.capstone.meetingmap.api.google.dto.GooglePlaceResult;
import com.capstone.meetingmap.api.google.dto.RatingResponse;
import com.capstone.meetingmap.map.entity.PlaceCategoryDetail;
import com.capstone.meetingmap.schedule.dto.SelectedPlace;
import lombok.*;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class PlaceResponseDto {
    private String address;
    private String contentId;
    private String category;
    private String thumbnail;
    private String latitude;
    private String longitude;
    private String name;
    private String rating;
    private String userRatingsTotal;

    // Google Places API nearbySearch로 가져온 장소 정보를 TourAPI 장소 형태로 변환
    public static PlaceResponseDto fromGooglePlaceResultNearBySearch(GooglePlaceResult result, PlaceCategoryDetail placeCategoryDetail, String imageUrl) {
        return PlaceResponseDto.builder()
                .address(result.getVicinity())
                .contentId("google_" + result.getPlace_id())
                .category(placeCategoryDetail.getPlaceCategoryDetailCode())
                .thumbnail(imageUrl)
                .latitude(String.valueOf(result.getGeometry().getLocation().getLat()))
                .longitude(String.valueOf(result.getGeometry().getLocation().getLng()))
                .name(result.getName())
                .rating(String.valueOf(result.getRating()))
                .userRatingsTotal(String.valueOf(result.getUser_ratings_total()))
                .build();
    }

    // SelectedPlace로 변환
    public SelectedPlace toSelectedPlace(Integer stayMinutesMean) {
        return SelectedPlace.builder()
                .contentId(contentId)
                .address(address)
                .name(name)
                .latitude(latitude)
                .longitude(longitude)
                .category(category)
                .stayMinutes(stayMinutesMean)
                .build();
    }

    // tourAPI 결과에 rating, user_ratings_total 붙여서 반환
    public static PlaceResponseDto fromTourApiPlaceResponse(TourApiPlaceResponse tourApiPlaceResponse, RatingResponse ratingResponse) {
        return PlaceResponseDto.builder()
                .address(tourApiPlaceResponse.getAddress())
                .contentId(tourApiPlaceResponse.getContentId())
                .category(tourApiPlaceResponse.getCategory())
                .thumbnail(tourApiPlaceResponse.getThumbnail())
                .latitude(tourApiPlaceResponse.getLatitude())
                .longitude(tourApiPlaceResponse.getLongitude())
                .name(tourApiPlaceResponse.getName())
                .rating(String.valueOf(ratingResponse.getRating()))
                .userRatingsTotal(String.valueOf(ratingResponse.getUserRatingsTotal()))
                .build();
    }
}
