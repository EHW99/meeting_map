package com.capstone.meetingmap.group.repository;

import com.capstone.meetingmap.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Integer> {
    boolean existsByGroupNoAndGroupCreatedUser_UserId(Integer groupNo, String userId);
    Optional<Group> findByGroupTitle(String groupTitle);
    Optional<Group> findByGroupNoAndGroupCreatedUser_UserId(Integer groupNo, String userId);
}
