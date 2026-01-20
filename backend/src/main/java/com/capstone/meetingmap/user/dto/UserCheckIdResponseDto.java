package com.capstone.meetingmap.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCheckIdResponseDto {
    private boolean isAvailable;

    @Builder
    public UserCheckIdResponseDto(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
