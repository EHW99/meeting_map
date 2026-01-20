package com.capstone.meetingmap.api.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PointCoord {
    Object doc;
    String lat;
    String lon;

    public static PointCoord fromResponse(Object response) {
        if (response instanceof searchCoordinateByAddressResponse) { // 주소 검색 결과이면
            return PointCoord.builder()
                    .doc(((searchCoordinateByAddressResponse) response).getDocuments().get(0))
                    .lat(((searchCoordinateByAddressResponse) response).getDocuments().get(0).getY())
                    .lon(((searchCoordinateByAddressResponse) response).getDocuments().get(0).getX())
                    .build();
        } else {  // 장소명 검색 결과이면
            return PointCoord.builder()
                    .doc(((SearchKeywordResponse) response).getDocuments().get(0))
                    .lat(((SearchKeywordResponse) response).getDocuments().get(0).getY())
                    .lon(((SearchKeywordResponse) response).getDocuments().get(0).getX())
                    .build();
        }
    }
}
