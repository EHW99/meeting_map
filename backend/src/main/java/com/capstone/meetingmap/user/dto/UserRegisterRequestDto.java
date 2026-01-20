package com.capstone.meetingmap.user.dto;

import com.capstone.meetingmap.user.entity.User;
import com.capstone.meetingmap.userrole.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRegisterRequestDto {
    @NotBlank(message = "아이디는 반드시 입력해야 합니다.")
    private String userId;

    @NotBlank(message = "이메일은 반드시 입력해야 합니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String userEmail;

    @NotBlank(message = "비밀번호는 반드시 입력해야 합니다.")
    private String userPasswd;

    @NotBlank(message = "닉네임은 반드시 입력해야 합니다.")
    private String userNick;

    private String userAddress;

    @Builder
    public UserRegisterRequestDto(String userId, String userEmail, String userPasswd, String userNick, String userAddress) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPasswd = userPasswd;
        this.userNick = userNick;
        this.userAddress = userAddress;
    }

    //dto를 엔티티로 변환
    public User toEntity(String hashedUserPasswd, UserRole userRole) {
        return User.builder()
                .userId(userId)
                .userEmail(userEmail)
                .userPasswd(hashedUserPasswd)
                .userNick(userNick)
                .userAddress(userAddress)
                .userRole(userRole)
                .onlyFriendsCanSeeActivity(false)
                .emailNotificationAgree(false)
                .pushNotificationAgree(false)
                .build();
    }
}
