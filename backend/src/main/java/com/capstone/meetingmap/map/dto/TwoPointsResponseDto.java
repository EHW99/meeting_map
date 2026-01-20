package com.capstone.meetingmap.map.dto;

import com.capstone.meetingmap.api.kakao.dto.SearchKeywordResponse;
import com.capstone.meetingmap.api.kakao.dto.searchCoordinateByAddressResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

//프론트에 locationBasedList와 각 출발지, 도착지를 전달할 dto
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TwoPointsResponseDto<T> {
    private CustomPoint start;
    private CustomPoint end;
    private T list;

    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class CustomPoint {
        private String name;
        private String address;
        private String latitude;
        private String longitude;
    }

    // 기존 place dto에 지점 dto를 붙여줌
    public static TwoPointsResponseDto<List<PlaceResponseDto>> addDestinationResponse(Object startResponse, Object endResponse, List<PlaceResponseDto> placeDto) {
        String startName, startAddress, startLatitude, startLongitude;
        String endName, endAddress, endLatitude, endLongitude;
        if (startResponse instanceof searchCoordinateByAddressResponse) {
            searchCoordinateByAddressResponse.Document startDoc = ((searchCoordinateByAddressResponse) startResponse).getDocuments().get(0);
            startName = (startDoc.getRoad_address().getAddress_name() != null ? startDoc.getRoad_address().getAddress_name() : startDoc.getAddress_name());
            startAddress = (startDoc.getRoad_address().getAddress_name() != null ? startDoc.getRoad_address().getAddress_name() : startDoc.getAddress().getAddress_name());
            startLatitude = ((searchCoordinateByAddressResponse) startResponse).getDocuments().get(0).getY();
            startLongitude = ((searchCoordinateByAddressResponse) startResponse).getDocuments().get(0).getX();
        } else {
            startName = ((SearchKeywordResponse) startResponse).getDocuments().get(0).getPlace_name();
            startAddress = ((SearchKeywordResponse) startResponse).getDocuments().get(0).getRoad_address_name();
            startLatitude = ((SearchKeywordResponse) startResponse).getDocuments().get(0).getY();
            startLongitude = ((SearchKeywordResponse) startResponse).getDocuments().get(0).getX();
        }
        if (endResponse instanceof searchCoordinateByAddressResponse) {
            searchCoordinateByAddressResponse.Document endDoc = ((searchCoordinateByAddressResponse) endResponse).getDocuments().get(0);
            endName = (endDoc.getRoad_address().getAddress_name() != null ? endDoc.getRoad_address().getAddress_name() : endDoc.getAddress_name());
            endAddress = (endDoc.getRoad_address().getAddress_name() != null ? endDoc.getRoad_address().getAddress_name() : endDoc.getAddress().getAddress_name());
            endLatitude = ((searchCoordinateByAddressResponse) endResponse).getDocuments().get(0).getY();
            endLongitude = ((searchCoordinateByAddressResponse) endResponse).getDocuments().get(0).getX();
        } else {
            endName = ((SearchKeywordResponse) endResponse).getDocuments().get(0).getPlace_name();
            endAddress = ((SearchKeywordResponse) endResponse).getDocuments().get(0).getRoad_address_name();
            endLatitude = ((SearchKeywordResponse) endResponse).getDocuments().get(0).getY();
            endLongitude = ((SearchKeywordResponse) endResponse).getDocuments().get(0).getX();
        }

        return TwoPointsResponseDto.<List<PlaceResponseDto>>builder()
                .start(CustomPoint.builder()
                        .name(startName)
                        .address(startAddress)
                        .latitude(startLatitude)
                        .longitude(startLongitude)
                        .build())
                .end(CustomPoint.builder()
                        .name(endName)
                        .address(endAddress)
                        .latitude(endLatitude)
                        .longitude(endLongitude)
                        .build())
                .list(placeDto)
                .build();
    }
}
