package com.capstone.meetingmap.api.tmap.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tmap.api")
@Getter
@Setter
public class TMapApiProperties {
    private String baseUrl;
    private String key;
}
