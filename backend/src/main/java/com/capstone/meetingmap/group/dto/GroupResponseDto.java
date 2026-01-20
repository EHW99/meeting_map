package com.capstone.meetingmap.group.dto;

import com.capstone.meetingmap.group.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GroupResponseDto {
    private Integer groupNo;
    private String groupTitle;
    private String groupDescription;
    private LocalDateTime groupCreateDate;
    private String groupCreatedUserId;

    public static GroupResponseDto fromEntity(Group group) {
        return GroupResponseDto.builder()
                .groupNo(group.getGroupNo())
                .groupTitle(group.getGroupTitle())
                .groupDescription(group.getGroupDescription())
                .groupCreateDate(group.getGroupCreatedDate())
                .groupCreatedUserId(group.getGroupCreatedUser().getUserId())
                .build();
    }
}
