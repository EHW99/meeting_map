package com.capstone.meetingmap.user.repository;

import com.capstone.meetingmap.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserNickAndUserEmail(String userNick, String userEmail);
}
