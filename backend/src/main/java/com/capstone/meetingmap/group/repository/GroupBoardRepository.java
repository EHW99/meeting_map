package com.capstone.meetingmap.group.repository;

import com.capstone.meetingmap.group.entity.GroupBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupBoardRepository extends JpaRepository<GroupBoard, Integer> {
    boolean existsByGroupBoardNoAndUser_UserId(Integer groupBoardNo, String userId);
    Page<GroupBoard> findAllByGroup_GroupNo(Integer groupNo, Pageable pageable);
    Page<GroupBoard> findAllByUser_UserId(String userId, Pageable pageable);
    Optional<GroupBoard> findByGroupBoardNoAndGroup_GroupNo(Integer groupBoardNo, Integer groupNo);
    Optional<GroupBoard> findByGroupBoardNoAndUser_UserIdAndGroup_GroupNo(Integer groupBoardNo, String userId, Integer groupNo);
    Page<GroupBoard> findAllByGroup_GroupNoAndGroupBoardTitleContainingIgnoreCase(Integer groupNo, String keyword, Pageable pageable);
}
