package com.capstone.meetingmap.board.controller;

import com.capstone.meetingmap.board.dto.*;
import com.capstone.meetingmap.board.service.BoardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private static final Logger log = LoggerFactory.getLogger(BoardController.class);
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 전체/카테고리별 게시글 보기
    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam(value = "category", required = false) Integer categoryNo,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(defaultValue = "boardWriteDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(boardService.searchArticles(categoryNo, keyword, pageable));
    }

    // 카테고리 조회
    @GetMapping("/category")
    public ResponseEntity<?> getCategory() {
        return ResponseEntity.ok(boardService.getCategories());
    }

    // 게시글 상세보기
    @GetMapping("/{boardNo}")
    public ResponseEntity<?> viewDetail(@PathVariable("boardNo") Integer boardNo) {
        return ResponseEntity.ok(boardService.searchByBoardNo(boardNo));
    }

    // 게시글 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> write(
            @RequestPart("json") @Valid BoardRequestDto boardRequestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Integer boardNo = boardService.write(boardRequestDto, files, userId);

        log.info("Article added: '{}' [{}] by {}", boardNo, boardRequestDto.getBoardTitle(), userId);

        URI location = URI.create("/api/boards/" + boardNo);

        return ResponseEntity.created(location).build();
    }

    // 게시글 수정
    @PutMapping(value = "/{boardNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> modify(
            @PathVariable Integer boardNo,
            @RequestPart(value = "json", required = false) @Valid BoardRequestDto boardRequestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "deleteFileNos", required = false) List<Integer> deleteFileNos
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        boardService.modify(boardNo, boardRequestDto, files, deleteFileNos, userId);

        log.info("Article modified: '{}' [{}] by {}", boardNo, boardRequestDto.getBoardTitle(), userId);

        return ResponseEntity.noContent().build();
    }

    // 게시글 삭제
    @DeleteMapping("/{boardNo}")
    public ResponseEntity<?> delete(@PathVariable Integer boardNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        boardService.delete(boardNo, userId);

        log.info("Article deleted: '{}' by {}", boardNo, userId);

        return ResponseEntity.noContent().build();
    }

    // 저장(스크랩) 토글
    @PostMapping("/{boardNo}/scrap")
    public ResponseEntity<?> scrap(@PathVariable Integer boardNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        boardService.scrap(boardNo, userId);

        return ResponseEntity.noContent().build();
    }
}
