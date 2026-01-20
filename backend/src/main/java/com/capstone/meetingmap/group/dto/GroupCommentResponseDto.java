package com.capstone.meetingmap.group.dto;

import com.capstone.meetingmap.group.entity.GroupComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GroupCommentResponseDto {
    private Integer groupCommentNo;
    private String userId;
    private String userNick;
    private String groupCommentContent;
    private LocalDateTime groupCommentWriteDate;
    private String userImg;
    private Integer groupBoardNo;

    public static GroupCommentResponseDto fromEntity(GroupComment groupComment) {
        return GroupCommentResponseDto.builder()
                .groupCommentNo(groupComment.getGroupCommentNo())
                .userId(groupComment.getUser().getUserId())
                .userNick(groupComment.getUser().getUserNick())
                .groupCommentContent(groupComment.getGroupCommentContent())
                .groupCommentWriteDate(groupComment.getGroupCommentWriteDate())
                .userImg(groupComment.getUser().getUserImg())
                .groupBoardNo(groupComment.getGroupBoard().getGroupBoardNo())
                .build();
    }
}
