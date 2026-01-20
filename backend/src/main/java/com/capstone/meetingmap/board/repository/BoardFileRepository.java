package com.capstone.meetingmap.board.repository;

import com.capstone.meetingmap.board.entity.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardFileRepository extends JpaRepository<BoardFile, Integer> {
    Optional<BoardFile> findFirstByBoardBoardNoOrderByFileNoAsc(Integer boardNo);
    List<BoardFile> findAllByBoard_BoardNo(Integer boardNo);
}
