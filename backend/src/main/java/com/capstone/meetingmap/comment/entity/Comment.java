package com.capstone.meetingmap.comment.entity;

import com.capstone.meetingmap.board.entity.Board;
import com.capstone.meetingmap.comment.dto.CommentRequestDto;
import com.capstone.meetingmap.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends CommentTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentNo;

    @Column(columnDefinition = "TEXT")
    private String commentContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_no", nullable = false)
    private Board board;

    @Builder
    public Comment(String commentContent, User user, Board board) {
        this.commentContent = commentContent;
        this.user = user;
        this.board = board;
    }

    public void setCommentContent(CommentRequestDto commentRequestDto) {
        this.commentContent = commentRequestDto.getCommentContent();
    }
}
