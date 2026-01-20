package com.capstone.meetingmap.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDeleteRequestDto {
    @NotBlank(message = "비밀번호는 반드시 입력해야 합니다.")
    private String userPasswd;
}
