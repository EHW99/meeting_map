package com.capstone.meetingmap.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BoardScrapView {
    private Integer scrapNo;
    private Integer boardNo;
    private String userId;
    private String userNick;
    private Integer userType;
    private String userTypeName;
    private String boardTitle;
    private String boardDescription;
    private Integer boardViewCount;
    private LocalDateTime boardWriteDate;
    private LocalDateTime boardUpdateDate;
    private Long boardLike;
    private Long boardHate;
    private Integer categoryNo;
    private String categoryName;
    private Long commentCount;
    private String userImg;
    private String thumbnailUrl;

    @Builder
    public BoardScrapView(Integer scrapNo, Integer boardNo, String userId, String userNick, Integer userType,
                          String userTypeName, String boardTitle, String boardDescription, Integer boardViewCount,
                          LocalDateTime boardWriteDate, LocalDateTime boardUpdateDate, Long boardLike, Long boardHate,
                          Integer categoryNo, String categoryName, Long commentCount, String userImg) {
        this.scrapNo = scrapNo;
        this.boardNo = boardNo;
        this.userId = userId;
        this.userNick = userNick;
        this.userType = userType;
        this.userTypeName = userTypeName;
        this.boardTitle = boardTitle;
        this.boardDescription = boardDescription;
        this.boardViewCount = boardViewCount;
        this.boardWriteDate = boardWriteDate;
        this.boardUpdateDate = boardUpdateDate;
        this.boardLike = boardLike;
        this.boardHate = boardHate;
        this.categoryNo = categoryNo;
        this.categoryName = categoryName;
        this.commentCount = commentCount;
        this.userImg = userImg;
    }

    public void addThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
