package com.capstone.meetingmap.group.service;

import com.capstone.meetingmap.group.dto.GroupCommentRequestDto;
import com.capstone.meetingmap.group.dto.GroupCommentResponseDto;
import com.capstone.meetingmap.group.entity.Group;
import com.capstone.meetingmap.group.entity.GroupBoard;
import com.capstone.meetingmap.group.entity.GroupComment;
import com.capstone.meetingmap.group.repository.GroupBoardRepository;
import com.capstone.meetingmap.group.repository.GroupCommentRepository;
import com.capstone.meetingmap.group.repository.GroupMemberRepository;
import com.capstone.meetingmap.group.repository.GroupRepository;
import com.capstone.meetingmap.user.entity.User;
import com.capstone.meetingmap.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupCommentService {

    private final GroupCommentRepository groupCommentRepository;
    private final UserRepository userRepository;
    private final GroupBoardRepository groupBoardRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;

    public GroupCommentService(GroupCommentRepository groupCommentRepository, UserRepository userRepository, GroupBoardRepository groupBoardRepository, GroupMemberRepository groupMemberRepository, GroupRepository groupRepository) {
        this.groupCommentRepository = groupCommentRepository;
        this.userRepository = userRepository;
        this.groupBoardRepository = groupBoardRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupRepository = groupRepository;
    }

    // 댓글 보기
    public List<GroupCommentResponseDto> searchComments(Integer groupNo, Integer groupBoardNo, String userId) {
        if (!groupMemberRepository.existsByGroup_GroupNoAndUser_UserId(groupNo, userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 속한 그룹의 글만 조회할 수 있습니다");

        return groupCommentRepository.findAllByGroupBoard_GroupBoardNoOrderByGroupCommentWriteDateDesc(groupBoardNo).stream().map(GroupCommentResponseDto::fromEntity).collect(Collectors.toList());
    }

    // 댓글 작성
    @Transactional
    public GroupCommentResponseDto write(Integer groupNo, Integer groupBoardNo, GroupCommentRequestDto groupCommentRequestDto, String userId) {
        if (!groupMemberRepository.existsByGroup_GroupNoAndUser_UserId(groupNo, userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 속한 그룹의 글에만 댓글을 작성할 수 있습니다");

        // 실제 존재하는 회원 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        // 실제 존재하는 게시글 가져오기
        GroupBoard groupBoard = groupBoardRepository.findById(groupBoardNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다"));

        GroupComment groupComment = groupCommentRequestDto.toEntity(user, groupBoard);

        groupCommentRepository.save(groupComment);

        return GroupCommentResponseDto.fromEntity(groupComment);
    }

    // 댓글 수정
    @Transactional
    public void modify(Integer groupNo, Integer groupBoardNo, Integer groupCommentNo, GroupCommentRequestDto groupCommentRequestDto, String userId) {
        if (!groupMemberRepository.existsByGroup_GroupNoAndUser_UserId(groupNo, userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 속한 그룹의 글에만 댓글을 수정할 수 있습니다");

        // 수정하려는 댓글 가져오기(해당 댓글의 작성자여야만 함)
        GroupComment groupComment = groupCommentRepository.findByGroupCommentNoAndUser_UserIdAndGroupBoard_GroupBoardNo(groupCommentNo, userId, groupBoardNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없거나 수정 권한이 없습니다"));

        groupComment.setGroupCommentContent(groupCommentRequestDto);
        groupCommentRepository.save(groupComment);
    }

    // 댓글 삭제
    @Transactional
    public void delete(Integer groupNo, Integer groupBoardNo, Integer groupCommentNo, String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        Group group = groupRepository.findById(groupNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "그룹 정보를 찾을 수 없습니다"));

        if (user.getUserRole().getUserType() != 0 || !group.getGroupCreatedUser().getUserId().equals(userId)) { // 관리자나 그룹 생성자가 아니면
            if (!groupMemberRepository.existsByGroup_GroupNoAndUser_UserId(groupNo, userId))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 속한 그룹의 글에만 댓글을 삭제할 수 있습니다");

            // 작성자만 삭제 가능
            if (!groupCommentRepository.existsByGroupCommentNoAndUser_UserIdAndGroupBoardGroupBoardNo(groupCommentNo, userId, groupBoardNo))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없거나 삭제 권한이 없습니다");
        }
        groupCommentRepository.deleteById(groupCommentNo);
    }
}
