package com.capstone.meetingmap.api.google.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingResponse {
    private double rating;
    private int userRatingsTotal;

    public static RatingResponse fromGoogleApiResponse(GooglePlaceResult googlePlaceResult) {
        return RatingResponse.builder()
                .rating(googlePlaceResult.getRating())
                .userRatingsTotal(googlePlaceResult.getUser_ratings_total())
                .build();
    }
}
