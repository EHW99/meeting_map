package com.capstone.meetingmap.group.dto;

import com.capstone.meetingmap.group.entity.Group;
import com.capstone.meetingmap.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserGroupResponseDto {
    private String userId;
    private String userEmail;
    private String userNick;
    private String userImg;
    private String userAddress;
    private Integer userType;
    private Integer groupNo;
    private String groupTitle;

    //엔티티를 dto로 변환
    public static UserGroupResponseDto fromEntity(User user, Group group) {
        return UserGroupResponseDto.builder()
                .userId(user.getUserId())
                .userEmail(user.getUserEmail())
                .userNick(user.getUserNick())
                .userImg(user.getUserImg())
                .userAddress(user.getUserAddress())
                .userType(user.getUserRole().getUserType())
                .groupNo(group.getGroupNo())
                .groupTitle(group.getGroupTitle())
                .build();
    }
}
