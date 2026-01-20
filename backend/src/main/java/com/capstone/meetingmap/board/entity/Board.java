package com.capstone.meetingmap.board.entity;

import com.capstone.meetingmap.board.dto.BoardRequestDto;
import com.capstone.meetingmap.schedule.entity.Schedule;
import com.capstone.meetingmap.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BoardTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer boardNo; //게시글 번호

    private String boardTitle; //게시글 제목
    private String boardDescription; // 게시글 설명

    @Column(columnDefinition = "TEXT") //VARCHAR가 아닌 TEXT로 매핑
    private String boardContent; //게시글 내용

    private Integer boardViewCount = 0; //게시글 조회수

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) //회원아이디 외래키
    private User user;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "category_no", nullable = false) //카테고리 번호 외래키
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_no")
    private Schedule schedule;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardLike> likes;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardHate> hates;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardFile> boardFiles = new ArrayList<>();

    @Builder
    public Board(String boardTitle, String boardDescription, String boardContent, User user, Category category, Schedule schedule, List<BoardLike> likes, List<BoardHate> hates) {
        this.boardTitle = boardTitle;
        this.boardDescription = boardDescription;
        this.boardContent = boardContent;
        this.user = user;
        this.category = category;
        this.schedule = schedule;
        this.likes = likes;
        this.hates = hates;
    }

    public void increaseViewCount() {
        this.boardViewCount += 1;
    }

    public void setBoardWithoutBoardNo(BoardRequestDto boardRequestDto, Category category, Schedule schedule) {
        this.boardTitle = boardRequestDto.getBoardTitle();
        this.boardDescription = boardRequestDto.getBoardDescription();
        this.boardContent = boardRequestDto.getBoardContent();
        this.category = category;
        this.schedule = schedule;
    }
}
