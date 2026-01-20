package com.capstone.meetingmap.group.controller;

import com.capstone.meetingmap.group.dto.GroupCommentRequestDto;
import com.capstone.meetingmap.group.dto.GroupCommentResponseDto;
import com.capstone.meetingmap.group.service.GroupCommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/groups/{groupNo}/boards/{groupBoardNo}/comments")
public class GroupCommentController {
    private final GroupCommentService groupCommentService;

    public GroupCommentController(GroupCommentService groupCommentService) {
        this.groupCommentService = groupCommentService;
    }

    // 특정 게시글 댓글 조회
    @GetMapping
    public ResponseEntity<?> getComments(@PathVariable Integer groupNo, @PathVariable Integer groupBoardNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(groupCommentService.searchComments(groupNo, groupBoardNo, userId));
    }

    // 그룹 게시글 댓글 등록
    @PostMapping
    public ResponseEntity<?> write(@PathVariable Integer groupNo, @PathVariable Integer groupBoardNo, @Valid @RequestBody GroupCommentRequestDto groupCommentRequestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        GroupCommentResponseDto groupCommentResponseDto = groupCommentService.write(groupNo, groupBoardNo, groupCommentRequestDto, userId);

        URI location = URI.create("/api/groups/" + groupNo + "/boards/" + groupBoardNo + "/comments/" + groupCommentResponseDto.getGroupCommentNo());

        return ResponseEntity.created(location).body(groupCommentResponseDto);
    }

    // 그룹 게시글 댓글 수정
    @PutMapping("/{groupCommentNo}")
    public ResponseEntity<?> modify(@PathVariable Integer groupNo,
                                    @PathVariable Integer groupBoardNo,
                                    @PathVariable Integer groupCommentNo,
                                    @Valid @RequestBody GroupCommentRequestDto groupCommentRequestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        groupCommentService.modify(groupNo, groupBoardNo, groupCommentNo, groupCommentRequestDto, userId);

        return ResponseEntity.noContent().build();
    }

    // 그룹 게시글 댓글 삭제
    @DeleteMapping("/{groupCommentNo}")
    public ResponseEntity<?> delete(@PathVariable Integer groupNo, @PathVariable Integer groupBoardNo, @PathVariable Integer groupCommentNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        groupCommentService.delete(groupNo, groupBoardNo, groupCommentNo, userId);

        return ResponseEntity.noContent().build();
    }
}
