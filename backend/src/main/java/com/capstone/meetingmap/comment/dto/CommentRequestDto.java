package com.capstone.meetingmap.comment.dto;

import com.capstone.meetingmap.board.entity.Board;
import com.capstone.meetingmap.comment.entity.Comment;
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
public class CommentRequestDto {
    @NotBlank(message = "댓글 내용은 반드시 입력해야 합니다")
    private String commentContent;

    public Comment toEntity(User user, Board board) {
        return Comment.builder()
                .commentContent(this.commentContent)
                .user(user)
                .board(board)
                .build();
    }
}
