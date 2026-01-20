package com.capstone.meetingmap.board.dto;

import com.capstone.meetingmap.board.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CategoryResponseDto {
    private Integer categoryNo;
    private String categoryName;

    //엔티티를 dto로 변환
    public static CategoryResponseDto fromEntity(Category category) {
        return CategoryResponseDto.builder()
                .categoryNo(category.getCategoryNo())
                .categoryName(category.getCategoryName())
                .build();
    }
}
