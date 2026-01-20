package com.capstone.meetingmap.api.google.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReviewResponse {
    private List<GooglePlaceDetailResult.Review> reviews;

    public static ReviewResponse fromGoogleApiDetailResponse(GooglePlaceDetailResult googlePlaceDetailResult) {
        return ReviewResponse.builder()
                .reviews(googlePlaceDetailResult.getReviews())
                .build();
    }
}
