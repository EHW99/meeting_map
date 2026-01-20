package com.capstone.meetingmap.comment.repository;

import com.capstone.meetingmap.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    boolean existsByCommentNoAndUser_UserId(Integer commentNo, String userId);
    Optional<Comment> findByCommentNoAndUser_UserId(Integer commentNo, String userId);
    List<Comment> findAllByBoard_BoardNoOrderByCommentWriteDateDesc(Integer commentNo);
    void deleteByBoard_BoardNo(Integer boardNo);
}
