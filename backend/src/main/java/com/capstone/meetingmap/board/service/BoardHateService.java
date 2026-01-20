package com.capstone.meetingmap.board.service;

import com.capstone.meetingmap.board.entity.Board;
import com.capstone.meetingmap.board.entity.BoardHate;
import com.capstone.meetingmap.board.repository.BoardHateRepository;
import com.capstone.meetingmap.board.repository.BoardLikeRepository;
import com.capstone.meetingmap.board.repository.BoardRepository;
import com.capstone.meetingmap.user.entity.User;
import com.capstone.meetingmap.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class BoardHateService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardHateRepository boardHateRepository;
    private final BoardLikeRepository boardLikeRepository;

    public BoardHateService(BoardHateRepository boardHateRepository, BoardLikeRepository boardLikeRepository, BoardRepository boardRepository, UserRepository userRepository) {
        this.boardHateRepository = boardHateRepository;
        this.boardLikeRepository = boardLikeRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void toggleHate(Integer boardNo, String userId) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        Optional<BoardHate> existingHate = boardHateRepository.findByBoard_BoardNoAndUser_UserId(boardNo, userId);

        if (existingHate.isPresent()) {
            // 싫어요가 이미 있으면 제거
            boardHateRepository.delete(existingHate.get());
        } else {
            // 기존 좋아요가 있다면 제거
            boardLikeRepository.deleteByBoard_BoardNoAndUser_UserId(boardNo, userId);

            // 싫어요 추가
            boardHateRepository.save(BoardHate.builder()
                    .board(board)
                    .user(user)
                    .build());
        }
    }
}
