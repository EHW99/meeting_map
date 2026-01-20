package com.capstone.meetingmap.board.repository;

import com.capstone.meetingmap.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    boolean existsByBoardNoAndUser_UserId(Integer boardNo, String userId);
    Optional<Board> findByBoardNoAndUser_UserId(Integer boardNo, String userId);
}
