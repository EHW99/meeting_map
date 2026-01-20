package com.capstone.meetingmap.friendship.repository;

import com.capstone.meetingmap.friendship.entity.Friendship;
import com.capstone.meetingmap.friendship.entity.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    boolean existsByFriendshipNoAndUser_UserIdAndIsFrom(Integer friendshipNo, String userId, boolean isFrom);
    boolean existsByUser_UserIdAndOpponent_UserIdAndStatus(String userId, String friendId, FriendshipStatus status);
    boolean existsByFriendshipNoAndUser_UserIdAndStatus(Integer friendshipNo, String userId, FriendshipStatus status);
}
