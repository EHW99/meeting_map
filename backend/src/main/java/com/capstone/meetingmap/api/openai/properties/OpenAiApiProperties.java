package com.capstone.meetingmap.api.openai.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai.api")
@Getter
@Setter
public class OpenAiApiProperties {
    private String baseUrl;
    private String key;
}
