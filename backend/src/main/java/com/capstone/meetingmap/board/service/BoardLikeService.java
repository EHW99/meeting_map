package com.capstone.meetingmap.board.service;

import com.capstone.meetingmap.board.entity.Board;
import com.capstone.meetingmap.board.entity.BoardLike;
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
public class BoardLikeService {
    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardHateRepository boardHateRepository;

    public BoardLikeService(BoardLikeRepository boardLikeRepository, BoardHateRepository boardHateRepository, BoardRepository boardRepository, UserRepository userRepository) {
        this.boardLikeRepository = boardLikeRepository;
        this.boardHateRepository = boardHateRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void toggleLike(Integer boardNo, String userId) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        Optional<BoardLike> existingLike = boardLikeRepository.findByBoard_BoardNoAndUser_UserId(boardNo, userId);

        if (existingLike.isPresent()) {
            // 좋아요가 이미 있으면 제거
            boardLikeRepository.delete(existingLike.get());
        } else {
            // 기존 싫어요가 있다면 제거
            boardHateRepository.deleteByBoard_BoardNoAndUser_UserId(boardNo, userId);

            // 좋아요 추가
            boardLikeRepository.save(BoardLike.builder()
                    .board(board)
                    .user(user)
                    .build());
        }
    }
}
