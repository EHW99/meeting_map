package com.capstone.meetingmap.friendship.controller;

import com.capstone.meetingmap.friendship.dto.FriendshipAddRequestDto;
import com.capstone.meetingmap.friendship.dto.FriendshipApproveRequestDto;
import com.capstone.meetingmap.friendship.dto.FriendshipDeleteRequestDto;
import com.capstone.meetingmap.friendship.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/friends")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    //친구 목록 조회
    @GetMapping
    public ResponseEntity<?> getFriends() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(friendshipService.getFriendships(userId));
    }

    //보낸 친구 요청 조회
    @GetMapping("/sent")
    public ResponseEntity<?> getSentWaitingFriends() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(friendshipService.getSentWaitingFriends(userId));
    }

    //받은 친구 요청 조회
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedWaitingFriends() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(friendshipService.getReceivedWaitingFriends(userId));
    }

    //친구 추가
    @PostMapping("/add")
    public ResponseEntity<?> addFriend(@RequestBody FriendshipAddRequestDto friendshipAddRequestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        friendshipService.createFriendship(userId, friendshipAddRequestDto);
        return ResponseEntity.noContent().build();
    }

    //친구 요청 수락
    @PostMapping("/approve")
    public ResponseEntity<?> approveFriendship(@RequestBody FriendshipApproveRequestDto friendshipApproveRequestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        friendshipService.approveFriendshipRequest(userId, friendshipApproveRequestDto.getFriendshipNo());
        return ResponseEntity.noContent().build();
    }

    // 친구 삭제
    @DeleteMapping
    public ResponseEntity<?> deleteFriendship(@RequestBody FriendshipDeleteRequestDto friendshipDeleteRequestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        friendshipService.deleteFriendship(userId, friendshipDeleteRequestDto.getFriendshipNo());
        return ResponseEntity.noContent().build();
    }
}
