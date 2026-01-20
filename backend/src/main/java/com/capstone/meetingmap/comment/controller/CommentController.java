package com.capstone.meetingmap.comment.controller;

import com.capstone.meetingmap.comment.dto.CommentRequestDto;
import com.capstone.meetingmap.comment.dto.CommentResponseDto;
import com.capstone.meetingmap.comment.service.CommentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 특정 게시글 댓글 조회
    @GetMapping("/api/boards/{boardNo}/comments")
    public ResponseEntity<?> getComments(@PathVariable Integer boardNo) {
        return ResponseEntity.ok(commentService.searchComments(boardNo));
    }

    // 댓글 작성
    @PostMapping("/api/boards/{boardNo}/comments")
    public ResponseEntity<?> write(@PathVariable Integer boardNo, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        CommentResponseDto commentResponseDto = commentService.write(boardNo, commentRequestDto, userId);

        log.info("Comment added: '{}' [{}] to {} by {}", commentResponseDto.getCommentNo(), commentRequestDto.getCommentContent(), boardNo, userId);

        URI location = URI.create("/api/comments/" + commentResponseDto.getCommentNo());

        return ResponseEntity.created(location).body(commentResponseDto);
    }

    // 댓글 수정
    @PutMapping("/api/comments/{commentNo}")
    public ResponseEntity<?> modify(@PathVariable Integer commentNo, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        commentService.modify(commentNo, commentRequestDto, userId);

        log.info("Comment modified: '{}' [{}] by {}", commentNo, commentRequestDto.getCommentContent(), userId);

        return ResponseEntity.noContent().build();
    }

    // 댓글 삭제
    @DeleteMapping("/api/comments/{commentNo}")
    public ResponseEntity<?> delete(@PathVariable Integer commentNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        commentService.delete(commentNo, userId);

        log.info("Comment deleted: '{}' by {}", commentNo, userId);

        return ResponseEntity.noContent().build();
    }
}
