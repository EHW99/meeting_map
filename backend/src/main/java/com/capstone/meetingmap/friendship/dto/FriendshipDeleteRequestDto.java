package com.capstone.meetingmap.friendship.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FriendshipDeleteRequestDto {
    @NotNull(message = "삭제할 친구 관계 번호는 반드시 필요합니다.")
    private Integer friendshipNo;
}
