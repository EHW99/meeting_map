package com.capstone.meetingmap.friendship.service;

import com.capstone.meetingmap.friendship.dto.FriendshipResponseDto;
import com.capstone.meetingmap.friendship.dto.FriendshipAddRequestDto;
import com.capstone.meetingmap.friendship.entity.Friendship;
import com.capstone.meetingmap.friendship.entity.FriendshipStatus;
import com.capstone.meetingmap.friendship.repository.FriendshipRepository;
import com.capstone.meetingmap.user.entity.User;
import com.capstone.meetingmap.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendshipService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public FriendshipService(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    //친구 요청
    @Transactional
    public void createFriendship(String userId, FriendshipAddRequestDto friendshipAddRequestDto) {
        User fromUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));
        User toUser = userRepository.findById(friendshipAddRequestDto.getOpponentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        Friendship friendshipFrom = Friendship.builder() //보내는 사람 측
                .user(fromUser)
                .opponent(toUser)
                .status(FriendshipStatus.WAITING)
                .isFrom(true)
                .build();
        Friendship friendshipTo = Friendship.builder() //받는 사람 측
                .user(toUser)
                .opponent(fromUser)
                .status(FriendshipStatus.WAITING)
                .isFrom(false)
                .build();

        //각각의 친구목록에 추가(opponent가 상대방)
        fromUser.getFriendshipList().add(friendshipFrom);
        toUser.getFriendshipList().add(friendshipTo);

        friendshipRepository.save(friendshipFrom);
        friendshipRepository.save(friendshipTo);

        //각각 상대방에 대한 friendshipNo를 저장
        friendshipTo.setCounterpartFriendshipNo(friendshipFrom.getFriendshipNo());
        friendshipFrom.setCounterpartFriendshipNo(friendshipTo.getFriendshipNo());
    }

    //친구 목록 조회
    public List<FriendshipResponseDto> getFriendships(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));

        List<Friendship> friendshipList = user.getFriendshipList();

        return friendshipList.stream()
                .filter(friendship -> friendship.getStatus() == FriendshipStatus.ACCEPTED && user.getUserId().equals(friendship.getUser().getUserId()))
                .map(FriendshipResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    //보낸 친구 요청중 대기 상태 조회
    public List<FriendshipResponseDto> getSentWaitingFriends(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));
        List<Friendship> friendshipList = user.getFriendshipList();
        return friendshipList.stream()
                .filter(friendship -> friendship.getStatus() == FriendshipStatus.WAITING && Boolean.TRUE.equals(friendship.isFrom()))
                .map(FriendshipResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    //받은 친구 요청중 대기 상태 조회
    public List<FriendshipResponseDto> getReceivedWaitingFriends(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다"));
        List<Friendship> friendshipList = user.getFriendshipList();
        return friendshipList.stream()
                .filter(friendship -> friendship.getStatus() == FriendshipStatus.WAITING && Boolean.FALSE.equals(friendship.isFrom()))
                .map(FriendshipResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 친구 요청 수락
    @Transactional
    public void approveFriendshipRequest(String userId, Integer friendshipNo) {

        if (!friendshipRepository.existsByFriendshipNoAndUser_UserIdAndIsFrom(friendshipNo, userId, false))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신이 받은 친구 요청만 수락 가능합니다");

        // 누를 친구 요청과 매칭되는 상대방 친구 요청 둘다 가져옴
        Friendship friendship = friendshipRepository.findById(friendshipNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다"));
        Friendship counterFriendship = friendshipRepository.findById(friendship.getCounterpartFriendshipNo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다"));

        // 둘다 상태를 ACCEPTED로 변경함
        friendship.acceptFriendshipRequest();
        counterFriendship.acceptFriendshipRequest();
    }

    // 친구 삭제
    @Transactional
    public void deleteFriendship(String userId, Integer friendshipNo) {

        if (!friendshipRepository.existsByFriendshipNoAndUser_UserIdAndStatus(friendshipNo, userId, FriendshipStatus.ACCEPTED))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "자신의 친구만 삭제 가능합니다");

        // 나의 친구 관계와 매칭되는 상대방 친구 관계 둘다 가져옴
        Friendship friendship = friendshipRepository.findById(friendshipNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다"));
        Friendship counterFriendship = friendshipRepository.findById(friendship.getCounterpartFriendshipNo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다"));

        // 둘다 삭제
        friendshipRepository.delete(friendship);
        friendshipRepository.delete(counterFriendship);
    }
}
