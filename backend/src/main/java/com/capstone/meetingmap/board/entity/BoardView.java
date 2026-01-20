package com.capstone.meetingmap.board.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Immutable
@Getter
@NoArgsConstructor
public class BoardView {
    @Id
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
}
