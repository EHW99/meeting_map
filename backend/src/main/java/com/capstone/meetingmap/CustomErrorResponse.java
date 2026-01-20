package com.capstone.meetingmap;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomErrorResponse {
    String message;

    @Builder
    public CustomErrorResponse(String message) {
        this.message = message;
    }
}
