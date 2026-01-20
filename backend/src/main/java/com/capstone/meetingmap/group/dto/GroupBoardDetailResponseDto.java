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
public class GroupBoardDetailResponseDto {
    private Integer groupBoardNo;
    private String groupBoardTitle;
    private String groupBoardContent;
    private String userId;
    private String userNick;
    private Integer groupNo;
    private String groupTitle;
    private LocalDateTime groupBoardWriteDate;
    private LocalDateTime groupBoardUpdateDate;

    public static GroupBoardDetailResponseDto fromEntity(GroupBoard groupBoard) {
        return GroupBoardDetailResponseDto.builder()
                .groupBoardNo(groupBoard.getGroupBoardNo())
                .groupBoardTitle(groupBoard.getGroupBoardTitle())
                .groupBoardContent(groupBoard.getGroupBoardContent())
                .userId(groupBoard.getUser().getUserId())
                .userNick(groupBoard.getUser().getUserNick())
                .groupNo(groupBoard.getGroup().getGroupNo())
                .groupTitle(groupBoard.getGroup().getGroupTitle())
                .groupBoardWriteDate(groupBoard.getGroupBoardWriteDate())
                .groupBoardUpdateDate(groupBoard.getGroupBoardUpdateDate())
                .build();
    }
}
