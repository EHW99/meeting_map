package com.capstone.meetingmap.comment.dto;

import com.capstone.meetingmap.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommentResponseDto {
    private Integer commentNo;
    private String userId;
    private String userNick;
    private Integer userType;
    private String userTypeName;
    private String commentContent;
    private LocalDateTime commentWriteDate;
    private String userImg;
    private Integer boardNo;

    public static CommentResponseDto fromEntity(Comment comment) {
        return CommentResponseDto.builder()
                .commentNo(comment.getCommentNo())
                .userId(comment.getUser().getUserId())
                .userNick(comment.getUser().getUserNick())
                .userType(comment.getUser().getUserRole().getUserType())
                .userTypeName(comment.getUser().getUserRole().getUserTypeName())
                .commentContent(comment.getCommentContent())
                .commentWriteDate(comment.getCommentWriteDate())
                .userImg(comment.getUser().getUserImg())
                .boardNo(comment.getBoard().getBoardNo())
                .build();
    }
}
