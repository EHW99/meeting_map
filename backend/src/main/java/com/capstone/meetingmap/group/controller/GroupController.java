package com.capstone.meetingmap.group.controller;

import com.capstone.meetingmap.group.dto.*;
import com.capstone.meetingmap.group.service.GroupService;
import com.capstone.meetingmap.schedule.dto.ScheduleResponseDto;
import com.capstone.meetingmap.schedule.dto.ScheduleShareRequestDto;
import com.capstone.meetingmap.user.dto.UserMemberResponseDto;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private static final Logger log = LoggerFactory.getLogger(GroupController.class);

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // 그룹 조회
    @GetMapping("/{groupNo}")
    public ResponseEntity<?> get(@PathVariable Integer groupNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        GroupResponseDto groupResponseDto = groupService.getGroup(groupNo, userId);

        return ResponseEntity.ok(groupResponseDto);
    }

    // 그룹 멤버 조회
    @GetMapping("/{groupNo}/members")
    public ResponseEntity<?> getMembers(@PathVariable Integer groupNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<UserMemberResponseDto> userMemberResponseDtoList = groupService.getGroupMembers(groupNo, userId);

        return ResponseEntity.ok(userMemberResponseDtoList);
    }

    // 소속되어있는 전체 그룹 멤버 조회
    @GetMapping("/members")
    public ResponseEntity<?> getAllMembers() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<UserGroupResponseDto> userResponseDtoList = groupService.getAllGroupMembers(userId);

        return ResponseEntity.ok(userResponseDtoList);
    }

    // 그룹 생성
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody GroupRequestDto groupRequestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Integer groupNo = groupService.createGroup(groupRequestDto, userId);

        URI location = URI.create("/api/groups/" + groupNo);

        return ResponseEntity.created(location).build();
    }

    // 그룹 수정
    @PutMapping("/{groupNo}")
    public ResponseEntity<?> update(@PathVariable Integer groupNo, @Valid @RequestBody GroupRequestDto groupRequestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        groupService.updateGroup(groupNo, groupRequestDto, userId);

        return ResponseEntity.noContent().build();
    }

    // 그룹 삭제
    @DeleteMapping("/{groupNo}")
    public ResponseEntity<?> delete(@PathVariable Integer groupNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        groupService.deleteGroup(groupNo, userId);

        return ResponseEntity.noContent().build();
    }

    // 그룹 멤버 강제 탈퇴
    @DeleteMapping("/{groupNo}/members/{deleteUserId}")
    public ResponseEntity<?> deleteGroupMember(@PathVariable Integer groupNo, @PathVariable String deleteUserId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        groupService.deleteGroupMember(groupNo, deleteUserId, userId);

        return ResponseEntity.noContent().build();
    }

    // 그룹 초대
    @PostMapping("/invitations")
    public ResponseEntity<?> invite(@Valid @RequestBody GroupInvitationRequestDto groupInvitationRequestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        groupService.inviteGroup(groupInvitationRequestDto, userId);

        return ResponseEntity.noContent().build();
    }

    // 그룹 초대 목록 조회
    @GetMapping("/invitations")
    public ResponseEntity<?> getInvitations() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<GroupInvitationResponseDto> groupInvitationResponseDtoList = groupService.getInvitations(userId);

        return ResponseEntity.ok(groupInvitationResponseDtoList);
    }

    // 그룹 초대 수락/거절
    @PostMapping("/invitations/{invitationNo}/{status}")
    public ResponseEntity<?> respond(@PathVariable Integer invitationNo, @PathVariable String status) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        groupService.respondInvitation(invitationNo, status + "ed", userId);

        return ResponseEntity.noContent().build();
    }

    // 그룹 내 스케줄 공유
    @PostMapping("/{groupNo}/schedules")
    public ResponseEntity<?> shareSchedule(@PathVariable Integer groupNo, @Valid @RequestBody ScheduleShareRequestDto scheduleShareRequestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        groupService.shareSchedule(groupNo, scheduleShareRequestDto, userId);

        return ResponseEntity.noContent().build();
    }

    // 그룹 내 공유된 스케줄 조회
    @GetMapping("/{groupNo}/schedules")
    public ResponseEntity<?> getSharedSchedules(@PathVariable Integer groupNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<ScheduleResponseDto> scheduleResponseDtoList = groupService.getSharedSchedules(groupNo, userId);

        return ResponseEntity.ok(scheduleResponseDtoList);
    }

    // 공유 스케줄 삭제
    @DeleteMapping("/{groupNo}/schedules/{scheduleNo}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Integer groupNo, @PathVariable Integer scheduleNo) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        groupService.deleteSchedule(groupNo, scheduleNo, userId);

        return ResponseEntity.noContent().build();
    }
}
