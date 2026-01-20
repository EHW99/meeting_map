package com.capstone.meetingmap.group.dto;

import com.capstone.meetingmap.group.entity.Group;
import com.capstone.meetingmap.group.entity.GroupBoard;
import com.capstone.meetingmap.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GroupBoardRequestDto {
    @NotBlank(message = "게시글 제목은 반드시 입력해야 합니다")
    private String groupBoardTitle;

    @NotBlank(message = "게시글 본문은 반드시 입력해야 합니다")
    private String groupBoardContent;

    //dto를 엔티티로 변환
    public GroupBoard toEntity(User user, Group group) {
        return GroupBoard.builder()
                .groupBoardTitle(groupBoardTitle)
                .groupBoardContent(groupBoardContent)
                .user(user)
                .group(group)
                .build();
    }
}
