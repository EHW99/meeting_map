package com.capstone.meetingmap.group.dto;

import com.capstone.meetingmap.group.entity.GroupBoard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GroupBoardResponseDto {
    private Integer groupBoardNo;
    private String groupBoardTitle;
    private String userId;
    private String userNick;
    private Integer groupNo;
    private String groupTitle;
    private LocalDateTime groupBoardWriteDate;
    private LocalDateTime groupBoardUpdateDate;
    private Integer commentCount;

    public static GroupBoardResponseDto fromEntity(GroupBoard groupBoard) {
        return GroupBoardResponseDto.builder()
                .groupBoardNo(groupBoard.getGroupBoardNo())
                .groupBoardTitle(groupBoard.getGroupBoardTitle())
                .userId(groupBoard.getUser().getUserId())
                .userNick(groupBoard.getUser().getUserNick())
                .groupNo(groupBoard.getGroup().getGroupNo())
                .groupTitle(groupBoard.getGroup().getGroupTitle())
                .groupBoardWriteDate(groupBoard.getGroupBoardWriteDate())
                .groupBoardUpdateDate(groupBoard.getGroupBoardUpdateDate())
                .commentCount(groupBoard.getComments().size())
                .build();
    }
}
