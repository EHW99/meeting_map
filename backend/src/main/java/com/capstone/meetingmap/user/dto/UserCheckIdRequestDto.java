package com.capstone.meetingmap.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCheckIdRequestDto {
    private String userId;

    @Builder
    public UserCheckIdRequestDto(String userId) {
        this.userId = userId;
    }
}
