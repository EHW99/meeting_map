package com.capstone.meetingmap.group.entity;

import com.capstone.meetingmap.group.dto.GroupRequestDto;
import com.capstone.meetingmap.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`group`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group extends GroupTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer groupNo;

    private String groupTitle;

    private String groupDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_created_user_id", nullable = false)
    private User groupCreatedUser;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupInvitation> groupInvitations = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupBoard> groupBoards = new ArrayList<>();

    @Builder
    public Group(String groupTitle, String groupDescription, User groupCreatedUser) {
        this.groupTitle = groupTitle;
        this.groupDescription = groupDescription;
        this.groupCreatedUser = groupCreatedUser;
    }

    public void setGroupBasicInfo(GroupRequestDto groupRequestDto) {
        this.groupTitle = groupRequestDto.getGroupTitle();
        this.groupDescription = groupRequestDto.getGroupDescription();
    }
}
