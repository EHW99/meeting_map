package com.capstone.meetingmap.board.controller;

import com.capstone.meetingmap.board.service.BoardHateService;
import com.capstone.meetingmap.board.service.BoardLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
public class BoardInteractionController {
    private final BoardLikeService boardLikeService;
    private final BoardHateService boardHateService;

    public BoardInteractionController(BoardLikeService boardLikeService, BoardHateService boardHateService) {
        this.boardLikeService = boardLikeService;
        this.boardHateService = boardHateService;
    }

    // 좋아요 토글
    @PostMapping("/{boardNo}/like")
    public ResponseEntity<?> addLike(@PathVariable Integer boardNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        boardLikeService.toggleLike(boardNo, userId);
        return ResponseEntity.ok().build();
    }

    // 싫어요 토글
    @PostMapping("/{boardNo}/hate")
    public ResponseEntity<?> addHate(@PathVariable Integer boardNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        boardHateService.toggleHate(boardNo, userId);
        return ResponseEntity.ok().build();
    }
}
