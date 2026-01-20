package com.capstone.meetingmap.group.repository;

import com.capstone.meetingmap.group.entity.GroupInvitation;
import com.capstone.meetingmap.group.entity.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Integer> {
    boolean existsByInvitationNoAndReceiver_UserId(Integer invitationNo, String userId);
    boolean existsByGroup_GroupNoAndSender_UserIdAndReceiver_UserIdAndStatusNot(Integer groupNo, String senderUserId, String receiverUserId, InvitationStatus status);
    List<GroupInvitation> findAllByReceiver_UserId(String userId);
}
