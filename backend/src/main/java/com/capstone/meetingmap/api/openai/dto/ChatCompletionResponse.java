package com.capstone.meetingmap.api.openai.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatCompletionResponse {

    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

    @Data
    @Builder
    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;
    }

    @Data
    @Builder
    public static class Message {
        private String role;    // e.g. "assistant", "user", "system"
        private String content; // 실제 응답 텍스트
    }

    @Data
    @Builder
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }
}
