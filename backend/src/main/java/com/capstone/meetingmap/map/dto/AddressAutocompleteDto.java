package com.capstone.meetingmap.map.dto;

import com.capstone.meetingmap.api.kakao.dto.SearchKeywordResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AddressAutocompleteDto {
    private String id;
    private String placeName;
    private String address;

    public static List<AddressAutocompleteDto> fromKakaoApiResponse(SearchKeywordResponse response) {
        List<AddressAutocompleteDto> addressAutocompleteDtoList = new ArrayList<>();
        for (SearchKeywordResponse.Documents document : response.getDocuments()) {
            String roadAddress = document.getRoad_address_name();
            if (roadAddress != null && roadAddress.contains("서울")) {
                AddressAutocompleteDto dto = AddressAutocompleteDto.builder()
                        .id(document.getId())
                        .placeName(document.getPlace_name())
                        .address(document.getRoad_address_name())
                        .build();
                addressAutocompleteDtoList.add(dto);
            }
        }
        return addressAutocompleteDtoList;
    }
}
