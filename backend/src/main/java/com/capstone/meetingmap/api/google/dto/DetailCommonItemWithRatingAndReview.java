package com.capstone.meetingmap.api.google.dto;

import com.capstone.meetingmap.api.tourapi.dto.DetailCommonItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DetailCommonItemWithRatingAndReview {
    private String contentId;
    private String contentTypeId;
    private String bookTour;
    private String createdTime;
    private String homepage;
    private String modifiedTime;
    private String tel;
    private String telName;
    private String title;
    private String firstImage;
    private String firstImage2;
    private String cpyrhtDivCd;
    private String areaCode;
    private String sigunguCode;
    private String addr1;
    private String addr2;
    private String zipcode;
    private String mapX;
    private String mapY;
    private String mLevel;
    private String overview;
    private Double rating;
    private Integer userRatingsTotal;
    private List<GooglePlaceDetailResult.Review> reviews;

    // tourAPI 세부 검색 결과에 rating, user_ratings_total 붙여서 반환
    public static DetailCommonItemWithRatingAndReview fromTourApiPlaceDetailResponse(DetailCommonItem detailCommonItem, RatingResponse ratingResponse, ReviewResponse reviewResponse) {
        return DetailCommonItemWithRatingAndReview.builder()
                .contentId(detailCommonItem.getContentid())
                .contentTypeId(detailCommonItem.getContenttypeid())
                .bookTour(detailCommonItem.getBooktour())
                .createdTime(detailCommonItem.getCreatedtime())
                .homepage(detailCommonItem.getHomepage())
                .modifiedTime(detailCommonItem.getModifiedtime())
                .tel(detailCommonItem.getTel())
                .telName(detailCommonItem.getTelname())
                .title(detailCommonItem.getTitle())
                .firstImage(detailCommonItem.getFirstimage())
                .firstImage2(detailCommonItem.getFirstimage2())
                .cpyrhtDivCd(detailCommonItem.getCpyrhtDivCd())
                .areaCode(detailCommonItem.getAreacode())
                .sigunguCode(detailCommonItem.getSigungucode())
                .addr1(detailCommonItem.getAddr1())
                .addr2(detailCommonItem.getAddr2())
                .zipcode(detailCommonItem.getZipcode())
                .mapX(detailCommonItem.getMapx())
                .mapY(detailCommonItem.getMapy())
                .mLevel(detailCommonItem.getMlevel())
                .overview(detailCommonItem.getOverview())
                .rating(ratingResponse.getRating())
                .userRatingsTotal(ratingResponse.getUserRatingsTotal())
                .reviews(reviewResponse.getReviews())
                .build();
    }
}
