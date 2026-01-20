package com.capstone.meetingmap.board.dto;

import com.capstone.meetingmap.board.entity.Board;
import com.capstone.meetingmap.schedule.dto.ScheduleResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BoardDetailResponseDto {
    private Integer boardNo;
    private String userId;
    private String userNick;
    private Integer userType;
    private String userTypeName;
    private String boardTitle;
    private String boardDescription;
    private String boardContent;
    private Integer boardViewCount;
    private LocalDateTime boardWriteDate;
    private LocalDateTime boardUpdateDate;
    private Long boardLike;
    private Long boardHate;
    private Integer categoryNo;
    private String categoryName;
    private ScheduleResponseDto schedule;
    private List<BoardFileResponseDto> boardFiles;

    //엔티티를 dto로 변환
    public static BoardDetailResponseDto fromEntity(Board board) {
        return BoardDetailResponseDto.builder()
                .boardNo(board.getBoardNo())
                .userId(board.getUser().getUserId())
                .userNick(board.getUser().getUserNick())
                .userType(board.getUser().getUserRole().getUserType())
                .userTypeName(board.getUser().getUserRole().getUserTypeName())
                .boardTitle(board.getBoardTitle())
                .boardDescription(board.getBoardDescription())
                .boardContent(board.getBoardContent())
                .boardViewCount(board.getBoardViewCount())
                .boardWriteDate(board.getBoardWriteDate())
                .boardUpdateDate(board.getBoardUpdateDate())
                .boardLike((long) board.getLikes().size())
                .boardHate((long) board.getHates().size())
                .categoryNo(board.getCategory().getCategoryNo())
                .categoryName(board.getCategory().getCategoryName())
                .schedule((board.getSchedule() == null ? null : ScheduleResponseDto.fromEntity(board.getSchedule())))
                .boardFiles(board.getBoardFiles().stream().map(BoardFileResponseDto::fromEntity).collect(Collectors.toList()))
                .build();
    }
}
