package com.capstone.meetingmap.board.entity;

import com.capstone.meetingmap.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"user_id", "group_no"}) })
public class BoardScrap extends BoardScrapTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scrapNo;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "board_no", nullable = false)
    private Board board;

    @Builder
    public BoardScrap(User user, Board board) {
        this.user = user;
        this.board = board;
    }
}
