package com.capstone.meetingmap.map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

//프론트에 locationBasedList와 위치를 전달할 dto
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PointResponseDto<T> {
    private CustomPoint current;
    private T list;

    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class CustomPoint {
        private String address;
        private String latitude;
        private String longitude;
    }

    // 기존 place dto에 지점 dto를 붙여줌
    public static PointResponseDto<List<PlaceResponseDto>> addLocationResponse(String address, Double latitude, Double longitude, List<PlaceResponseDto> placeDto) {
        return PointResponseDto.<List<PlaceResponseDto>>builder()
                .current(CustomPoint.builder()
                        .address(address)
                        .latitude(String.valueOf(latitude))
                        .longitude(String.valueOf(longitude))
                        .build())
                .list(placeDto)
                .build();
    }
}
