package com.capstone.meetingmap.group.dto;

import com.capstone.meetingmap.group.entity.GroupBoard;
import com.capstone.meetingmap.group.entity.GroupComment;
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
public class GroupCommentRequestDto {
    @NotBlank(message = "댓글 내용은 반드시 입력해야 합니다")
    private String groupCommentContent;

    public GroupComment toEntity(User user, GroupBoard groupBoard) {
        return GroupComment.builder()
                .groupCommentContent(this.groupCommentContent)
                .user(user)
                .groupBoard(groupBoard)
                .build();
    }
}
