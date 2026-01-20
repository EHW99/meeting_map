package com.capstone.meetingmap.group.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GroupInvitationRequestDto {
    @NotBlank(message = "초대받을 회원 닉네임은 반드시 입력해야 합니다")
    private String userNick;

    @NotBlank(message = "초대받을 회원 이메일은 반드시 입력해야 합니다")
    private String userEmail;

    @NotBlank(message = "초대받을 그룹명은 반드시 입력해야 합니다")
    private String groupTitle;
}
