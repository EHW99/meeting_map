package com.capstone.meetingmap.group.repository;

import com.capstone.meetingmap.group.entity.Group;
import com.capstone.meetingmap.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {
    boolean existsByGroup_GroupNoAndUser_UserId(Integer groupNo, String userId);

    List<GroupMember> findByGroup_GroupNo(Integer groupNo);

    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user.userId = :userId")
    List<Group> findGroupsByUserId(@Param("userId") String userId);

    void deleteByGroupGroupNoAndUserUserId(Integer groupNo, String userId);
}
