package com.capstone.meetingmap.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenResponseDto {
    private String accessToken;

    @Builder
    public TokenResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
