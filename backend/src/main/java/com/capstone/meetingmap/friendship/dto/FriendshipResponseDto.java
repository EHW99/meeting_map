package com.capstone.meetingmap.friendship.dto;

import com.capstone.meetingmap.friendship.entity.Friendship;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FriendshipResponseDto {
    private Integer friendshipNo;
    private String userId;
    private String opponentId;
    private String opponentNick;
    private String opponentImg;
    private String status;
    private boolean isFrom;
    private Integer counterpartFriendshipNo;

    //엔티티를 dto로 변환
    public static FriendshipResponseDto fromEntity(Friendship friendship) {
        return FriendshipResponseDto.builder()
                .friendshipNo(friendship.getFriendshipNo())
                .userId(friendship.getUser().getUserId())
                .opponentId(friendship.getOpponent().getUserId())
                .opponentNick(friendship.getOpponent().getUserNick())
                .opponentImg(friendship.getOpponent().getUserImg())
                .status(friendship.getStatus().name())
                .isFrom(friendship.isFrom())
                .counterpartFriendshipNo(friendship.getCounterpartFriendshipNo())
                .build();
    }
}
