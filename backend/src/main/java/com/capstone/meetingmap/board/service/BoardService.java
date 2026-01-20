package com.capstone.meetingmap.board.service;

import com.capstone.meetingmap.board.dto.BoardDetailResponseDto;
import com.capstone.meetingmap.board.dto.BoardRequestDto;
import com.capstone.meetingmap.board.dto.BoardScrapView;
import com.capstone.meetingmap.board.entity.*;
import com.capstone.meetingmap.board.repository.*;
import com.capstone.meetingmap.board.dto.CategoryResponseDto;
import com.capstone.meetingmap.comment.repository.CommentRepository;
import com.capstone.meetingmap.schedule.entity.Schedule;
import com.capstone.meetingmap.schedule.repository.ScheduleRepository;
import com.capstone.meetingmap.user.entity.User;
import com.capstone.meetingmap.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BoardViewRepository boardViewRepository;
    private final CommentRepository commentRepository;
    private final BoardFileService boardFileService;
    private final BoardFileRepository boardFileRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardScrapRepository boardScrapRepository;
    private final ScheduleRepository scheduleRepository;

    public BoardService(CategoryRepository categoryRepository, BoardRepository boardRepository, UserRepository userRepository, BoardViewRepository boardViewRepository, CommentRepository commentRepository, BoardFileService boardFileService, BoardFileRepository boardFileRepository, BoardLikeRepository boardLikeRepository, BoardScrapRepository boardScrapRepository, ScheduleRepository scheduleRepository) {
        this.categoryRepository = categoryRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.boardViewRepository = boardViewRepository;
        this.commentRepository = commentRepository;
        this.boardFileService = boardFileService;
        this.boardFileRepository = boardFileRepository;
        this.boardLikeRepository = boardLikeRepository;
        this.boardScrapRepository = boardScrapRepository;
        this.scheduleRepository = scheduleRepository;
    }

    // 게시글 보기
    public Page<BoardView> searchArticles(Integer categoryNo, String keyword, Pageable pageable) {

        // categoryNo, keyword 파라미터가 모두 있으면 해당 키워드로 검색
        if (categoryNo != null && keyword != null && !keyword.trim().isEmpty()) {
            return boardViewRepository.searchByCategoryAndKeyword(categoryNo, keyword, pageable);
        }

        // categoryNo 파라미터만 있으면 해당 카테고리 목록을 가져오는 메서드 호출
        if (categoryNo != null) {
            categoryRepository.findById(categoryNo) // 실제 존재하는 카테고리인지 검증
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 카테고리가 존재하지 않습니다"));

            return boardViewRepository.findAllByCategoryNo(categoryNo, pageable);
        }

        // keyword 파라미터만 있으면 해당 키워드로 검색
        if (keyword != null && !keyword.trim().isEmpty()) {
            return boardViewRepository.findAllByBoardTitleContainingIgnoreCaseOrBoardDescriptionContainingIgnoreCase(keyword, keyword, pageable);
        }

        // 아무 파라미터도 없으면 전체 목록을 가져오는 메서드 호출
        return boardViewRepository.findAll(pageable);
    }

    // 카테고리 조회
    public List<CategoryResponseDto> getCategories() {
        return categoryRepository.findAll().stream().map(CategoryResponseDto::fromEntity).collect(Collectors.toList());
    }

    // 특정 회원의 게시글 보기
    public List<BoardView> searchArticlesByUser(String userId) {
        return boardViewRepository.findAllByUserIdOrderByBoardUpdateDateDesc(userId);
    }

    // 특정 회원이 좋아요한 게시글 보기
    public List<BoardView> searchArticlesByLiked(String userId) {
        List<Integer> likedBoardNos = boardLikeRepository.findBoardNosByUserIdOrderByBoardNoDesc(userId);
        if (likedBoardNos.isEmpty()) {
            return Collections.emptyList();
        }
        return boardViewRepository.findByBoardNoIn(likedBoardNos);
    }

    // 특정 회원이 저장(스크랩)한 게시글 보기
    public List<BoardScrapView> searchArticlesByScraped(String userId) {
        List<Integer> scrapedBoardNos = boardScrapRepository.findBoardNosByUserId(userId);
        if (scrapedBoardNos.isEmpty()) {
            return Collections.emptyList();
        }
        List<BoardScrapView> viewList = boardScrapRepository.findScrapedBoardViewsByUserIdOrderByBoardUpdateDateDesc(userId);
        for (BoardScrapView scrapView : viewList) {
            Optional<BoardFile> thumbnail = boardFileRepository.findFirstByBoardBoardNoOrderByFileNoAsc(scrapView.getBoardNo());
            thumbnail.ifPresent(file -> scrapView.addThumbnailUrl(file.getFileUrl()));
        }
        return viewList;
    }

    // 게시글 상세보기
    @Transactional
    public BoardDetailResponseDto searchByBoardNo(Integer boardNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다"));

        board.increaseViewCount(); // 조회수 증가

        return BoardDetailResponseDto.fromEntity(board);
    }

    // 게시글 생성
    @Transactional
    public Integer write(BoardRequestDto boardRequestDto, List<MultipartFile> files, String userId) {
        // 실제 존재하는 카테고리 가져오기
        Category category = categoryRepository.findById(boardRequestDto.getCategoryNo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 카테고리가 존재하지 않습니다"));

        // 실제 존재하는 회원 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        Schedule schedule = null;

        if (boardRequestDto.getScheduleNo() != null) {
            // 실제 존재하는 스케줄 가져오기
            schedule = scheduleRepository.findById(boardRequestDto.getScheduleNo())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "스케줄 정보를 찾을 수 없습니다"));
        }

        Board board = boardRepository.save(boardRequestDto.toEntity(category, user, schedule));

        try {
            boardFileService.saveFiles(board, files);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지를 저장하는데 실패했습니다");
        }

        return board.getBoardNo();
    }

    // 게시글 수정
    @Transactional
    public void modify(Integer boardNo, BoardRequestDto boardRequestDto, List<MultipartFile> files, List<Integer> deleteFileNos, String userId) {
        // 실제 존재하는 카테고리 가져오기
        Category category = categoryRepository.findById(boardRequestDto.getCategoryNo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 카테고리가 존재하지 않습니다"));

        Schedule schedule = null;

        if (boardRequestDto.getScheduleNo() != null) {
            // 실제 존재하는 스케줄 가져오기
            schedule = scheduleRepository.findById(boardRequestDto.getScheduleNo())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "스케줄 정보를 찾을 수 없습니다"));
        }

        // 수정하려는 게시글 가져오기(해당 게시글의 작성자여야만 함)
        Board board = boardRepository.findByBoardNoAndUser_UserId(boardNo, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없거나 수정 권한이 없습니다"));

        board.setBoardWithoutBoardNo(boardRequestDto, category, schedule);

        // 기존 파일 삭제
        if (deleteFileNos != null && !deleteFileNos.isEmpty()) {
            List<BoardFile> filesToDelete = boardFileRepository.findAllById(deleteFileNos);
            for (BoardFile file : filesToDelete) {
                if (!file.getBoard().getBoardNo().equals(boardNo)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일이 해당 게시글에 속하지 않습니다");
                }
                boardFileRepository.delete(file);
            }
        }

        // 파일 처리
        if (files != null && !files.isEmpty()) {
            try {
            List<BoardFile> newFiles = boardFileService.saveFiles(board, files);
                // 파일 업로드 후 List<BoardFile> 엔티티 리스트 생성 및 저장
                board.getBoardFiles().addAll(newFiles);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지를 저장하는데 실패했습니다");
            }
        }
        // 자동으로 변경 사항 반영
        //boardRepository.save(board);
    }

    // 게시글 삭제
    @Transactional
    public void delete(Integer boardNo, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        if (user.getUserRole().getUserType() != 0) { // 관리자가 아니면
            // 작성자만 삭제 가능
            if (!boardRepository.existsByBoardNoAndUser_UserId(boardNo, userId))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없거나 삭제 권한이 없습니다");
        }

        // 게시글의 파일 먼저 삭제
        boardFileService.deleteFiles(boardNo);

        // 게시글에 달린 댓글 삭제
        commentRepository.deleteByBoard_BoardNo(boardNo);

        // 게시글 삭제
        boardRepository.deleteById(boardNo);
    }

    // 스크랩 토글
    @Transactional
    public void scrap(Integer boardNo, String userId) {
        Optional<BoardScrap> existingScrap = boardScrapRepository.findByUser_UserIdAndBoard_BoardNo(userId, boardNo);

        if (existingScrap.isPresent()) {
            // 스크랩이 이미 있으면 제거
            boardScrapRepository.delete(existingScrap.get());
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

            Board board = boardRepository.findById(boardNo)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다"));

            BoardScrap boardScrap = BoardScrap.builder()
                    .user(user)
                    .board(board)
                    .build();
            boardScrapRepository.save(boardScrap);
        }
    }
}
