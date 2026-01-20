package com.capstone.meetingmap.map.dto;

import com.capstone.meetingmap.api.google.dto.DetailCommonItemWithRatingAndReview;
import com.capstone.meetingmap.api.google.dto.GoogleApiDetailResponse;
import com.capstone.meetingmap.api.google.dto.GooglePlaceDetailResult;
import com.capstone.meetingmap.api.tourapi.dto.DetailImageItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DetailCommonResponseDto {
    private String address;
    private String contentId;
    private List<String> thumbnails;
    private String latitude;
    private String longitude;
    private String name;
    private String rating;
    private String userRatingsTotal;
    private String phoneNumber;
    private String url;
    private List<Review> reviews;

    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Review {
        private String author;
        private String language;
        private String rating;
        private String relativeTimeDescription;
        private String text;
        private LocalDateTime time;

        public static Review fromReview(GooglePlaceDetailResult.Review review) {
            return Review.builder()
                    .author(review.getAuthor_name())
                    .language(review.getOriginal_language())
                    .rating(String.valueOf(review.getRating()))
                    .relativeTimeDescription(review.getRelative_time_description())
                    .text(review.getText())
                    .time(LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(review.getTime()),
                            ZoneId.of("Asia/Seoul") // 원하는 시간대 (예: 한국)
                    ))
                    .build();
        }
    }

    public static DetailCommonResponseDto fromTourApiPlace(DetailCommonItemWithRatingAndReview commonItem, List<DetailImageItem> imageItems) {
        return DetailCommonResponseDto.builder()
                .address((commonItem.getAddr1() + " " + commonItem.getAddr2()).trim())
                .contentId(commonItem.getContentId())
                .thumbnails(imageItems.stream().map(DetailImageItem::getOriginimgurl).collect(Collectors.toList()))
                .latitude(commonItem.getMapY())
                .longitude(commonItem.getMapX())
                .name(commonItem.getTitle())
                .rating(String.valueOf(commonItem.getRating()))
                .userRatingsTotal(String.valueOf(commonItem.getUserRatingsTotal()))
                .phoneNumber(commonItem.getTel())
                .url(commonItem.getHomepage())
                .reviews(commonItem.getReviews().stream().map(Review::fromReview).collect(Collectors.toList()))
                .build();
    }

    public static DetailCommonResponseDto fromGoogleApiPlace(GoogleApiDetailResponse<GooglePlaceDetailResult> response, List<String> imageUrls) {
        return DetailCommonResponseDto.builder()
                .address(response.getResult().getVicinity())
                .contentId("google_" + response.getResult().getPlace_id())
                .thumbnails(imageUrls)
                .latitude(String.valueOf(response.getResult().getGeometry().getLocation().getLat()))
                .longitude(String.valueOf(response.getResult().getGeometry().getLocation().getLng()))
                .name(response.getResult().getName())
                .rating(String.valueOf(response.getResult().getRating()))
                .userRatingsTotal(String.valueOf(response.getResult().getUser_ratings_total()))
                .phoneNumber(response.getResult().getFormatted_phone_number())
                .url(response.getResult().getWebsite())
                .reviews(response.getResult().getReviews().stream().map(Review::fromReview).collect(Collectors.toList()))
                .build();
    }
}
