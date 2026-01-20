package com.capstone.meetingmap.comment.service;

import com.capstone.meetingmap.board.entity.Board;
import com.capstone.meetingmap.board.repository.BoardRepository;
import com.capstone.meetingmap.comment.dto.CommentRequestDto;
import com.capstone.meetingmap.comment.dto.CommentResponseDto;
import com.capstone.meetingmap.comment.entity.Comment;
import com.capstone.meetingmap.comment.repository.CommentRepository;
import com.capstone.meetingmap.user.entity.User;
import com.capstone.meetingmap.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public CommentService(BoardRepository boardRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    // 댓글 보기
    public List<CommentResponseDto> searchComments(Integer boardNo) {
        return commentRepository.findAllByBoard_BoardNoOrderByCommentWriteDateDesc(boardNo).stream().map(CommentResponseDto::fromEntity).collect(Collectors.toList());
    }

    // 댓글 작성
    @Transactional
    public CommentResponseDto write(Integer boardNo, CommentRequestDto commentRequestDto, String userId) {
        // 실제 존재하는 회원 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        // 실제 존재하는 게시글 가져오기
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다"));

        Comment comment = commentRequestDto.toEntity(user, board);

        commentRepository.save(comment);

        return CommentResponseDto.fromEntity(comment);
    }

    // 댓글 수정
    @Transactional
    public void modify(Integer commentNo, CommentRequestDto commentRequestDto, String userId) {
        // 수정하려는 댓글 가져오기(해당 댓글의 작성자여야만 함)
        Comment comment = commentRepository.findByCommentNoAndUser_UserId(commentNo, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없거나 수정 권한이 없습니다"));

        comment.setCommentContent(commentRequestDto);
        commentRepository.save(comment);
    }

    // 댓글 삭제
    @Transactional
    public void delete(Integer commentNo, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));
        if (user.getUserRole().getUserType() != 0) { // 관리자가 아니면
            // 작성자만 삭제 가능
            if (!commentRepository.existsByCommentNoAndUser_UserId(commentNo, userId))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없거나 삭제 권한이 없습니다");
        }
        commentRepository.deleteById(commentNo);
    }
}
