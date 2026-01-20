package com.capstone.meetingmap.group.entity;

import com.capstone.meetingmap.group.dto.GroupBoardRequestDto;
import com.capstone.meetingmap.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupBoard extends GroupBoardTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer groupBoardNo;

    private String groupBoardTitle;

    @Column(columnDefinition = "TEXT")
    private String groupBoardContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupNo", nullable = false)
    private Group group;

    @OneToMany(mappedBy = "groupBoard", cascade = CascadeType.ALL)
    private List<GroupComment> comments;

    @Builder
    public GroupBoard(String groupBoardTitle, String groupBoardContent, User user, Group group, List<GroupComment> comments) {
        this.groupBoardTitle = groupBoardTitle;
        this.groupBoardContent = groupBoardContent;
        this.user = user;
        this.group = group;
        this.comments = comments;
    }

    public void setGroupBoardWithoutGroupBoardNo(GroupBoardRequestDto groupBoardRequestDto) {
        this.groupBoardTitle = groupBoardRequestDto.getGroupBoardTitle();
        this.groupBoardContent = groupBoardRequestDto.getGroupBoardContent();
    }
}
