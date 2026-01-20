package com.capstone.meetingmap.group.entity;

import com.capstone.meetingmap.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"group_no", "user_id"}) })
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer groupMemberNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_no", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public GroupMember(Group group, User user) {
        this.group = group;
        this.user = user;
    }
}
