package com.capstone.meetingmap.user.dto;

import com.capstone.meetingmap.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdatePasswdRequestDto {
    @NotBlank(message = "변경할 비밀번호는 반드시 입력해야 합니다.")
    private String userPasswd;

    @Builder
    public UserUpdatePasswdRequestDto(String userPasswd) {
        this.userPasswd = userPasswd;
    }

    //dto를 엔티티로 변환
    public User toEntity(String hashedUserPasswd, User user) {
        return User.builder()
                .userId(user.getUserId())
                .userEmail(user.getUserEmail())
                .userPasswd(hashedUserPasswd)
                .userNick(user.getUserNick())
                .userImg(user.getUserImg())
                .userAddress(user.getUserAddress())
                .userRole(user.getUserRole())
                .build();
    }
}
