package com.capstone.meetingmap.group.service;

import com.capstone.meetingmap.friendship.entity.FriendshipStatus;
import com.capstone.meetingmap.friendship.repository.FriendshipRepository;
import com.capstone.meetingmap.group.dto.*;
import com.capstone.meetingmap.group.entity.*;
import com.capstone.meetingmap.group.repository.GroupInvitationRepository;
import com.capstone.meetingmap.group.repository.GroupMemberRepository;
import com.capstone.meetingmap.group.repository.GroupRepository;
import com.capstone.meetingmap.group.repository.GroupScheduleRepository;
import com.capstone.meetingmap.schedule.dto.ScheduleResponseDto;
import com.capstone.meetingmap.schedule.dto.ScheduleShareRequestDto;
import com.capstone.meetingmap.schedule.entity.Schedule;
import com.capstone.meetingmap.schedule.repository.ScheduleRepository;
import com.capstone.meetingmap.user.dto.UserMemberResponseDto;
import com.capstone.meetingmap.user.entity.User;
import com.capstone.meetingmap.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final FriendshipRepository friendshipRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final GroupScheduleRepository groupScheduleRepository;
    private final ScheduleRepository scheduleRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, GroupMemberRepository groupMemberRepository, FriendshipRepository friendshipRepository, GroupInvitationRepository groupInvitationRepository, GroupScheduleRepository groupScheduleRepository, ScheduleRepository scheduleRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.friendshipRepository = friendshipRepository;
        this.groupInvitationRepository = groupInvitationRepository;
        this.groupScheduleRepository = groupScheduleRepository;
        this.scheduleRepository = scheduleRepository;
    }

    // 그룹 조회
    public GroupResponseDto getGroup(Integer groupNo, String userId) {
        // 자신이 속한 group만 조회 가능
        if (!groupMemberRepository.existsByGroup_GroupNoAndUser_UserId(groupNo, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 속한 그룹만 조회 가능합니다");
        }
        Group group = groupRepository.findById(groupNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "그룹을 찾을 수 없습니다"));
        return GroupResponseDto.fromEntity(group);
    }

    // 특정 사용자의 그룹 정보 조회
    public List<GroupResponseDto> getGroupsByUserId(String userId) {
        List<Group> groupList = groupMemberRepository.findGroupsByUserId(userId);
        return groupList.stream()
                .map(GroupResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 그룹 멤버 조회
    public List<UserMemberResponseDto> getGroupMembers(Integer groupNo, String userId) {
        // 자신이 속한 group만 조회 가능
        if (!groupMemberRepository.existsByGroup_GroupNoAndUser_UserId(groupNo, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 속한 그룹만 조회 가능합니다");
        }

        List<GroupMember> groupMemberList = groupMemberRepository.findByGroup_GroupNo(groupNo);

        return groupMemberList.stream()
                .map(groupMember -> UserMemberResponseDto.fromEntity(groupMember.getUser()))
                .collect(Collectors.toList());
    }

    // 소속되어있는 전체 그룹 멤버 조회 (사용자가 속한 그룹의 멤버만)
    public List<UserGroupResponseDto> getAllGroupMembers(String userId) {
        // 사용자가 속한 그룹 목록 조회
        List<Group> userGroups = groupMemberRepository.findGroupsByUserId(userId);

        // 사용자가 속한 그룹들의 멤버만 반환
        return userGroups.stream()
                .flatMap(group -> groupMemberRepository.findByGroup_GroupNo(group.getGroupNo()).stream())
                .map(groupMember -> UserGroupResponseDto.fromEntity(groupMember.getUser(), groupMember.getGroup()))
                .collect(Collectors.toList());
    }

    // 그룹 생성
    @Transactional
    public Integer createGroup(GroupRequestDto groupRequestDto, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        Group group = groupRequestDto.toEntity(user);
        groupRepository.save(group);

        GroupMember groupMember = GroupMember.builder()
                .group(group)
                .user(user)
                .build();
        groupMemberRepository.save(groupMember);

        return group.getGroupNo();
    }

    // 그룹 수정
    @Transactional
    public void updateGroup(Integer groupNo, GroupRequestDto groupRequestDto, String userId) {
        Group group = groupRepository.findByGroupNoAndGroupCreatedUser_UserId(groupNo, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "그룹 정보를 찾을 수 없거나 수정 권한이 없습니다"));

        group.setGroupBasicInfo(groupRequestDto);
    }

    // 그룹 삭제
    @Transactional
    public void deleteGroup(Integer groupNo, String userId) {
        if (!groupRepository.existsByGroupNoAndGroupCreatedUser_UserId(groupNo, userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "그룹 정보를 찾을 수 없거나 삭제 권한이 없습니다");
        }

        groupRepository.deleteById(groupNo); // cascade로 GroupMember, GroupInvitation, GroupBoard, GroupComment까지 전부 같이 삭제
    }

    // 그룹 멤버 강제 탈퇴
    @Transactional
    public void deleteGroupMember(Integer groupNo, String deleteUserId, String userId) {
        if (!groupRepository.existsByGroupNoAndGroupCreatedUser_UserId(groupNo, userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "그룹 정보를 찾을 수 없거나 탈퇴 권한이 없습니다");
        }

        groupMemberRepository.deleteByGroupGroupNoAndUserUserId(groupNo, deleteUserId);
    }

    // 그룹 초대
    @Transactional
    public void inviteGroup(GroupInvitationRequestDto groupInvitationRequestDto, String userId) {

        Group group = groupRepository.findByGroupTitle(groupInvitationRequestDto.getGroupTitle())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "그룹 정보를 찾을 수 없습니다"));

        if (!groupMemberRepository.existsByGroup_GroupNoAndUser_UserId(group.getGroupNo(), userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 속한 그룹만 다른 회원을 초대할 수 있습니다");

        User receiver = userRepository.findByUserNickAndUserEmail(groupInvitationRequestDto.getUserNick(), groupInvitationRequestDto.getUserEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        if (!friendshipRepository.existsByUser_UserIdAndOpponent_UserIdAndStatus(userId, receiver.getUserId(), FriendshipStatus.ACCEPTED)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "친구인 회원만 초대할 수 있습니다");
        }

        if (groupInvitationRepository.existsByGroup_GroupNoAndSender_UserIdAndReceiver_UserIdAndStatusNot(group.getGroupNo(), userId, receiver.getUserId(), InvitationStatus.REJECTED)) {
            throw new IllegalStateException("이미 초대가 이루어진 회원입니다");
        }

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        GroupInvitation groupInvitation = GroupInvitation.builder()
                    .group(group)
                    .sender(sender)
                    .receiver(receiver)
                    .status(InvitationStatus.WAITING)
                    .build();

        groupInvitationRepository.save(groupInvitation);
    }

    // 그룹 초대 목록 조회
    public List<GroupInvitationResponseDto> getInvitations(String userId) {
        List<GroupInvitation> groupInvitationList = groupInvitationRepository.findAllByReceiver_UserId(userId);

        return groupInvitationList.stream().map(GroupInvitationResponseDto::fromEntity).collect(Collectors.toList());
    }

    // 초대 수락/거절
    @Transactional
    public void respondInvitation(Integer invitationNo, String status, String userId) {
        if (!groupInvitationRepository.existsByInvitationNoAndReceiver_UserId(invitationNo, userId))
            throw new IllegalStateException("자신이 받은 그룹 초대만 수락/거절이 가능합니다");

        GroupInvitation groupInvitation = groupInvitationRepository.findById(invitationNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "초대 정보를 찾을 수 없습니다"));

        if (status.toUpperCase().equals(InvitationStatus.ACCEPTED.name())) {
            groupInvitation.acceptInvitation();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

            Group group = groupRepository.findById(groupInvitation.getGroup().getGroupNo())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "그룹을 찾을 수 없습니다"));

            GroupMember groupMember = GroupMember.builder()
                    .group(group)
                    .user(user)
                    .build();
            groupMemberRepository.save(groupMember);
        } else if (status.toUpperCase().equals(InvitationStatus.REJECTED.name())) {
            groupInvitation.rejectnvitation();
        } else {
            throw new IllegalStateException("올바르지 않은 요청입니다");
        }
    }

    // 그룹 내 스케줄 공유
    @Transactional
    public void shareSchedule(Integer groupNo, ScheduleShareRequestDto scheduleShareRequestDto, String userId) {
        if (!scheduleRepository.existsByScheduleNoAndUser_UserId(scheduleShareRequestDto.getScheduleNo(), userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 만든 스케줄만 공유 가능합니다");

        if (!groupMemberRepository.existsByGroup_GroupNoAndUser_UserId(groupNo, userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 속한 그룹만 스케줄을 공유 가능합니다");

        Group group = groupRepository.findById(groupNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "그룹 정보를 찾을 수 없습니다"));

        Schedule schedule = scheduleRepository.findById(scheduleShareRequestDto.getScheduleNo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "스케줄을 찾을 수 없습니다"));

        if (groupScheduleRepository.countByGroup_GroupNo(groupNo) >= 4) {
            throw new IllegalStateException("이 그룹에는 최대 4개의 스케줄만 추가할 수 있습니다.");
        }

        // groupSchedule에 추가가 안되어있으면 추가
        if (!groupScheduleRepository.existsByGroup_GroupNoAndSchedule_ScheduleNo(groupNo, schedule.getScheduleNo())) {
            groupScheduleRepository.save(new GroupSchedule(group, schedule));
        }
    }

    // 그룹 내 공유된 스케줄 조회
    public List<ScheduleResponseDto> getSharedSchedules(Integer groupNo, String userId) {
        if (!groupMemberRepository.existsByGroup_GroupNoAndUser_UserId(groupNo, userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 속한 그룹만 스케줄 조회가 가능합니다");

        List<Schedule> scheduleList = groupScheduleRepository.findSchedulesByGroup_GroupNo(groupNo);

        return scheduleList.stream().map(ScheduleResponseDto::fromEntity).collect(Collectors.toList());
    }

    // 그룹 내 스케줄 삭제
    @Transactional
    public void deleteSchedule(Integer groupNo, Integer scheduleNo, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        Group group = groupRepository.findById(groupNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "그룹 정보를 찾을 수 없습니다"));

        if (user.getUserRole().getUserType() != 0 && !group.getGroupCreatedUser().getUserId().equals(userId)) // 관리자나 그룹 생성자가 아니면
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "그룹장만 스케줄 삭제가 가능합니다");

        groupScheduleRepository.deleteByGroupGroupNoAndScheduleScheduleNo(groupNo, scheduleNo);
    }
}
