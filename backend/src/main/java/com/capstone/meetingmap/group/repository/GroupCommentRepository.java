package com.capstone.meetingmap.group.repository;

import com.capstone.meetingmap.group.entity.GroupComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupCommentRepository extends JpaRepository<GroupComment, Integer> {
    boolean existsByGroupCommentNoAndUser_UserIdAndGroupBoardGroupBoardNo(Integer groupCommentNo, String userId, Integer groupBoardNo);
    Optional<GroupComment> findByGroupCommentNoAndUser_UserIdAndGroupBoard_GroupBoardNo(Integer groupCommentNo, String userId, Integer groupBoardNo);
    List<GroupComment> findAllByGroupBoard_GroupBoardNoOrderByGroupCommentWriteDateDesc(Integer groupBoardNo);
}
