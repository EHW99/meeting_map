package com.capstone.meetingmap.api.google.dto;

import lombok.Data;

import java.util.List;

@Data
public class GoogleApiResponse<T> {
    private List<String> html_attributions;
    private String next_page_token;
    private List<T> results;
}
