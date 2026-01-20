package com.capstone.meetingmap.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserUpdateRequestDto {
    private String userEmail;
    private String userNick;
    private String userAddress;
    private Boolean onlyFriendsCanSeeActivity;
    private Boolean emailNotificationAgree;
    private Boolean pushNotificationAgree;
}
