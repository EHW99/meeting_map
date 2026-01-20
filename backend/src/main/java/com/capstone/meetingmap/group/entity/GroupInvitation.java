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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"group_no", "receiver_id"}) })
public class GroupInvitation extends GroupInvitationTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer invitationNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_no", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Builder
    public GroupInvitation(Group group, User sender, User receiver, InvitationStatus status) {
        this.group = group;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    public void acceptInvitation() {
        this.status = InvitationStatus.ACCEPTED;
    }

    public void rejectnvitation() {
        this.status = InvitationStatus.REJECTED;
    }
}
