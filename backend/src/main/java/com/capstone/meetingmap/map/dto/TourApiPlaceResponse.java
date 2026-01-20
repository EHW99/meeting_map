package com.capstone.meetingmap.map.dto;

import com.capstone.meetingmap.api.tourapi.dto.LocationBasedListItem;
import com.capstone.meetingmap.map.entity.PlaceCategoryDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TourApiPlaceResponse {
    private String address;
    private String contentId;
    private String category;
    private String thumbnail;
    private String latitude;
    private String longitude;
    private String name;

    public static TourApiPlaceResponse fromLocationBasedListItem(LocationBasedListItem item, PlaceCategoryDetail placeCategoryDetail) {
        return TourApiPlaceResponse.builder()
                .address((item.getAddr1() == null ? "" : item.getAddr1()) + " " + (item.getAddr2() == null ? "" : item.getAddr2()).trim())
                .contentId(item.getContentid())
                .category(placeCategoryDetail.getPlaceCategoryDetailCode())
                .thumbnail(item.getFirstimage())
                .latitude(item.getMapy())
                .longitude(item.getMapx())
                .name(item.getTitle())
                .build();
    }
}
