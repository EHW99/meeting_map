package com.capstone.meetingmap.group.dto;

import com.capstone.meetingmap.group.entity.GroupInvitation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GroupInvitationResponseDto {
    private Integer invitationNo;
    private Integer groupNo;
    private String groupTitle;
    private String senderId;
    private String senderNick;
    private String receiverId;
    private String status;
    private LocalDateTime invitedDate;

    public static GroupInvitationResponseDto fromEntity(GroupInvitation groupInvitation) {
        return GroupInvitationResponseDto.builder()
                .invitationNo(groupInvitation.getInvitationNo())
                .groupNo(groupInvitation.getGroup().getGroupNo())
                .groupTitle(groupInvitation.getGroup().getGroupTitle())
                .senderId(groupInvitation.getSender().getUserId())
                .senderNick(groupInvitation.getSender().getUserNick())
                .receiverId(groupInvitation.getReceiver().getUserId())
                .status(groupInvitation.getStatus().name())
                .invitedDate(groupInvitation.getInvitedDate())
                .build();
    }
}
