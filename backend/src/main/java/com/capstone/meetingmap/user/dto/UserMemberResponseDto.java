package com.capstone.meetingmap.user.dto;

import com.capstone.meetingmap.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
// 다른 사람이 조회할 수 있는 범위
public class UserMemberResponseDto {
    private String userId;
    private String userEmail;
    private String userNick;
    private String userImg;
    private String userAddress;
    private Integer userType;

    //엔티티를 dto로 변환
    public static UserMemberResponseDto fromEntity(User user) {
        return UserMemberResponseDto.builder()
                .userId(user.getUserId())
                .userEmail(user.getUserEmail())
                .userNick(user.getUserNick())
                .userImg(user.getUserImg())
                .userAddress(user.getUserAddress())
                .userType(user.getUserRole().getUserType())
                .build();
    }
}
