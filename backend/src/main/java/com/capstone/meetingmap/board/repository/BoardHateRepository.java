package com.capstone.meetingmap.board.repository;

import com.capstone.meetingmap.board.entity.BoardHate;
import com.capstone.meetingmap.board.entity.BoardInteractionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardHateRepository extends JpaRepository<BoardHate, BoardInteractionId> {
    Optional<BoardHate> findByBoard_BoardNoAndUser_UserId(Integer boardNo, String userId);
    void deleteByBoard_BoardNoAndUser_UserId(Integer boardNo, String userId);
}
