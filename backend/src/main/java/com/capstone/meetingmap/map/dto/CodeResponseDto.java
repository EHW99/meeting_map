package com.capstone.meetingmap.map.dto;

import com.capstone.meetingmap.api.tourapi.dto.CodeItem;
import com.capstone.meetingmap.map.entity.PlaceCategory;
import com.capstone.meetingmap.map.entity.PlaceCategoryDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//프론트에 각종 코드들을 전달할 dto
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CodeResponseDto {
    private String code;
    private String name;

    public static CodeResponseDto fromCodeItem(CodeItem item) {
        return CodeResponseDto.builder()
                .code(item.getCode())
                .name(item.getName())
                .build();
    }

    public static CodeResponseDto fromPlaceCategory(PlaceCategory placeCategory) {
        return CodeResponseDto.builder()
                .code(placeCategory.getPlaceCategoryCode())
                .name(placeCategory.getPlaceCategoryName())
                .build();
    }

    public static CodeResponseDto fromPlaceCategoryDetail(PlaceCategoryDetail placeCategoryDetail) {
        return CodeResponseDto.builder()
                .code(placeCategoryDetail.getPlaceCategoryDetailCode())
                .name(placeCategoryDetail.getPlaceCategoryDetailName())
                .build();
    }
}
