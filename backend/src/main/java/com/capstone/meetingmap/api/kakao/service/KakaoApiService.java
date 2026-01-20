package com.capstone.meetingmap.api.kakao.service;

import com.capstone.meetingmap.api.kakao.dto.SearchKeywordResponse;
import com.capstone.meetingmap.api.kakao.dto.PointCoord;
import com.capstone.meetingmap.api.kakao.dto.searchCoordinateByAddressResponse;
import com.capstone.meetingmap.api.kakao.dto.searchAddressByCoordinateResponse;
import com.capstone.meetingmap.util.AddressUtil;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class KakaoApiService {
    private final RestClient kakaoRestClient;

    public KakaoApiService(@Qualifier("kakaoRestClient") RestClient kakaoRestClient) {
        this.kakaoRestClient = kakaoRestClient;
    }

    // 키워드로 장소 검색 api 호출
    public SearchKeywordResponse searchKeyword(String query) {
        return kakaoRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/keyword.json")
                        .queryParam("query", query)
                        .queryParam("size", 10)
                        .build())
                .retrieve()
                .body(SearchKeywordResponse.class);
    }

    // 주소를 좌표로 변환하는 카카오 api 호출
    public searchCoordinateByAddressResponse searchCoordinateByAddress(String address) {
        return kakaoRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/address.json")
                        .queryParam("query", address)
                        .build())
                .retrieve()
                .body(searchCoordinateByAddressResponse.class);
    }

    // 좌표를 주소로 변환하는 카카오 api 요청
    public searchAddressByCoordinateResponse searchAddressByCoordinate(double x, double y) {
        return kakaoRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/geo/coord2address.json")
                        .queryParam("x", String.valueOf(x))
                        .queryParam("y", String.valueOf(y))
                        .build())
                .retrieve()
                .body(searchAddressByCoordinateResponse.class);
    }

    // 좌표를 주소로 변환하는 카카오 api 요청
    public String getAddressFromLocation(double longitude, double latitude) {
        searchAddressByCoordinateResponse response = searchAddressByCoordinate(longitude, latitude);

        return (response.getDocuments().get(0).getRoad_address() != null
                ? response.getDocuments().get(0).getRoad_address().getAddress_name()
                : response.getDocuments().get(0).getAddress().getAddress_name());
    }

    // search=middle-point에 대한 알고리즘을 사용하기 위한 변환
    public List<Coordinate> getCoordList(List<String> names) {
        List<Coordinate> coordList = new ArrayList<>();
        // 각 주소에 대해 좌표 검색 후 리스트에 저장
        for (String name : names) {
            PointCoord pointCoord = getPointCoord(name);
            coordList.add(new Coordinate(Double.parseDouble(pointCoord.getLon()), Double.parseDouble(pointCoord.getLat())));
        }
        return coordList;
    }

    // 장소명인지 주소인지 검사해 doc, lat, lon 반환
    public PointCoord getPointCoord(String name) {
        // 주소인지 여부 확인
        boolean isAddress = AddressUtil.isAddressQuery(name);

        // 그에 맞는 API 호출
        Object response = isAddress ? searchCoordinateByAddress(name) : searchKeyword(name);

        return PointCoord.fromResponse(response);
    }
}
