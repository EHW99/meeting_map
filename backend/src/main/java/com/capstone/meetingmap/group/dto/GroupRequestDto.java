package com.capstone.meetingmap.group.dto;

import com.capstone.meetingmap.group.entity.Group;
import com.capstone.meetingmap.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GroupRequestDto {
    @NotBlank(message = "그룹명은 반드시 입력해야 합니다")
    private String groupTitle;

    @NotBlank(message = "그룹 설명은 반드시 입력해야 합니다")
    private String groupDescription;

    public Group toEntity(User user) {
        return Group.builder()
                .groupTitle(groupTitle)
                .groupDescription(groupDescription)
                .groupCreatedUser(user)
                .build();
    }
}
