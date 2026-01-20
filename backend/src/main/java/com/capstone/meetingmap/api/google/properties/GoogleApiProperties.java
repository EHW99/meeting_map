package com.capstone.meetingmap.api.google.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google.api")
@Getter
@Setter
public class GoogleApiProperties {
    private String baseUrl;
    private String key;
}
