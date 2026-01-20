package com.capstone.meetingmap.api.google.dto;

import lombok.Data;

import java.util.List;

@Data
public class GoogleApiDetailResponse<T> {
    private List<String> html_attributions;
    private T result;
    private String status;
}
