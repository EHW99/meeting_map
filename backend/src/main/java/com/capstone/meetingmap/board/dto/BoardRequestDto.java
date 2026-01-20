package com.capstone.meetingmap.board.dto;

import com.capstone.meetingmap.board.entity.Board;
import com.capstone.meetingmap.board.entity.Category;
import com.capstone.meetingmap.schedule.entity.Schedule;
import com.capstone.meetingmap.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BoardRequestDto {
    @NotBlank(message = "게시글 제목은 반드시 입력해야 합니다")
    private String boardTitle;

    @NotBlank(message = "게시글 설명은 반드시 입력해야 합니다")
    private String boardDescription;

    @NotBlank(message = "게시글 본문은 반드시 입력해야 합니다")
    private String boardContent;

    @NotNull(message = "카테고리가 선택되어 있어야 합니다")
    private Integer categoryNo;

    private Integer scheduleNo;

    //dto를 엔티티로 변환
    public Board toEntity(Category category, User user, Schedule schedule) {
        return Board.builder()
                .boardTitle(boardTitle)
                .boardDescription(boardDescription)
                .boardContent(boardContent)
                .category(category)
                .user(user)
                .schedule(schedule)
                .build();
    }
}
