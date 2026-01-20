package com.capstone.meetingmap.userrole.repository;

import com.capstone.meetingmap.userrole.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
}
