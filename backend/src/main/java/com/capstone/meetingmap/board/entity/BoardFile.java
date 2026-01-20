package com.capstone.meetingmap.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileNo;

    private String fileName;
    private String fileUrl;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "board_no", nullable = false)
    private Board board;

    @Builder
    public BoardFile(String fileName, String fileUrl, Board board) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.board = board;
    }
}
