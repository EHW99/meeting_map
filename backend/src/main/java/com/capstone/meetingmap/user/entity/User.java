package com.capstone.meetingmap.user.entity;

import com.capstone.meetingmap.friendship.entity.Friendship;
import com.capstone.meetingmap.user.dto.UserUpdateRequestDto;
import com.capstone.meetingmap.userrole.entity.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`user`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(length = 50)
    private String userId;

    private String userEmail;
    private String userPasswd;
    private String userNick;
    private String userImg;
    private String userAddress;

    @ManyToOne
    @JoinColumn(name = "user_type")
    private UserRole userRole;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean onlyFriendsCanSeeActivity;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean emailNotificationAgree;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean pushNotificationAgree;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Friendship> friendshipList = new ArrayList<>();

    @OneToMany(mappedBy = "opponent", fetch = FetchType.LAZY)
    private List<Friendship> opponentFriendshipList = new ArrayList<>();

    @Builder
    public User(String userId, String userEmail, String userPasswd, String userNick, String userImg, String userAddress, UserRole userRole, boolean onlyFriendsCanSeeActivity, boolean emailNotificationAgree, boolean pushNotificationAgree) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPasswd = userPasswd;
        this.userNick = userNick;
        this.userImg = userImg;
        this.userAddress = userAddress;
        this.userRole = userRole;
        this.onlyFriendsCanSeeActivity = onlyFriendsCanSeeActivity;
        this.emailNotificationAgree = emailNotificationAgree;
        this.pushNotificationAgree = pushNotificationAgree;
    }

    public void updateInfo(UserUpdateRequestDto userUpdateRequestDto) {
        if (userUpdateRequestDto.getUserEmail() != null)
            this.userEmail = userUpdateRequestDto.getUserEmail();
        if (userUpdateRequestDto.getUserNick() != null)
            this.userNick = userUpdateRequestDto.getUserNick();
        if (userUpdateRequestDto.getUserAddress() != null)
            this.userAddress = userUpdateRequestDto.getUserAddress();
        if (userUpdateRequestDto.getOnlyFriendsCanSeeActivity() != null)
            this.onlyFriendsCanSeeActivity = userUpdateRequestDto.getOnlyFriendsCanSeeActivity();
        if (userUpdateRequestDto.getEmailNotificationAgree() != null)
            this.emailNotificationAgree = userUpdateRequestDto.getEmailNotificationAgree();
        if (userUpdateRequestDto.getPushNotificationAgree() != null)
            this.pushNotificationAgree = userUpdateRequestDto.getPushNotificationAgree();
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.userImg = profileImageUrl;
    }
}
