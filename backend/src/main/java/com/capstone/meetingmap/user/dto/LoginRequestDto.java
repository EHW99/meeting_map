package com.capstone.meetingmap.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    private String userId;
    private String userPasswd;

    @Builder
    public LoginRequestDto(String userId, String userPasswd) {
        this.userId = userId;
        this.userPasswd = userPasswd;
    }
}
