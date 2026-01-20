package com.capstone.meetingmap.board.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
//복합키 구현을 위한 별도의 PK 클래스
public class BoardInteractionId implements Serializable {
    private Integer boardNo;
    private String userId;

    public BoardInteractionId(Integer boardNo, String userId) {
        this.boardNo = boardNo;
        this.userId = userId;
    }
}