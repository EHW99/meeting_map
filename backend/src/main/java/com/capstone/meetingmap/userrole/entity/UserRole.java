package com.capstone.meetingmap.userrole.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole {
    @Id
    private Integer userType;

    private String userTypeName;

    @Builder
    public UserRole(Integer userType, String userTypeName) {
        this.userType = userType;
        this.userTypeName = userTypeName;
    }
}
