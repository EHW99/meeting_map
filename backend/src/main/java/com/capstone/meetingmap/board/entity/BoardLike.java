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
public class BoardLike {

    @EmbeddedId
    private BoardInteractionId boardInteractionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("boardNo")  // 복합키 클래스(BoardInteractionId)의 boardNo와 연결
    @JoinColumn(name = "board_no", nullable = false) //게시글번호 외래키
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")  // 복합키 클래스(BoardInteractionId)의 userId와 연결
    @JoinColumn(name = "user_id", nullable = false) //회원아이디 외래키
    private User user;

    @Builder
    public BoardLike(Board board, User user) {
        this.boardInteractionId = new BoardInteractionId(board.getBoardNo(), user.getUserId()); // 복합키 객체 생성
        this.board = board;
        this.user = user;
    }
}
