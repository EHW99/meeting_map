package com.capstone.meetingmap.group.entity;

import com.capstone.meetingmap.group.dto.GroupCommentRequestDto;
import com.capstone.meetingmap.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupComment extends GroupCommentTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer groupCommentNo;

    @Column(columnDefinition = "TEXT")
    private String groupCommentContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_board_no", nullable = false)
    private GroupBoard groupBoard;

    @Builder
    public GroupComment(String groupCommentContent, User user, GroupBoard groupBoard) {
        this.groupCommentContent = groupCommentContent;
        this.user = user;
        this.groupBoard = groupBoard;
    }

    public void setGroupCommentContent(GroupCommentRequestDto groupCommentRequestDto) {
        this.groupCommentContent = groupCommentRequestDto.getGroupCommentContent();
    }
}
