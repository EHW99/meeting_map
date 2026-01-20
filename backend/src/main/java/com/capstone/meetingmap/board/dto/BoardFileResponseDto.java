package com.capstone.meetingmap.board.dto;

import com.capstone.meetingmap.board.entity.BoardFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BoardFileResponseDto {
    private Integer fileNo;
    private String fileName;
    private String fileUrl;

    public static BoardFileResponseDto fromEntity(BoardFile boardFile) {
        return BoardFileResponseDto.builder()
                .fileNo(boardFile.getFileNo())
                .fileName(boardFile.getFileName())
                .fileUrl(boardFile.getFileUrl())
                .build();
    }
}
