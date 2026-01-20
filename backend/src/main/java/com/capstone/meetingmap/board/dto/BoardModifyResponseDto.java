package com.capstone.meetingmap.board.dto;

import com.capstone.meetingmap.board.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BoardModifyResponseDto {
    private String boardTitle;
    private String userId;

    //엔티티를 dto로 변환
    public static BoardModifyResponseDto fromEntity(Board board) {
        return BoardModifyResponseDto.builder()
                .boardTitle(board.getBoardTitle())
                .userId(board.getUser().getUserId())
                .build();
    }
}
