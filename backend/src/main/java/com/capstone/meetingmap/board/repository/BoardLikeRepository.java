package com.capstone.meetingmap.board.repository;

import com.capstone.meetingmap.board.entity.BoardInteractionId;
import com.capstone.meetingmap.board.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, BoardInteractionId> {
    Optional<BoardLike> findByBoard_BoardNoAndUser_UserId(Integer boardNo, String userId);

    @Query("SELECT bl.board.boardNo FROM BoardLike bl WHERE bl.user.userId = :userId ORDER BY bl.board.boardNo DESC")
    List<Integer> findBoardNosByUserIdOrderByBoardNoDesc(@Param("userId") String userId);

    void deleteByBoard_BoardNoAndUser_UserId(Integer boardNo, String userId);
}
